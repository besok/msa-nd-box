package ie.home.msa.sandbox.raft;

import ie.home.msa.messages.RaftRequestVoteMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    private final Processor processor;

    @Autowired
    public Controller(Processor processor) {
        this.processor = processor;
    }

    @PostMapping(path = "/election")
    public VoteResult processMessage(@RequestBody RaftRequestVoteMessage message) {
        return processor.processVoteMessage(message);
    }

}
