package ie.home.msa.lab.zab;

import ie.home.msa.messages.ZElectionMessage;
import ie.home.msa.messages.ZRecoveryMessage;
import ie.home.msa.messages.ZWriteMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
public class NodeController {

    private final ElectionNotificationReceiver processor;
    private final BroadcastProcessor broadcastProcessor;

    public NodeController(ElectionNotificationReceiver processor,
                          BroadcastProcessor broadcastProcessor) {
        this.processor = processor;
        this.broadcastProcessor = broadcastProcessor;
    }


    @PostMapping(path = "/election")
    public void processMessage(@RequestBody ZElectionMessage message) {
        CompletableFuture.runAsync(() -> processor.processMessage(message));
    }

    @PostMapping(path = "/message")
    public void broadcastMessage(@RequestBody ZWriteMessage message) {
        CompletableFuture.runAsync(() -> broadcastProcessor.commitMessage(message));
    }

    @PostMapping(path = "/write")
    public void broadcastMessage(@RequestBody String object) {
        CompletableFuture.runAsync(() -> broadcastProcessor.processObject(object));
    }

    @PostMapping(path = "/recovery")
    public void broadcastMessage(@RequestBody ZRecoveryMessage message) {
        CompletableFuture.runAsync(() -> broadcastProcessor.processRecovery(message));
    }

}
