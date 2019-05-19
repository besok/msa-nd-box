package ie.home.msa.lab.zab;

import ie.home.msa.messages.ElectionMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
public class NodeController {

    private final ElectionNotificationReceiver processor;

    public NodeController(ElectionNotificationReceiver processor) {
        this.processor = processor;
    }


    @PostMapping(path = "/election")
    public void processMessage(@RequestBody ElectionMessage message){
        CompletableFuture.runAsync(()-> processor.processMessage(message));
    }

}
