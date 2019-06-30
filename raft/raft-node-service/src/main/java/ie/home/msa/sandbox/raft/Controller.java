package ie.home.msa.sandbox.raft;

import ie.home.msa.messages.RaftAppendEntriesMessage;
import ie.home.msa.messages.RaftRequestVoteMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
public class Controller {

    private final RaftProcessor processor;

    @Autowired
    public Controller(RaftProcessor processor) {
        this.processor = processor;
    }

    @PostMapping(path = "/election")
    public VoteResult processMessage(@RequestBody RaftRequestVoteMessage message) {
        return processor.processVoteMessage(message);
    }

    @PostMapping(path = "/append")
    public VoteResult processAppendMessage(@RequestBody RaftAppendEntriesMessage message) {
        return processor.processAppendMessage(message);
    }


    @PostMapping(path = "/command")
    public boolean takeCommand(@RequestBody Integer command) {
        return processor.processCommand(command);
    }

    @PostMapping(path = "/pulse")
    public VoteResult heartBeat(@RequestBody HeartBeatMessage message) {
        return processor.processPulse(message);
    }


    @GetMapping(path = "/state")
    public String getState() {
        return processor.getState().get().toString();
    }

    @GetMapping(path = "/log")
    public String getLog() {
        return Arrays.toString(processor.getLogs().toArray());
    }



}
