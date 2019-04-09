package ie.home.msa.sandbox.discovery.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.*;


@Service
@Slf4j
public class ServiceRegistryStorage {

    private Path store;
    private Map<String, String> memory;
    private Lock lock;


    public Path store() {
        return store;
    }

    public ServiceRegistryStorage() {
        lock = new ReentrantLock();
        memory = new HashMap<>();
        store = Paths.get("src", "main", "resources")
                .resolve("store");

    }

    public Map<String,String> getServices(){
        lock.lock();
        try {
            return new HashMap<>(memory);
        }finally {
            lock.unlock();
        }
    }
    public boolean put(String service, String address) {
        Path file = store.resolve(service);
        lock.lock();
        try {
            String addr = memory.put(service, address);
            if (addr != null) {
                Files.write(file, address.getBytes(), CREATE, WRITE);
                log.info("update a service {} - {}", service,addr);
            } else {
                Files.createFile(file);
                Files.write(file, address.getBytes(), WRITE);
                log.info("register a service {}", service);
            }
        } catch (IOException e) {
            log.info("service {} is not registered: {}", service, e);
            return false;
        } finally {
            lock.unlock();
        }
        return true;
    }

    public String get(String service) {
        return memory.get(service);
    }

    public void  clean() throws IOException {
        for (File file : store.toFile().listFiles()) {
            Files.deleteIfExists(file.toPath());
            log.info("file {} has been removed",file.getName());
        }
    }

    @PostConstruct
    protected void init() throws IOException {
        if (Files.notExists(store)) {
            Files.createDirectory(store);
        }
        try (Stream<Path> paths = Files.list(store)) {
            paths.forEach(p -> {
                try {
                    List<String> servers = Files.readAllLines(p);
                    String service = p.getFileName().toString();
                    memory.put(service, servers.get(0));
                    log.info("service {} has been registered",service);
                } catch (IOException e) {
                    log.info(" error while initialization {}",e);
                }
            });
        }
    }

}
