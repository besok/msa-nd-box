package ie.home.msa.lab.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class WorkerService {
    private final ServiceInitializer serviceInitializer;
    private final WorkerDestroyer workerDestroyer;
    public WorkerService(ServiceInitializer serviceInitializer,
                         WorkerDestroyer workerDestroyer) {
        this.serviceInitializer = serviceInitializer;
        this.workerDestroyer = workerDestroyer;
    }

    @RequestMapping(path = "/worker/add", method = RequestMethod.GET)
    public void newWorker(){
        serviceInitializer.newWorker();
    }

    @RequestMapping(path = "/worker/rem", method = RequestMethod.GET)
    public void closeWorker(){
        workerDestroyer.removeFirst();
    }
}
