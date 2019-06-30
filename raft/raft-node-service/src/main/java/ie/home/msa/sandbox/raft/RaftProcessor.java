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

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static ie.home.msa.sandbox.raft.RaftProcessor.State.*;

@Component
@Slf4j
public class RaftProcessor implements InitializationOperation {

    private Timer timer;
    private final DiscoveryClient client;

    private AtomicReference<State> state;
    private AtomicInteger currentTerm;
    private AtomicInteger commitIdx;
    private AtomicInteger votedFor;
    private List<Entry> logs;

    public List<Entry> getLogs() {
        return logs;
    }

    private int[] nextIdx;
    private int[] matchIdx;

    private AtomicInteger voteCount;

    public AtomicReference<State> getState() {
        return state;
    }

    @Autowired
    public RaftProcessor(DiscoveryClient client) {
        votedFor = new AtomicInteger(-1);
        commitIdx = new AtomicInteger(0);
        voteCount = new AtomicInteger(0);
        state = new AtomicReference<>(Follower);
        currentTerm = new AtomicInteger(0);
        logs = new ArrayList<>();
        this.client = client;
        timer = new Timer(electionRestart(), checkPulse(client));
    }

    private Supplier<Boolean> electionRestart() {
        return () -> {
            currentTerm.incrementAndGet();
            voteCount.set(1);
            votedFor.set(-1);
            timer.reset();
            setState(Follower);
            election();
            return true;
        };
    }

    VoteResult processVoteMessage(RaftRequestVoteMessage message) {
        RequestVote rv = message.getBody();
        int term = rv.getTerm();
        int cId = rv.getCandidateId();
        int lIdx = rv.getLastLogIdx();
        int lTerm = rv.getLastLogTerm();
        boolean flag = false;
        int cT = currentTerm.get();
        if (term > cT) {
            setState(Follower);
            timer.restart();
            currentTerm.set(term);
            votedFor.set(cId);
            flag = true;
        } else if (term == cT) {
            int id = votedFor.get();
            Entry l = last();
            flag = (id == -1 || id == cId) && lIdx >= l.getIdx() && lTerm >= l.getTerm();
            if (flag) {
                votedFor.set(cId);
                timer.reset();
            }
        }

        log.info("coming vote request: {}, ct:{}, vf:{}", rv, cT, votedFor.get());
        return new VoteResult(cT, flag);
    }

    private void election() {
        String[] nodes = client.getNodes();
        String servAddr = client.getServiceAddress();
        String[] addresses = RaftUtils.filter(servAddr, nodes);
        int id = RaftUtils.find(servAddr, nodes);
        Entry last = last();
        int qs = RaftUtils.quorumSize(nodes.length);
        log.info("start election ... {}, qs:{}", Arrays.toString(addresses), qs);

        for (String address : addresses) {
            RaftRequestVoteMessage req = RaftRequestVoteMessageBuilder.build(
                    servAddr, id,
                    currentTerm.get(), last.getIdx(), last.getTerm()
            );
            log.info("build request:{}", req);
            Optional<VoteResult> resp = post(address, "election", req, VoteResult.class);
            if (resp.isPresent()) {
                VoteResult vr = resp.get();
                if (vr.isVote()) {
                    int count = voteCount.incrementAndGet();
                    if (RaftUtils.isQ(count, qs)) {
                        state.set(Leader);
                        timer.stopWatch();
                        votedFor.set(id);
                        int nextLog = logs.size() + 1;
                        for (int i = 0; i < nextIdx.length; i++) {
                            nextIdx[i] = nextLog;
                            matchIdx[i] = 0;
                        }
                        log.info("this node is leader for ct: {}, mI:{},nI:{} ",
                                currentTerm.get(),
                                Arrays.toString(matchIdx),
                                Arrays.toString(nextIdx));
                        break;
                    }
                }
            }
        }
    }

