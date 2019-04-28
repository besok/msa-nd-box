package ie.home.msa.sandbox.saga;

import ie.home.msa.saga.Chapter;
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

    private final SagaProcessor processor;
    private final ChapterProcessor chapterProcessor;
    @Autowired
    public SagaController(SagaProcessor processor, ChapterProcessor chapterProcessor) {
        this.processor = processor;
        this.chapterProcessor = chapterProcessor;
    }

    @RequestMapping(path = "/saga/event", method = RequestMethod.POST)
    private boolean processSaga(@RequestBody Saga saga) {
        CompletableFuture.runAsync(() -> processor.process(saga));
        return true;
    }
    @RequestMapping(path = "/saga/ch", method = RequestMethod.POST)
    private Chapter processChapter(@RequestBody Chapter chapter) {
        return chapterProcessor.handle(chapter);
    }




}
