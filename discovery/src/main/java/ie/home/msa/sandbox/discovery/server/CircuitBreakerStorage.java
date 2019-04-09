package ie.home.msa.sandbox.discovery.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Slf4j
public class CircuitBreakerStorage {
    private Map<String, Boolean> services;
    private Path store;
    private Lock lock;

    public CircuitBreakerStorage() {
        this.services = new HashMap<>();
        this.store = Paths.get("src", "main", "resources", "circuit-list");
        this.lock = new ReentrantLock();
    }

    public void turnOff(String service) {
        if (services.containsKey(service)) {
            services.put(service, true);
            log.info(" service {} is turn off",service);
        }
    }

    public void turnOn(String service) {
        if (services.containsKey(service)) {
            services.put(service, false);
            log.info(" service {} is turn on",service);
        }
    }

    public void put(String service) {
        lock.lock();
        try {
            boolean first = services.put(service, true);
            if (first) {
                String serv = service + System.lineSeparator();
                Files.write(store, serv.getBytes(), StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            log.info("service {} - error : {}", service, e);
        } finally {
            lock.unlock();
        }
    }

    public boolean get(String serv) {
        lock.lock();
        try {
            Boolean val = services.get(serv);
            return Objects.isNull(val)?false:val;
        } finally {
            lock.unlock();
        }
    }

    @PostConstruct
    protected void init() throws IOException {
        if (Files.notExists(store)) {
            Files.createFile(store);
        }
        List<String> servicesList = Files.readAllLines(store);
        for (String service : servicesList) {
            services.put(service, false);
        }
    }
}
