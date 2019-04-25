package ie.home.msa.sandbox.logger;

import ie.home.msa.sandbox.saga.Process;
import ie.home.msa.sandbox.saga.Rollback;
import ie.home.msa.sandbox.saga.SagaChapter;
import lombok.extern.slf4j.Slf4j;

@SagaChapter(title = "log")
@Slf4j
public class Logger {

    @Process
    public String log(String logInfo) {
        log.info(" save to log  failed{} ", logInfo);
        throw new RuntimeException();
    }

    @Rollback
    public String rollback(String logInfo) {
        log.info(" do nothing with log rollback");
        return logInfo;
    }
}
