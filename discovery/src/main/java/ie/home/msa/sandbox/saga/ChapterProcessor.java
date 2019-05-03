package ie.home.msa.sandbox.saga;

import ie.home.msa.saga.Chapter;
import ie.home.msa.saga.Status;
import ie.home.msa.sandbox.discovery.client.DiscoveryClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;

@Service
@Slf4j
public class ChapterProcessor {
    private final ChapterInvoker invoker;

    public ChapterProcessor(ChapterInvoker invoker) {
        this.invoker = invoker;
    }

    private Chapter processChapter(Chapter chapter) {
        try {
            Object inputData = chapter.getInputData();
            ChapterInvoker.ChapterWrapper chWrapper = invoker.invoke(chapter.getTitle());
            Object outputData = chWrapper.getProcessMethod().invoke(chWrapper.getBean(), inputData);
            chapter.setOutputData(outputData);
            chapter.setStatus(Status.DONE);
        } catch (Exception e) {
            log.error(" chapter {} ", chapter, e);
            return rollbackChapter(chapter);
        }
        return chapter;
    }

    private Chapter rollbackChapter(Chapter chapter) {
        try {
            Object inputData = chapter.getInputData();
            ChapterInvoker.ChapterWrapper chWrapper = invoker.invoke(chapter.getTitle());
            Object outputData = chWrapper.getRollbackMethod().invoke(chWrapper.getBean(), inputData);
            chapter.setOutputData(outputData);
            chapter.setStatus(Status.ROLLBACK);
        } catch (Exception e) {
            log.error(" chapter {} ", chapter, e);
            chapter.setStatus(Status.FAILED);
        }
        return chapter;
    }

    public Chapter handle(Chapter chapter) {
        Status status = chapter.getStatus();
        switch (status) {
            case RUN:
            case READY:
                return processChapter(chapter);
            case DONE:
            case ROLLBACK:
                return rollbackChapter(chapter);
            case FAILED:
        }
        return chapter;
    }
}
