package ie.home.msa.messages.sandbox.discovery.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.*;


@Service
@Slf4j
public class FileStorage {

    private Path store;
    private ConcurrentMap<String, String> memory;
    private Lock lock;

    public Path store() {
        return store;
    }

    public FileStorage() {
        lock = new ReentrantLock();
        memory = new ConcurrentHashMap<>();
    }

    public boolean put(String service, String address) {
        String addr = memory.put(service, address);
        Path file = store.resolve(service);
        lock.lock();
        try {
            if (addr != null) {
                Files.write(file, address.getBytes(), CREATE, WRITE);
                log.info("update a service {}", service);
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

    @PostConstruct
    protected void fileInit() throws IOException {
        store = Paths.get("src", "main", "resources").resolve("store");
        if (Files.notExists(store)) {
            Files.createDirectory(store);
        }

        try (Stream<Path> paths = Files.list(store)) {
            paths.forEach(p -> {
                try {
                    List<String> servers = Files.readAllLines(p);
                    memory.put(p.getFileName().toString(), servers.get(0));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }


    }

}