    private Supplier<Boolean> checkPulse(DiscoveryClient client) {
        return () -> {
            String[] nodes = client.getNodes();
            String servAddr = client.getServiceAddress();
            int id = RaftUtils.find(servAddr, nodes);
            for (int i = 0; i < nodes.length; i++) {
                if (i != id) {
                    String address = nodes[i];
                    HeartBeatMessage message = new HeartBeatMessage();
                    message.setLeaderId(id);
                    message.setTerm(currentTerm.get());
                    post(address, "pulse", message, VoteResult.class)
                            .ifPresent(v -> {
                                int term = v.getTerm();
                                if (term > currentTerm.get()) {
                                    setState(Follower);
                                    currentTerm.set(term);
                                    votedFor.set(-1);
                                }
                            });
                }
            }
            return true;
        };
    }

    VoteResult processPulse(HeartBeatMessage message) {
        int leaderId = message.getLeaderId();
        int term = message.getTerm();
        int cT = currentTerm.get();
        if (cT > term) {
            return new VoteResult(cT, false);
        }

        if (state.get() == Leader) timer.restart();
        else timer.reset();

        currentTerm.set(term);
        setState(Follower);
        votedFor.set(leaderId);
        return new VoteResult(term, true);

    }

    VoteResult processAppendMessage(RaftAppendEntriesMessage message) {
        AppendEntries av = message.getBody();
        int term = av.getTerm();
        int lId = av.getLeaderId();
        int cT = currentTerm.get();
        int len = av.getEntries().length;

        int pIdx = av.getPevLogIdx();
        int pTerm = av.getPrevLogTerm();
        Entry l = last();
        boolean logOk = pIdx <= 0 || (pIdx <= l.getIdx() && pTerm == l.getTerm());
        boolean res = false;
        int mIdx = 0;
        if (term < cT) {
        } else if (term > cT) {
            setState(Follower);
            timer.reset();
            currentTerm.set(term);
            votedFor.set(lId);
            res = false;
        } else {
            if (state.get() == Candidate) {
                setState(Follower);
            } else if (state.get() == Follower && logOk) {
            // todo - refactoring with tests(not proper work with change leader with single entry)
                int index = pIdx + 1;

                if (len == 0 || (logs.size() >= index && av.getEntries()[0].getTerm() == last().getTerm())) {
                    commitIdx.set(av.getCommitIdx());
                } else {
                    if (logs.size() >= index) {
                        if (logs.get(index).getTerm() != av.getEntries()[0].getTerm()) {
                            logs = new ArrayList<>(logs.subList(0, index));
                        }
                    }
                    for (Entry entry : av.getEntries()) {
                        add(entry.getCommand(), entry.getTerm());
                    }
                }
                res = true;
                mIdx = av.getPevLogIdx() + av.getEntries().length;

            }
            log.info("com, logok:{}, av:{},ct:{},res:{},last:{}", logOk, av, cT, res, last());
            votedFor.set(lId);
            timer.reset();
        }

        return new VoteResult(term, mIdx, res);
    }

    boolean processCommand(Integer command) {
        String[] nodes = client.getNodes();
        String servAddr = client.getServiceAddress();
        int id = RaftUtils.find(servAddr, nodes);
        int leaderId = votedFor.get();
        if (leaderId != id) {
            if (leaderId >= 0) {
                Optional<Boolean> cmdOpt = post(nodes[leaderId], "command", command, Boolean.class);
                if (cmdOpt.isPresent()) {
                    Boolean res = cmdOpt.get();
                    log.info("message has been redirected to {}, result {}", nodes[leaderId], res);
                    return res;
                } else {
                    log.info("message has not been redirected to {}", nodes[leaderId]);
                    return false;
                }
            } else {
                log.error("lost message {}", command);
                return false;
            }
        } else {
            add(command, currentTerm.get());
            for (int i = 0; i < nodes.length; i++) {
                if (id != i) {
                    String addr = nodes[i];
                    int nxIdx = this.nextIdx[i];
                    int prevLogIdx = nxIdx - 1;
                    int prevLogTerm = prevLogIdx > 1 ? logs.get(prevLogIdx-1).getTerm() : 0;
                    int le = Integer.min(logs.size(), nxIdx);
                    Entry[] entries = logs.subList(nxIdx - 1, le).toArray(new Entry[]{});
                    int min = Integer.min(commitIdx.get(), le);
                    RaftAppendEntriesMessage req =
                            RaftAppendEntriesMessageBuilder.build(
                                    servAddr, currentTerm.get(), id, prevLogIdx, prevLogTerm, min, entries);
                    Optional<VoteResult> rOp = post(addr, "append", req, VoteResult.class);
                    if (rOp.isPresent()) {
                        VoteResult vr = rOp.get();
                        log.info(" send a new com: serv:{}, next:{}, lstEntr:{}, lSize:{}, entrSz:{}, cIdx: {}, res:{}",
                                addr, nxIdx, le, logs.size(), entries.length, commitIdx.get(), vr);
                        if (vr.getTerm() == currentTerm.get()) {
                            if (vr.isVote()) {
                                nextIdx[i] = vr.getmIdx() + 1;
                                matchIdx[i] = vr.getmIdx();
                            } else {
                                nextIdx[i] = Integer.max(nextIdx[i] - 1, 1);
                            }

                        }
                    }
                }
            }
            RaftUtils.findMaxInQs(matchIdx).ifPresent(e -> {
                log.info("change commit idx :{}", e);
                commitIdx.set(e);
            });

        }
        return false;
    }


