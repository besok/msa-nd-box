package ie.home.msa.sandbox.discovery.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
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

    public AbstractSingleFileStorage(String storeFile) {
        this.services = new HashMap<>();
        this.store = Paths.get(new ClassPathResource(storeFile).getPath());
        this.lock = new ReentrantLock();
    }

    public void put(String service, T val) {
        lock.lock();
        try {
            T first = services.put(service, val);
            if (Objects.isNull(first)) {
                String serv = toFile(service, val) + System.lineSeparator();
                Files.write(store, serv.getBytes(), StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            log.info("service {} - error : {}", service, e);
        } finally {
            lock.unlock();
        }
    }

    protected void clean() {
        lock.lock();
        try {
           this.services.clear();
           Files.write(store,"".getBytes(),StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            log.info(" file can not be truncated ",e);
        } finally {
            lock.unlock();
        }
    }

    protected abstract String toFile(String serv, T val);

    protected abstract Map<String, T> fromFile(List<String> records);

    public T get(String serv) {
        lock.lock();
        try {
            return services.get(serv);
        } finally {
            lock.unlock();
        }
    }

    public boolean remove(String serv) {
        lock.lock();
        try {
            T removeVal = services.remove(serv);
            List<String> records = Files.readAllLines(store);
            if (Objects.isNull(removeVal)) {
                records.removeIf(e -> e.contains(serv));
            } else {
                records.remove(toFile(serv, removeVal));
            }
            Files.write(store, records, StandardOpenOption.CREATE);
            return true;
        } catch (IOException e) {
            log.info(" error remove from file store", e);
        } finally {
            lock.unlock();
        }
        return false;
    }

    @PostConstruct
    protected void init() throws IOException {
        Path store = getStore();
        if (!store.toFile().exists()) {
            Files.createFile(store);
        }
        this.services = fromFile(Files.readAllLines(store));
    }
}
