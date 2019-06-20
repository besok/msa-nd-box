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

    private AtomicInteger leaderId;

    private List<LogEntry> logs;

    @Autowired
    public Processor(DiscoveryClient client) {
        leaderId = new AtomicInteger(-1);
        state = new AtomicReference<>(State.Follower);
        currentTerm = new AtomicInteger(0);
        logs = new ArrayList<>();
        logs.add(new LogEntry(0,0));
        this.client = client;
        stopwatch = new Stopwatch(() -> {
            currentTerm.incrementAndGet();
            state.set(State.Candidate);
            stopwatch.reset();

            return true;
        });
    }





    public VoteResult processVoteMessage(RaftRequestVoteMessage message){
        RequestVote rv = message.getBody();
        int term = rv.getTerm();

        if(term < currentTerm.get()){
            return new VoteResult(currentTerm.get(),false);
        }

        if(term > currentTerm.get()){
            state.set(State.Candidate);
            stopwatch.reset();
            currentTerm.set(term);
            return new VoteResult(term,true);
        }
        if(term == currentTerm.get()){

        }
    }


    private int findId(){
        return RaftUtils.find(client.getServiceAddress(),client.getNodes());
    }

    private RaftRequestVoteMessage voteItself(){
        LogEntry logEntry = logs.get(logs.size() - 1);

        return RaftRequestVoteMessageBuilder.build(
                client.getServiceAddress(),findId(),currentTerm.get(),logEntry.getIdx(),logEntry.getTerm()
        );
    }

    @Override
    public Boolean operate() {
        stopwatch.watch();

        return null;
    }
}
