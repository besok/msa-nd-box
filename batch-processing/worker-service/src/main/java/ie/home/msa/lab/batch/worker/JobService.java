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

    public FileCountTask processTask(FileCountTask task) {
        work.set(true);
        FileCountTask resTask = task.process();
        work.set(false);
        return resTask;
    }

}
