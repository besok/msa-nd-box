package ie.home.msa.sandbox.saga.orchestrator;

import ie.home.msa.messages.GetServiceMessage;
import ie.home.msa.saga.Chapter;
import ie.home.msa.saga.Saga;
import ie.home.msa.saga.Status;
import ie.home.msa.sandbox.discovery.client.DiscoveryClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import static ie.home.msa.saga.Status.*;

@RestController
@Slf4j
public class SagaOrchestrator {
    private final DiscoveryClient discoveryClient;

    public SagaOrchestrator(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @RequestMapping(path = "/saga/run", method = RequestMethod.POST)
    public void processSaga(@RequestBody Saga saga) {
        log.info("start saga {} ", saga);

        saga.setStatus(RUN);
        int size = saga.size() ;
        while (saga.getStatus() != DONE && saga.getStatus() != FAILED) {
            Chapter chapter = saga.currentChapter();
            log.info("current chapter {}", chapter);
            Chapter doneCh = runCh(chapter);
            saga.updateCurrentChapter(doneCh);
            log.info("current chapter after {}", doneCh);
            switch (doneCh.getStatus()) {
                case DONE:
                    saga.setStatus(saga.inc() >= size ? DONE : RUN);
                    break;
                case ROLLBACK:
                case FAILED:
                    saga.setStatus((saga.dec() <= -1) ? FAILED : ROLLBACK);
                    break;
            }
        }

        log.info("finish {} ,saga {}", saga.getStatus(), saga);
    }


    private Chapter runCh(Chapter chapter) {
        String service = chapter.getService();
        RestTemplate restTemplate = discoveryClient.getRestTemplate();
        try {
            ResponseEntity<Chapter> resp = restTemplate.postForEntity(getUrl(service), chapter, Chapter.class);
            if (resp.getStatusCode().isError()) {
                chapter.setStatus(FAILED);
                return chapter;
            }
            return resp.getBody();
        } catch (Exception e) {
            log.info(" service is failed , chapter {}", chapter, e);
            chapter.setStatus(FAILED);
            return chapter;
        }
    }

    private String getUrl(String service) {
        GetServiceMessage mes = discoveryClient.getAddress(service);
        String address = mes.getBody().getAddress();
        return "http://" + address + "/saga/ch";
    }

}
