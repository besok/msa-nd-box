package ie.home.msa.sandbox.saga;

import ie.home.msa.saga.Saga;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@Slf4j
public class SagaController {

    private final ChapterProcessor processor;

    @Autowired
    public SagaController(ChapterProcessor processor) {
        this.processor = processor;
    }

    @RequestMapping(path = "/saga/event", method = RequestMethod.POST)
    private boolean processSaga(@RequestBody Saga saga) {
        CompletableFuture.runAsync(() -> processor.process(saga));
        return true;
    }


}
