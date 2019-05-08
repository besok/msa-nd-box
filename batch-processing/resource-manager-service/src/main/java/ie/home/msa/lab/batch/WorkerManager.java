package ie.home.msa.lab.batch;

import ie.home.msa.messages.FileCountTask;
import ie.home.msa.messages.FileCountTaskMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class WorkerManager {
    private final WorkerInitializer workerInitializer;
    private final WorkerDestroyer workerDestroyer;
    private final RestTemplate restTemplate;
    private final AtomicInteger counter;

    private Queue<FileCountTask> taskQueue;
    private FileCountTask initTask;

    public WorkerManager(WorkerInitializer workerInitializer,
                         WorkerDestroyer workerDestroyer) {
        this.workerInitializer = workerInitializer;
        this.workerDestroyer = workerDestroyer;
        this.taskQueue = new ArrayDeque<>();
        counter = new AtomicInteger(0);
        restTemplate = new RestTemplate();
    }


    public synchronized void processTask(FileCountTaskMessage taskMessage) {
        this.initTask = taskMessage.getBody();
        taskQueue.addAll((Collection<? extends FileCountTask>) initTask.split());
        while (!taskQueue.isEmpty()) {

        }
        workerDestroyer.operate();
    }

    public void sendTaskToWorker(FileCountTask task,String address){
        ResponseEntity<Void> resp = restTemplate.postForEntity("http://" + address + "/task", task, Void.class);
        if(resp.getStatusCode().isError()){
            log.error(" send task {} to worker {} error {}",task,address,resp.getStatusCode());
        }else{
            log.info(" send task {} to worker {} ",task,address);
        }
    }

    public void processCompletedTask(FileCountTaskMessage taskMessage){
        this.initTask.accumulate(taskMessage.getBody().getResult());
    }

    public void newWorker() {
        if (counter.incrementAndGet() < 10) {
            workerInitializer.newWorker();
        }else{
            log.info(" worker count is limited");
        }
    }

    public void removeWorker() {
        workerDestroyer.removeFirst();
        counter.decrementAndGet();
    }

}
