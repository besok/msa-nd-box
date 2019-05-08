package ie.home.msa.lab.batch;

import ie.home.msa.sandbox.discovery.client.DestroyOperation;
import ie.home.msa.sandbox.discovery.server.ServiceController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Random;


@Service
@Slf4j
public class WorkerDestroyer implements DestroyOperation {
    private final Workers workers;
    private RestTemplate restTemplate ;
    public WorkerDestroyer(Workers workers) {
        this.restTemplate = new RestTemplate();
        this.workers = workers;
    }

    @Override
    public Boolean operate() {
        List<Worker> workerList = workers.getWorkerList();
        for (Worker worker : workerList) {
            removeByAddress(worker.getAddress());
        }
        return true;
    }


    protected boolean removeByAddress(String address){
        try {
        restTemplate.getForEntity("http://" +address + "/close", Void.class);
        return false;
        }
        catch (Exception ignore){
            // connection ex;
        }
        return true;
    }
}
