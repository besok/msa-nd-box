package ie.home.msa.lab.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class ServiceInitializer {

    @Value("${batch.worker.jar}")
    private String jar;

    private AtomicInteger id;
    private ExecutorService executor;

    public ServiceInitializer() {
        this.id = new AtomicInteger(0);
        this.executor = Executors.newFixedThreadPool(20);
    }

    public void newWorker() {
        executor.submit(() -> {
            try {
                Process p = new ProcessBuilder("java", "-server", "-jar", jar).start();
                BufferedReader is = new BufferedReader(new InputStreamReader(p.getInputStream()));
                int idNode = id.incrementAndGet();
                String line;
                while ((line = is.readLine()) != null) {
                    log.info("w_id[{}]: {} ", idNode, line);
                }
            } catch (IOException e) {
                log.error(" error while start new worker ", e);
            }
        });
    }


}
