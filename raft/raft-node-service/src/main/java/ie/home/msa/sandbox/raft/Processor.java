package ie.home.msa.sandbox.raft;


import ie.home.msa.messages.RaftRequestVoteMessage;
import ie.home.msa.messages.RaftRequestVoteMessageBuilder;
import ie.home.msa.raft.RequestVote;
import ie.home.msa.sandbox.discovery.client.DiscoveryClient;
import ie.home.msa.sandbox.discovery.client.InitializationOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Slf4j
public class Processor implements InitializationOperation {
    private AtomicInteger currentTerm;
    private AtomicReference<State> state;
    private Stopwatch stopwatch;
    private final DiscoveryClient client;

    private AtomicInteger voteCount;
    private AtomicInteger votedFor;
    private List<LogEntry> logs;

    @Autowired
    public Processor(DiscoveryClient client) {
        votedFor = new AtomicInteger(-1);
        voteCount = new AtomicInteger(0);
        state = new AtomicReference<>(State.Follower);
        currentTerm = new AtomicInteger(0);
        logs = new ArrayList<>();
        logs.add(new LogEntry(0, 0));
        this.client = client;
        stopwatch = new Stopwatch(() -> {
            currentTerm.incrementAndGet();
            voteCount.set(1);
            state.set(State.Candidate);
            stopwatch.reset();
            return true;
        });
    }


    public VoteResult processVoteMessage(RaftRequestVoteMessage message) {
        RequestVote rv = message.getBody();
        log.info(" coming vote request: {}", rv);
        int term = rv.getTerm();
        int cId = rv.getCandidateId();
        int lIdx = rv.getLastLogIdx();
        int lTerm = rv.getLastLogTerm();
        boolean flag = false;
        int cT = currentTerm.get();
        if (term > cT) {
            State s = this.state.get();
            if (s == State.Leader) {
                this.state.set(State.Candidate);
            }
            if (s == State.Candidate) {
                this.state.set(State.Follower);
            }

            stopwatch.reset();
            currentTerm.set(term);
            flag = true;
        } else {
            int id = votedFor.get();
            LogEntry l = last();
            flag = (id == -1 || id == cId) && lIdx >= l.getIdx() && lTerm >= l.getTerm();
        }

        log.info(" returned resuzlt: flag:{},term:{}",flag,cT);
        return new VoteResult(cT, flag);
    }


    private int findId() {
        return RaftUtils.find(client.getServiceAddress(), client.getNodes());
    }

    private RaftRequestVoteMessage voteItself() {
        LogEntry logEntry = logs.get(logs.size() - 1);

        return RaftRequestVoteMessageBuilder.build(
                client.getServiceAddress(), findId(), currentTerm.get(), logEntry.getIdx(), logEntry.getTerm()
        );
    }

    public LogEntry last() {
        return logs.get(logs.size() - 1);
    }

    @Override
    public Boolean operate() {
        stopwatch.watch();

        return null;
    }
}
