package ie.home.msa.lab.batch;

import ie.home.msa.messages.FileCountTaskMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class WorkerController {
    private final WorkerManager workerManager;

    public WorkerController(WorkerManager workerManager) {
        this.workerManager = workerManager;
    }

    @RequestMapping(path = "/worker/task", method = RequestMethod.POST)
    public void intermidiateTask(@RequestBody FileCountTaskMessage taskMessage){
        workerManager.processCompletedTask(taskMessage);
    }
    @RequestMapping(path = "/task", method = RequestMethod.POST)
    public long fullTask(@RequestBody FileCountTaskMessage taskMessage){
        return workerManager.processTask(taskMessage);
    }


}
