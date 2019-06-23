package ie.home.msa.sandbox.raft;


import ie.home.msa.messages.RaftAppendEntriesMessage;
import ie.home.msa.messages.RaftAppendEntriesMessageBuilder;
import ie.home.msa.messages.RaftRequestVoteMessage;
import ie.home.msa.messages.RaftRequestVoteMessageBuilder;
import ie.home.msa.raft.AppendEntries;
import ie.home.msa.raft.Entry;
import ie.home.msa.raft.RequestVote;
import ie.home.msa.sandbox.discovery.client.DiscoveryClient;
import ie.home.msa.sandbox.discovery.client.InitializationOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@Component
@Slf4j
public class Processor implements InitializationOperation {

    private AtomicInteger ownId;
    private Timer timer;
    private final DiscoveryClient client;

    private AtomicInteger commitIdx;
    private AtomicInteger currentTerm;
    private AtomicReference<State> state;
    private AtomicInteger votedFor;
    private List<LogEntry> logs;

    private AtomicInteger voteCount;

    @Autowired
    public Processor(DiscoveryClient client) {
        ownId = new AtomicInteger(-1);
        votedFor = new AtomicInteger(-1);
        commitIdx = new AtomicInteger(0);
        voteCount = new AtomicInteger(0);
        state = new AtomicReference<>(State.Follower);
        currentTerm = new AtomicInteger(0);
        logs = new ArrayList<>();
        logs.add(new LogEntry(0, 0));
        this.client = client;
        timer = new Timer(electionAction(), leaderAction(client));
    }

    private Supplier<Boolean> electionAction() {
        return () -> {
            currentTerm.incrementAndGet();
            voteCount.set(1);
            state.set(State.Follower);
            votedFor.set(-1);
            timer.reset();
            election();
            return true;
        };
    }


    private Supplier<Boolean> leaderAction(DiscoveryClient client) {
        return () -> {
            if (state.get() == State.Leader) {
                String[] nodes = client.getNodes();
                String servAddr = client.getServiceAddress();
                int id = RaftUtils.find(servAddr, nodes);
                String[] addresses = RaftUtils.filter(servAddr, nodes);
                int qs = RaftUtils.quorumSize(nodes.length);
                for (String address : addresses) {
                    Entry[] entries = new Entry[0];
                    RaftAppendEntriesMessage req = RaftAppendEntriesMessageBuilder.build(
                            servAddr, currentTerm.get(), id, 0, 0, 0, entries
                    );
                    log.info("build append request:{}", req);
                    try {
                        ResponseEntity<VoteResult> resp =
                                client.getRestTemplate().postForEntity(
                                        "http://" + address + "/append",
                                        req, VoteResult.class);
                        if (resp.getStatusCode().is2xxSuccessful()) {
                            VoteResult vr = resp.getBody();
                            log.info(" log : {}", vr);
                        }
                    } catch (Exception ex) {
                        log.error("ex:", ex);
                    }
                }
            }
            return true;
        };
    }


    public VoteResult processAppendMessage(RaftAppendEntriesMessage message) {
        AppendEntries av = message.getBody();
        log.info("coming append request {}", av);
        int term = av.getTerm();
        int lId = av.getLeaderId();
        int cT = currentTerm.get();
        if (cT > term) {
            return new VoteResult(term, false);
        } else if (term > cT) {
            setState(State.Follower);
            timer.reset();
            currentTerm.set(term);
            votedFor.set(lId);
            return new VoteResult(term, false);
        } else {
            setState(State.Follower);
            timer.reset();
            votedFor.set(lId);
            return new VoteResult(term, false);
        }

    }

