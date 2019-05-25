package ie.home.msa.lab.zab;

import ie.home.msa.messages.ElectionMessage;
import ie.home.msa.sandbox.discovery.client.InitializationOperation;
import ie.home.msa.zab.ZVote;
import ie.home.msa.zab.Zid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static ie.home.msa.lab.zab.ZabUtils.round;
import static ie.home.msa.lab.zab.ZabUtils.vote;
import static ie.home.msa.zab.ZNodeState.*;

@Service
@Slf4j
public class ElectionProcessor implements InitializationOperation {

    private final NodeState state;
    private final ElectionMessageQueue queue;
    private final ElectionNotificationReceiver receiver;
    private final BroadcastProcessor broadcast;
    private final Map<Integer, ElectionMessage> outOfElectionMap;
    private final Map<Integer, ElectionMessage> receivedVoteMap;

    public ElectionProcessor(NodeState state, ElectionMessageQueue queue, ElectionNotificationReceiver receiver,
                             BroadcastProcessor broadcastProcessor) {
        this.state = state;
        this.queue = queue;
        this.receiver = receiver;
        this.broadcast = broadcastProcessor;
        this.receivedVoteMap = new HashMap<>();
        this.outOfElectionMap = new HashMap<>();
    }

    private void commonThread() {
        log.info(" election process init");

        outOfElectionMap.clear();
        receivedVoteMap.clear();

        if (receiver.electionStateInit(broadcast.getLastZid())) {
            log.info(" election process init");
            while (state.getStatus() == ELECTION) {
                Optional<ElectionMessage> mesOpt = queue.pop();
                if (mesOpt.isPresent()) {
                    ElectionMessage mes = mesOpt.get();
                    log.info(" message is present {}", mes);
                    int crR = state.getRound();
                    int inR = round(mes);
                    ZVote crV = state.getVote();
                    ZVote inV = vote(mes);
                    log.info("precond : crR:{}, inR:{}, crV: {}, inV:{}", crR, inR, crV, inV);
                    if (mes.getStatus() == ELECTION) {
                        if (inR > crR) {
                            state.setRound(inR);
                            receivedVoteMap.clear();
                            if (inV.compareTo(crV) > 0) {
                                state.setVote(inV);
                                log.info("update vote to income {}", inV);
                            } else {
                                int id = state.getId();
                                state.setVote(new ZVote(id, broadcast.getLastZid()));
                                log.info("update vote to current {}", crV);
                            }
                            receiver.sendMessageToOthers();
                        } else if (crR == inR && inV.compareTo(crV) > 0) {
                            state.setVote(inV);
                            log.info("update vote to income {}", inV);
                            receiver.sendMessageToOthers();
                        } else if (inR < crR) {
                            break;
                        }
                        receivedVoteMap.put(mes.getBody().getId(), mes);
                        log.info("after put to map : {} ", receivedVoteMap);
                        if (receiver.nodes.length == receivedVoteMap.size()) {
                            log.info("deduce after reaching ensemle size");
                            deduceLeader(state.getVote().getId());
                            return;
                        } else if (checkQuorumThisReceivedMap()) {
                            log.info("deduce after reaching quorum size");
                            deduceLeader(state.getVote().getId());
                            return;
                        }
                    } else {
                        if (crR == inR) {
                            receivedVoteMap.put(mes.getBody().getId(), mes);
                            if (mes.getStatus() == LEADER) {
                                log.info("deduce after get mes from leader");
                                deduceLeader(mes.getBody().getVote().getId());
                                return;
                            } else if (state.getId() == mes.getBody().getVote().getId() && ZabUtils.checkQuorum(mes.getBody().getVote(), receivedVoteMap, receiver.nodes.length)) {
                                log.info("deduce after quorum for this id");
                                deduceLeader(state.getId());
                                return;
                            }
                            //else if n.vote has a quorum in ReceivedVotes and the voted peer n.vote.id is in  state LEADING and n.vote.id ∈ OutOfElection then
                            // DeduceLeader(n.vote.id); return n.vote
                        }
                        outOfElectionMap.put(mes.getBody().getId(), mes);
                        if (mes.getBody().getVote().getId() == state.getId() && ZabUtils.checkQuorum(mes.getBody().getVote(), outOfElectionMap, receiver.nodes.length)) {
                            state.setRound(mes.getBody().getRound());
                            log.info("deduce after quorum for this id [outofelect]");
                            deduceLeader(mes.getBody().getVote().getId());
                            return;
                        }
                        // else if n.vote has a quorum in OutOfElection and the voted peer n.vote.id is in state LEADING and n.vote.id ∈ OutOfElection then
                        // P.round ← n.round
                        // DeduceLeader(n.vote.id); return n.vote
                    }
                } else {
                    receiver.sendMessageToOthers();
                }
            }


        } else {
            log.info(" election process failed");
        }

    }


    private boolean checkQuorumThisReceivedMap() {
        int length = receiver.nodes.length;
        ZVote vote = state.getVote();
        log.info("check quorum for {} , {}, {}", receivedVoteMap, vote, length);
        return ZabUtils.checkQuorum(vote, receivedVoteMap, length);
    }


    @Override
    public Boolean operate() {
        broadcast.setLastZid(new Zid(1, 0));
        leaderElection();
        return true;
    }

    public void leaderElection() {
        CompletableFuture.runAsync(this::commonThread);
    }



    private void deduceLeader(int id) {
        String addr = receiver.client.getServiceAddress();
        if (ZabUtils.isLeader(id, addr, receiver.nodes)) {
            state.setStatus(LEADER);
        } else {
            state.setStatus(FOLLOWER);
        }
        queue.clear();
        log.info(" deduce leader {} for id {}", state, id);
    }

}
