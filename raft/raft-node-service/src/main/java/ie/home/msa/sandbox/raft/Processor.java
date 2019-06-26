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
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Component
@Slf4j
public class Processor implements InitializationOperation {

    private Timer timer;
    private final DiscoveryClient client;

    private AtomicInteger commitIdx;
    private AtomicInteger currentTerm;
    private AtomicReference<State> state;
    private AtomicInteger votedFor;
    private List<LogEntry> logs;

    private int[] nextIdx;
    private int[] matchIdx;


    private AtomicInteger voteCount;

    @Autowired
    public Processor(DiscoveryClient client) {
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
            votedFor.set(-1);
            timer.reset();
            setState(State.Follower);
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
                for (String address : addresses) {
                    Entry[] entries = new Entry[0];
                    RaftAppendEntriesMessage req =
                            RaftAppendEntriesMessageBuilder.build(
                                    servAddr, currentTerm.get(), id, 0, 0, 0, entries);
                    log.info("build append request:{}", req);
                    post(address, "append", req, VoteResult.class).ifPresent(vr -> log.info(" log : {}", vr));
                }
            }
            return true;
        }

                ;
    }


    public VoteResult processAppendMessage(RaftAppendEntriesMessage message) {
        AppendEntries av = message.getBody();
        int term = av.getTerm();
        int lId = av.getLeaderId();
        int cT = currentTerm.get();

        int pIdx = av.getPevLogIdx();
        int pTerm = av.getPrevLogTerm();
        LogEntry l = last();
        boolean logOk = pIdx <= 0 || (pIdx <= l.getIdx() && pTerm == l.getTerm());

        boolean res = false;
        if (term < cT) {

        } else if (term > cT) {
            setState(State.Follower);
            timer.reset();
            currentTerm.set(term);
            votedFor.set(lId);
            res = true;
        } else {
            if (state.get() == State.Candidate) {
                setState(State.Follower);
            } else if (state.get() == State.Follower && logOk) {
                int index = ++pIdx;

                int len = av.getEntries().length;
                if (len == 0 || (logs.size() >= index && av.getEntries()[0].getTerm() == last().getTerm())) {
                    commitIdx.set(av.getCommitIdx());
                } else {
                    if (logs.size() >= index) {
                        if (logs.get(index).getTerm() != av.getEntries()[0].getTerm()) {
                            logs = new ArrayList<>(logs.subList(0, index));
                        }
                        logs.addAll(Stream.of(av.getEntries())
                                .map(e -> new LogEntry(e.getTerm(), e.getIdx(), e.getCommand()))
                                .collect(toList()));
                    }
                }

                res = true;

            }
            votedFor.set(lId);
            timer.reset();
        }
        log.info("append request:{} for ct:{}", av, cT);

        return new VoteResult(term, res);
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
            timer.restart();
            currentTerm.set(term);
            votedFor.set(cId);
            flag = true;
        } else {
            int id = votedFor.get();
            LogEntry l = last();
            flag = (id == -1 || id == cId) && lIdx >= l.getIdx() && lTerm >= l.getTerm();
            if (flag) {
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

        for (String address : addresses) {
            RaftRequestVoteMessage req = RaftRequestVoteMessageBuilder.build(
                    servAddr, RaftUtils.find(servAddr, nodes),
                    currentTerm.get(), last().getIdx(), last().getTerm()
            );
            log.info("build request:{}", req);
            Optional<VoteResult> resp = post(address, "election", req, VoteResult.class);
            if (resp.isPresent()) {
                VoteResult vr = resp.get();
                if (vr.isVote()) {
                    int count = voteCount.incrementAndGet();
                    log.info("vote count : {} ", count);
                    if (RaftUtils.isQ(count, qs)) {
                        state.set(State.Leader);
                        timer.stopWatch();
                        votedFor.set(RaftUtils.find(client.getServiceAddress(), client.getNodes()));
                        log.info("this node is leader: {} ", currentTerm.get());

                        break;
                    }
                }
            }

        }
    }

    private <I, O> Optional<O> post(String address, String postfix, I req, Class<O> clazz) {
        try {
            ResponseEntity<O> resp = client.getRestTemplate().postForEntity("http://" + address + "/" + postfix, req, clazz);
            if (resp.getStatusCode().is2xxSuccessful()) {
                return Optional.ofNullable(resp.getBody());
            }
        } catch (Exception ex) {
            log.error("ex:", ex);
        }
        return Optional.empty();
    }

    private LogEntry last() {
        return logs.get(logs.size() - 1);
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
            matchIdx[i] = 1;
            nextIdx[i] = matchIdx[i] + 1;
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

    public void processCommand(Integer command) {
        String[] nodes = client.getNodes();
        String servAddr = client.getServiceAddress();
        int id = RaftUtils.find(servAddr, nodes);
        int lId = votedFor.get();
        if (lId != id) {
            if (lId > 0) {
                post(nodes[lId], "command", command, Void.class).ifPresent(v -> log.info("message redirected"));
            } else {
                log.error("lost message {}", command);
            }
        } else {
            logs.add(new LogEntry(currentTerm.get(), 0, command));
            for (int i = 0; i < nodes.length; i++) {
                String addr = nodes[i];
                int nxIdx = this.nextIdx[i];
                int prevLogIdx = --nxIdx;
                int prevLogTerm = prevLogIdx > 0 ? logs.get(prevLogIdx).getTerm() : 0;
                int le = Integer.min(logs.size(), nxIdx);
                Entry[] entries = logs.subList(nxIdx, le).stream()
                        .map(l -> new Entry(l.getTerm(), l.getIdx(), l.getCommand()))
                        .toArray(Entry[]::new);
                int min = Integer.min(commitIdx.get(), le);
                RaftAppendEntriesMessage req =
                        RaftAppendEntriesMessageBuilder.build(
                                servAddr, currentTerm.get(), id, prevLogIdx,prevLogTerm, min, entries);
                log.info("build append request:{}", req);
                post(addr, "append", req, VoteResult.class).ifPresent(vr -> log.info(" log : {}", vr));

            }
        }
    }


    class Timer {
        private AtomicBoolean turnFlag;
        private final int threshold;
        private final int leaderPause;

        private AtomicInteger countDown;

        private Supplier<Boolean> electionAction;
        private Supplier<Boolean> leaderAction;

        Timer(Supplier<Boolean> action, Supplier<Boolean> leaderAction) {
            this.threshold = new Random().nextInt(50) * 10 + 500; // ~ 0.5-1 sec
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

        void leaderWatch() {
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
