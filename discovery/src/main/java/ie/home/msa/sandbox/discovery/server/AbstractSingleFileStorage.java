package ie.home.msa.sandbox.discovery.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public abstract class AbstractSingleFileStorage<T> {
    private Map<String, T> services;
    private Path store;
    private Lock lock;

    public Map<String, T> getServices() {
        return services;
    }

    public Path getStore() {
        return store;
    }

    public Lock getLock() {
        return lock;
    }

    public AbstractSingleFileStorage(String storeFile) {
        this.services = new HashMap<>();
        this.store = Paths.get(new ClassPathResource(storeFile).getPath());
        this.lock = new ReentrantLock();
    }
    public void put(String service,T val) {
        lock.lock();
        try {
            T first = services.put(service, val);
            if (Objects.isNull(first)) {
                String serv = service + System.lineSeparator();
                Files.write(store, serv.getBytes(), StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            log.info("service {} - error : {}", service, e);
        } finally {
            lock.unlock();
        }
    }

    public T get(String serv) {
        lock.lock();
        try {
            T val = services.get(serv);
            return val;
        } finally {
            lock.unlock();
        }
    }

    public boolean remove(String serv) {
        lock.lock();
        try {
            List<String> servs = Files.readAllLines(store);
            servs.removeIf(e -> e.contains(serv));
            Files.write(store, servs, StandardOpenOption.CREATE);
            return true;
        } catch (IOException e) {
            log.info(" error remove from file store", e);
        } finally {
            lock.unlock();
        }
        return false;
    }

    protected abstract void init() throws Exception;
}
