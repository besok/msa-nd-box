package ie.home.msa.lab.batch.worker;

import ie.home.msa.messages.*;
import ie.home.msa.sandbox.discovery.client.DiscoveryClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@RestController
@Slf4j
public class WorkerController {


    private final JobService jobService;
    private final String address;
    private final String serviceName;
    private final String adminAddress;
    private final RestTemplate restTemplate;

    public WorkerController(JobService jobService, DiscoveryClient client) {
        this.jobService = jobService;
        this.address = client.getServiceAddress();
        this.serviceName = client.getServiceName();
        this.restTemplate = new RestTemplate();
        this.adminAddress = client.getAdminAddress();
    }

    @RequestMapping(path = "/task", method = RequestMethod.POST)
    public void processTask(@RequestBody FileCountTask task) {
        CompletableFuture.runAsync(() -> {
            FileCountTask resTask = jobService.processTask(task);
            FileCountTaskMessage taskMessage = new FileCountTaskMessage();
            taskMessage.setBody(resTask);
            taskMessage.setDsc("");
            taskMessage.setStatus(TaskStatus.FINISH);
            taskMessage.setVersion(1);
            taskMessage.setService(Service.of(serviceName, address));
            ResponseEntity<Void> resp = restTemplate.postForEntity("http://" + adminAddress + "/worker/task", taskMessage, Void.class);
            if (resp.getStatusCode().isError()) {
                log.error(" error to send to admin server {}", taskMessage);
            } else {
                log.info(" send to admin server {}", taskMessage);
            }
        });
    }

}
