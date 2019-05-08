package ie.home.msa.lab.batch.worker;

import ie.home.msa.messages.FileCountTask;
import ie.home.msa.messages.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
public class JobService {

    private AtomicBoolean work;

    public JobService() {
        work = new AtomicBoolean(false);
    }

    public int getWork() {
        return work.get() ? 1 : 0;
    }

    public synchronized FileCountTask processTask(FileCountTask task) {
        work.set(true);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        FileCountTask resTask = (FileCountTask) task.process();
        work.set(false);
        return resTask;
    }

}