    private Entry add(int com, int term) {
        int size = logs.size();
        Entry le = new Entry(term, size, com);
        logs.add(le);
        log.info("add new log entry:{}", le);
        return le;
    }

    private <I, O> Optional<O> post(String address, String postfix, I req, Class<O> clazz) {
        try {
            ResponseEntity<O> resp = client.getRestTemplate()
                    .postForEntity("http://" + address + "/"
                            + postfix, req, clazz);
            if (resp.getStatusCode().is2xxSuccessful()) {
                return Optional.ofNullable(resp.getBody());
            }
        } catch (Exception ex) {
            log.error("ex:", ex);
        }
        return Optional.empty();
    }

    private Entry last() {
        return logs.isEmpty() ? new Entry(0, 0, 0) : logs.get(logs.size() - 1);
    }

    @Override
    public Boolean operate() {
        timer.watch();
        timer.leaderWatch();
        String[] nodes = client.getNodes();
        int len = nodes.length;
        matchIdx = new int[len];
        nextIdx = new int[len];

        for (int i = 0; i < matchIdx.length; i++) {
            int idx = last().getIdx();
            matchIdx[i] = idx;
            nextIdx[i] = idx + 1;
        }


        return true;
    }

    private boolean setState(State state) {
        if (this.state.get() != state) {
            log.info("prev state:{}, next state {}", this.state.get(), state);
            this.state.set(state);
            return true;
        }
        return false;
    }


    class Timer {
        private AtomicBoolean turnFlag;
        private final int threshold;
        private final int leaderPause;

        private AtomicInteger countDown;

        private Supplier<Boolean> electionAction;
        private Supplier<Boolean> leaderAction;

        Timer(Supplier<Boolean> action, Supplier<Boolean> leaderAction) {
            Random r = new Random();
            this.threshold = r.nextInt(50) * 10 + 500;// ~ 0.5-1 sec
            this.turnFlag = new AtomicBoolean(false);
            this.countDown = new AtomicInteger(0);
            this.electionAction = action;
            this.leaderAction = leaderAction;
            log.info(" threshold:{},", threshold);
            leaderPause = 100;
        }

        void reset() {
            countDown.set(0);
        }

        void restart() {
            this.reset();
            this.watch();
        }

        void watch() {
            if (turnFlag.compareAndSet(false, true)) {
                CompletableFuture.runAsync(() -> {
                    int step = 30;
                    while (turnFlag.get()) {
                        if (countDown.addAndGet(step) > threshold) {
                            log.info("threshold is over : {} > {}", countDown.get(), threshold);
                            if (electionAction.get()) sleep(step);
                        } else sleep(step);
                    }
                });
            }
        }

        void stopWatch() {
            log.info("stopwatch is turned off");
            turnFlag.set(false);
        }

        private void sleep(int step) {
            try {
                Thread.sleep(step);
            } catch (InterruptedException ignore) {
                Thread.currentThread().interrupt();
            }
        }

        void leaderWatch() {
            CompletableFuture.runAsync(() -> {
                while (true) {
                    if (state.get() == Leader) {
                        leaderAction.get();
                    }
                    sleep(leaderPause);
                }
            });
        }

    }

    public enum State {
        Follower, Candidate, Leader
    }
}
