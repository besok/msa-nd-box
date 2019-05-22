package ie.home.msa.lab.zab;

import ie.home.msa.messages.ElectionMessage;
import ie.home.msa.messages.Message;
import ie.home.msa.sandbox.discovery.client.InitializationOperation;
import ie.home.msa.zab.ZNotification;
import ie.home.msa.zab.ZVote;
import ie.home.msa.zab.Zid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static ie.home.msa.zab.ZNodeState.*;

@Service
@Slf4j
public class ElectionProcessor implements InitializationOperation {

    private static final int THRESHOLD = 4000;
    private static int TIMEOUT = new Random().nextInt(500);

    private final ElectionNotificationReceiver receiver;
    private final BroadcastProcessor broadcast;
    private final Map<Integer, ElectionMessage> outOfElectionMap;
    private final Map<Integer, ElectionMessage> receivedVoteMap;
    private final Lock lock;

    public ElectionProcessor(ElectionNotificationReceiver receiver,
                             BroadcastProcessor broadcastProcessor) {
        this.receiver = receiver;
        this.broadcast = broadcastProcessor;
        this.receivedVoteMap = new HashMap<>();
        this.outOfElectionMap = new HashMap<>();
        this.lock = new ReentrantLock(true);
    }

    private void commonThread() {


        log.info(" election process init");

        threadSleep();
        outOfElectionMap.clear();
        receivedVoteMap.clear();

        if (receiver.electionStateInit(broadcast.getLastZid())) {
            receiver.sendMessageToOthers();
            log.info(" election process init");
            while (receiver.currentMessage.getStatus() == ELECTION) {
                Optional<ElectionMessage> mesOpt = receiver.pop();
                if (mesOpt.isPresent()) {
                    ElectionMessage mes = mesOpt.get();
                    int currentRound = round(receiver.getState());
                    int incomeRound = round(mes);
                    ZVote currentVote = vote(receiver.getState());
                    ZVote incomeVote = vote(mes);
                    if (mes.getStatus() == ELECTION) {
                        if (incomeRound > currentRound) {
                            receiver.getState().getBody().setRound(incomeRound);
                            receivedVoteMap.clear();
                            if (incomeVote.compareTo(currentVote) > 0) {
                                receiver.getState().getBody().setVote(incomeVote);
                            }
                            receiver.sendMessageToOthers();
                        } else if (currentRound == incomeRound && incomeVote.compareTo(currentVote) > 0) {
                            receiver.getState().getBody().setVote(mes.getBody().getVote());
                            receiver.sendMessageToOthers();
                        } else if (incomeRound < currentRound) {
                            break;
                        }
                        receivedVoteMap.put(mes.getBody().getId(), mes);
                        if (receiver.nodes.length == receivedVoteMap.size()) {
                            deduceLeader(vote(mes).getId());
                            return;
                        } else if (checkQuorumThisReceivedMap()) {
                            deduceLeader(id());
                        }
                    } else {
                        if (currentRound == incomeRound) {
                            receivedVoteMap.put(mes.getBody().getId(), mes);
                            if (mes.getStatus() == LEADER) {
                                deduceLeader(mes.getBody().getId());
                                return;
                            } else if (id() == mes.getBody().getVote().getId() && checkQuorum(mes.getBody().getVote(), receivedVoteMap)) {
                                deduceLeader(mes.getBody().getVote().getId());
                                return;
                            }
                            //else if n.vote has a quorum in ReceivedVotes and the voted peer n.vote.id is in  state LEADING and n.vote.id ∈ OutOfElection then
                            // DeduceLeader(n.vote.id); return n.vote
                        }
                        outOfElectionMap.put(mes.getBody().getId(), mes);
                        if (mes.getBody().getVote().getId() == id() && checkQuorum(mes.getBody().getVote(), outOfElectionMap)) {
                            receiver.getState().getBody().setRound(mes.getBody().getRound());
                            deduceLeader(mes.getBody().getVote().getId());
                            return;
                        }
                        // else if n.vote has a quorum in OutOfElection and the voted peer n.vote.id is in state LEADING and n.vote.id ∈ OutOfElection then
                        // P.round ← n.round
                        // DeduceLeader(n.vote.id); return n.vote
                    }
                } else {
                    receiver.sendMessageToOthers();
                    threadSleep();
                }
            }


        } else {
            log.info(" election process failed");
        }

    }

    private void threadSleep() {
        TIMEOUT = ZabUtils.threadSleep(TIMEOUT, THRESHOLD);
        log.info(" thread sleep {}",TIMEOUT);
    }

    private boolean checkQuorumThisReceivedMap() {
        return checkQuorum(receiver.getState().getBody().getVote(), receivedVoteMap);
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

    private int round(ElectionMessage m) {
        return m.getBody().getRound();
    }

    private ZVote vote(ElectionMessage m) {
        return m.getBody().getVote();
    }

    private void deduceLeader(int id) {
        String addr = receiver.client.getServiceAddress();
        if (ZabUtils.isLeader(id, addr, receiver.nodes)) {
            receiver.getState().setStatus(LEADER);
        } else {
            receiver.getState().setStatus(FOLLOWER);
        }
        log.info(" deduce leader {} ", receiver.getState());
    }

    private boolean checkQuorum(ZVote vote, Map<Integer, ElectionMessage> map) {
        int length = receiver.nodes.length;
        int qS = length % 2 == 0 ? length / 2 + 1 : length / 2;
        long size = map.values().stream()
                .map(Message::getBody)
                .map(ZNotification::getVote)
                .filter(vote::equals)
                .count();
        return size >= qS;
    }

    private int id() {
        return receiver.getState().getBody().getId();
    }
}
