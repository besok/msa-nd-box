package ie.home.msa.sandbox.saga;

import ie.home.msa.messages.GetServiceMessage;
import ie.home.msa.saga.Chapter;
import ie.home.msa.saga.Saga;
import ie.home.msa.saga.Status;
import ie.home.msa.sandbox.discovery.client.DiscoveryClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SagaProcessor {

    private final ChapterInvoker invoker;
    private final DiscoveryClient discoveryClient;

    public SagaProcessor(ChapterInvoker invoker, DiscoveryClient discoveryClient) {
        this.invoker = invoker;
        this.discoveryClient = discoveryClient;
    }

    public Saga process(Saga saga) {
        Chapter chapter = saga.currentChapter();
        Status sagaStatus = saga.getStatus();
        if (thisService(chapter)) {
            switch (sagaStatus) {
                case READY:
                case RUN:
                    processChapter(saga);
                    break;
                case DONE:
                    log.info("saga is successfully done, saga {} ", saga);
                    break;
                case ROLLBACK:
                    rollbackChapterLoop(saga);
                    break;
                case FAILED:
                    log.info("saga is failed, saga {} ", saga);
                    break;
            }
        } else {
            sendToService(chapter.getService(), saga);
        }
        return saga;
    }

    private boolean thisService(Chapter chapter) {
        return chapter.getService().equals(discoveryClient.getServiceName());
    }

    protected Saga processChapter(Saga saga) {
        Chapter chapter = saga.currentChapter();
        String title = chapter.getTitle();
        ChapterInvoker.ChapterWrapper wrapper = invoker.invoke(title);
        try {
            Object invoke = wrapper.getProcessMethod().invoke(wrapper.getBean(), chapter.getInputData());
            chapter.setStatus(Status.DONE);
            chapter.setOutputData(invoke);
            saga.setStatus(Status.RUN);
            if (saga.inc() == saga.size()) {
                saga.setStatus(Status.DONE);
                saga.dec();
            }
        } catch (Exception ex) {
            rollbackChapter(saga);
        }
        return process(saga);
    }

    protected Saga rollbackChapter(Saga saga) {
        Chapter chapter = saga.currentChapter();
        String title = chapter.getTitle();
        ChapterInvoker.ChapterWrapper wrapper = invoker.invoke(title);
        try {
            Object invoke = wrapper.getRollbackMethod().invoke(wrapper.getBean(), chapter.getInputData());
            chapter.setStatus(Status.ROLLBACK);
            chapter.setOutputData(invoke);

        } catch (Exception e) {
            chapter.setStatus(Status.FAILED);
        }
        saga.setStatus(Status.ROLLBACK);
        if (saga.dec() < 0) {
            saga.setStatus(Status.FAILED);
            saga.inc();
        }
        return saga;
    }
    protected Saga rollbackChapterLoop(Saga saga) {
        return process(rollbackChapter(saga));
    }

    private void sendToService(String service, Saga saga) {
        GetServiceMessage message = discoveryClient.getAddress(service);
        String address = "http://" + message.getBody().getAddress() + "/saga/event";
        ResponseEntity<Boolean> resp = discoveryClient.getRestTemplate().postForEntity(address, saga, Boolean.class);
        if (resp.getStatusCode().is2xxSuccessful()) {
            log.info("send saga to service {} ", service);
        }
        else{
            Chapter chapter = saga.currentChapter();
            chapter.setStatus(Status.FAILED);
            saga.dec();
            saga.setStatus(Status.ROLLBACK);
            process(saga);
        }
    }


}
