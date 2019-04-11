package ie.home.msa.sandbox.discovery.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ApplicationRestarter {
    private final RetryFileStorage storage;

    @Autowired
    public ApplicationRestarter(RetryFileStorage storage) {
        this.storage = storage;
    }

    public void startApplication(String service) throws IOException, InterruptedException {
        Process process = new ProcessBuilder("java", "-jar", storage.get(service))
                .inheritIO()
                .start();
        log.info(" process exit value {} ", process.isAlive());
    }
}
