package ie.home.msa.sandbox.discovery.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ApplicationRestarter {

    private final List<InitializationOperation> initializationOperations;
    private final List<DestroyOperation> destroyers;
    private final ApplicationContext context;

    public ApplicationRestarter(List<InitializationOperation> initializationOperations,
                                List<DestroyOperation> destroyers,
                                ApplicationContext context) {
        this.initializationOperations = initializationOperations;
        this.destroyers = destroyers;
        this.context = context;
    }

    public Boolean init(){
        boolean result = true;
        log.info("common init process is starting");
        for (InitializationOperation initializationOperation : initializationOperations) {
            String className = initializationOperation.getClass().getName();
            try {
                log.info("init operation {} has been started", className);
                if(!initializationOperation.operate()){
                    result = false;
                }
            } catch (Exception e) {
                log.error("error to init operation {}", className,e);
            }
        }
        return result;
    }

    public boolean close() {
        boolean result = true;
        log.info("common close process is starting");
        for (DestroyOperation destroyOperation : destroyers) {
            String className = destroyOperation.getClass().getName();
            try {
                log.info("Destroy operation {} has been started", className);
                if(!destroyOperation.operate()){
                    result = false;
                }
            } catch (Exception e) {
                log.error("error to destroy operation {}", className,e);
            }
        }
        return result && springClose();
    }

    public boolean closeImmediately() {
        log.info("close immediately process is starting");
        return springClose();
    }

    private boolean springClose() {
        return SpringApplication.exit(context, () -> 1) > 0;
    }


}
