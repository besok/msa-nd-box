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

import static ie.home.msa.lab.zab.ZabUtils.*;
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
                            } else {
                                int id = state.getId();
                                state.setVote(new ZVote(id, broadcast.getLastZid()));
                            }
                            receiver.sendMessageToOthers();
                        } else if (crR == inR && inV.compareTo(crV) > 0) {
                            state.setVote(inV);
                            receiver.sendMessageToOthers();
                        } else if (inR < crR) {
                            return;
                        }
                        receivedVoteMap.put(mes.getBody().getId(), mes);
                        log.info("after put to map : {} ", receivedVoteMap);
                        if (receiver.nodeSize() == receivedVoteMap.size()) {
                            deduceLeader(state.getVote().getId());
                            return;
                        } else if (checkQuorumThisRec()) {
                            deduceLeader(state.getVote().getId());
                            return;
                        }
                    } else {
                        if (crR == inR) {
                            receivedVoteMap.put(id(mes), mes);
                            if (mes.getStatus() == LEADER) {
                                deduceLeader(voteId(mes));
                                return;
                            } else if (state.getId() == voteId(mes) && checkQuorumRec(vote(mes))) {
                                deduceLeader(state.getId());
                                return;
                            }
                            else if(checkQuorumOut(vote(mes)) && outOfElectionMap.containsKey(voteId(mes))){
                                deduceLeader(voteId(mes));
                            }
                        }
                        outOfElectionMap.put(mes.getBody().getId(), mes);
                        if (voteId(mes) == state.getId() && checkQuorumOut(vote(mes))) {
                            state.setRound(round(mes));
                            deduceLeader(voteId(mes));
                            return;
                        }else if(checkQuorumOut(vote(mes)) && outOfElectionMap.containsKey(voteId(mes))){
                            state.setRound(round(mes));
                            deduceLeader(voteId(mes));
                        }
                    }
                } else {
                    receiver.sendMessageToOthers();
                }
            }


        } else {
            log.info(" election process failed");
        }

    }



    private boolean checkQuorumThisRec() {
        return checkQuorumRec(state.getVote());
    }
    private boolean checkQuorumRec(ZVote vote) {
        int length = receiver.nodes.length;
        log.info("check quorum for {} , {}, {}", receivedVoteMap, vote, length);
        return ZabUtils.checkQuorum(vote, receivedVoteMap, length);
    }
    private boolean checkQuorumOut(ZVote vote) {
        int length = receiver.nodes.length;
        log.info("check quorum for {} , {}, {}", outOfElectionMap, vote, length);
        return ZabUtils.checkQuorum(vote, outOfElectionMap, length);
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
