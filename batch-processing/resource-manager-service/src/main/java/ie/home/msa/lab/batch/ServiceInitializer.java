package ie.home.msa.lab.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
public class ServiceInitializer {

    @Value("${batch.worker.jar}")
    private String jar;

    private Workers workers;
    private ExecutorService executor;

    public ServiceInitializer(Workers workers) {
        this.workers = workers;
        this.executor = Executors.newFixedThreadPool(10);
    }

    public void newWorker() throws ExecutionException, InterruptedException {
        Future<Optional<Worker>> result = executor.submit(run(workers.size()));
        Optional<Worker> workerOpt = result.get();
        workerOpt.ifPresent(workers::add);
    }

    private Callable<Optional<Worker>> run(int id) {
        return () -> {
            try {
                Process p = pb().start();
                Worker w = new Worker();
                w.setProcess(p);
                w.setId(id);
                processLog(id, p);
                return Optional.of(w);
            } catch (IOException e) {
                log.error(" error while start new worker ", e);
            }
            return Optional.empty();
        };
    }

    private void processLog(int id, Process p) throws IOException {
        BufferedReader is = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = is.readLine()) != null) {
            log.info("w_id[{}]: {} ", id, line);
        }
    }


    private ProcessBuilder pb() {
        return new ProcessBuilder("java", "-server", "-jar", jar);
    }
}