    public VoteResult processVoteMessage(RaftRequestVoteMessage message) {
        RequestVote rv = message.getBody();
        log.info("coming vote request: {}", rv);
        int term = rv.getTerm();
        int cId = rv.getCandidateId();
        int lIdx = rv.getLastLogIdx();
        int lTerm = rv.getLastLogTerm();
        boolean flag;
        int cT = currentTerm.get();
        if (term > cT) {
            setState(State.Follower);
            timer.reset();
            timer.watch();
            currentTerm.set(term);
            votedFor.set(cId);
            flag = true;
        } else {
            int id = votedFor.get();
            LogEntry l = last();
            flag = (id == -1 || id == cId) && lIdx >= l.getIdx() && lTerm >= l.getTerm();
            if(flag){
                votedFor.set(cId);
            }
        }

        log.info(" returned result: flag:{},term:{}", flag, cT);
        return new VoteResult(cT, flag);
    }

    public void election() {
        String[] nodes = client.getNodes();
        String servAddr = client.getServiceAddress();
        String[] addresses = RaftUtils.filter(servAddr, nodes);
        int qs = RaftUtils.quorumSize(nodes.length);
        log.info("start election ... {}, qs:{}", Arrays.toString(addresses), qs);

        common:
        for (String address : addresses) {
            RaftRequestVoteMessage req = RaftRequestVoteMessageBuilder.build(
                    servAddr, RaftUtils.find(servAddr, nodes),
                    currentTerm.get(), last().getIdx(), last().getTerm()
            );
            log.info("build request:{}", req);
            try {
                ResponseEntity<VoteResult> resp =
                        client.getRestTemplate().postForEntity(
                                "http://" + address + "/election",
                                req, VoteResult.class);
                if (resp.getStatusCode().is2xxSuccessful()) {
                    VoteResult vr = resp.getBody();
                    if (vr.isVote()) {
                        int count = voteCount.incrementAndGet();
                        log.info("vote count : {} ", count);
                        if (RaftUtils.isQ(count, qs)) {
                            state.set(State.Leader);
                            timer.stopWatch();
                            votedFor.set(ownId.get());
                            log.info("this node is leader: {} ", currentTerm.get());
                            break common;
                        }
                    }
                }
            } catch (Exception ex) {
                log.error("ex:", ex);
            }
        }
    }

    private LogEntry last() {
        return logs.get(logs.size() - 1);
    }

    @Override
    public Boolean operate() {
        timer.watch();
        ownId.set(RaftUtils.find(client.getServiceAddress(), client.getNodes()));
        timer.leaderWatch();
        return true;
    }


    private void setState(State state) {
        log.info("prev state:{}, next state {}", this.state.get(), state);
        this.state.set(state);
    }


    class Timer {
        private AtomicBoolean turnFlag;
        private final int threshold;
        private final int leaderPause;

        private AtomicInteger timer;

        private Supplier<Boolean> electionAction;
        private Supplier<Boolean> leaderAction;

        Timer(Supplier<Boolean> action, Supplier<Boolean> leaderAction) {
            this.threshold = new Random().nextInt(50) * 10 + 500;
            this.turnFlag = new AtomicBoolean(false);
            this.timer = new AtomicInteger(0);
            this.electionAction = action;
            this.leaderAction = leaderAction;
            log.info(" thershold:{},", threshold);
            leaderPause = 100;
        }

        public void reset() {
            timer.set(0);
        }


        void watch() {
            if (turnFlag.compareAndSet(false, true)) {
                CompletableFuture.runAsync(() -> {
                    int step = 30;
                    while (turnFlag.get()) {
                        if (timer.addAndGet(step) > threshold) {
                            log.info("threshold is over : {} > {}", timer.get(), threshold);
                            if (electionAction.get()) sleep(step);
                        } else sleep(step);
                    }
                });
            }
        }

        void stopWatch() {
            log.info("stopwatch  is turned off");
            turnFlag.set(false);
        }

        private void sleep(int step) {
            try {
                Thread.sleep(step);
            } catch (InterruptedException ignore) {
                Thread.currentThread().interrupt();
            }
        }

        public void leaderWatch() {
            CompletableFuture.runAsync(() -> {
                while (true) {
                    if (leaderAction.get()) {
                        sleep(leaderPause);
                    }
                }
            });
        }

    }
}
