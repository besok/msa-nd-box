package ie.home.msa.sandbox.logger;

import ie.home.msa.sandbox.saga.ChapterAction;
import ie.home.msa.sandbox.saga.ChapterRollback;
import ie.home.msa.sandbox.saga.SagaChapter;
import lombok.extern.slf4j.Slf4j;

@SagaChapter(title = "log")
@Slf4j
public class Logger {

    private int c = 0;

    @ChapterAction
    public String log(String logInfo) {
        log.info(" save to log  {} ", logInfo);
        c++;
        if (c > 1) {
            throw new RuntimeException();
        }
        return logInfo;
    }

    @ChapterRollback
    public String rollback(String logInfo) {
        log.info(" do nothing with log rollback");
        return logInfo;
    }
}
