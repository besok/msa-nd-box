package ie.home.msa.lab.batch;

import ie.home.msa.messages.FileCountTask;
import ie.home.msa.messages.FileCountTaskMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class WorkerManager {
    private final WorkerInitializer workerInitializer;
    private final WorkerDestroyer workerDestroyer;
    private final Workers workers;

    private final RestTemplate restTemplate;
    private final AtomicInteger counter;
    private Queue<FileCountTask> taskQueue;
    private FileCountTask initTask;

    public WorkerManager(WorkerInitializer workerInitializer,
                         WorkerDestroyer workerDestroyer,
                         Workers workers) {
        this.workerInitializer = workerInitializer;
        this.workerDestroyer = workerDestroyer;
        this.workers = workers;
        this.taskQueue = new ArrayDeque<>();
        this.counter = new AtomicInteger(0);
        this.restTemplate = new RestTemplate();
    }


    public boolean isQueueTaskEmpty() {
        boolean empty = taskQueue.isEmpty();
        log.info(" the queue is empty {}", empty);
        return empty;

    }

    public long processTask(FileCountTaskMessage taskMessage) {
        this.initTask = taskMessage.getBody();
        taskQueue.addAll(initTask.split());
        int delta = 10 - workers.size();
        if (delta > 0) {
            for (int i = 0; i < delta; i++) {
                newWorker();
            }
        }
        while (!taskQueue.isEmpty() || workers.size() != 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return this.initTask.getResult();

    }

    public void sendTaskToWorker(String address) {
        FileCountTask task = taskQueue.poll();
        ResponseEntity<Void> resp = restTemplate.postForEntity("http://" + address + "/task", task, Void.class);
        if (resp.getStatusCode().isError()) {
            log.error(" send task {} to worker {} error {}", task, address, resp.getStatusCode());
        } else {
            log.info(" send task {} to worker {} ", task, address);
        }
    }

    public void processCompletedTask(FileCountTaskMessage taskMessage) {
        Long result = taskMessage.getBody().getResult();
        log.info("task from service {}",taskMessage.getBody());
        this.initTask.accumulate(result);
        log.info("initTask service {}",initTask);
    }

    public void newWorker() {
        if (counter.incrementAndGet() < 11) {
            workerInitializer.newWorker();
        }
    }



    public void removeWorker(String address) {
        workerDestroyer.removeByAddress(address);
        counter.decrementAndGet();
    }

}
