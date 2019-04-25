package ie.home.msa.sandbox.discovery.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ie.home.msa.sandbox.discovery.server.StorageListener.Event.*;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

@Slf4j
public abstract class AbstractFileStorage<T> implements FileStorage<T> {
    private String storeName;
    private Path store;
    private Lock lock;
    private Map<String, List<T>> memoryServices;
    private final Random random;

    private StorageListenerHandler listenerHandler;


    public AbstractFileStorage(String directory, StorageListenerHandler handler) {
        this.storeName = directory;
        lock = new ReentrantLock();
        memoryServices = new HashMap<>();
        store = Paths.get(new ClassPathResource(directory).getPath());
        random = new Random();
        this.listenerHandler = handler;
    }


    public List<T> get(String service) {
        listenerHandler.onEvent(GET, service, storeName, null);
        return memoryServices.get(service);
    }

    public boolean contains(String key) {
        return memoryServices.containsKey(key);
    }

    public boolean put(String key, T val) {

        Path file = store.resolve(key);
        lock.lock();
        String storageName = store.toString();
        try {
            List<T> values = memoryServices.get(key);
            if (Objects.isNull(values)) {
                values = new ArrayList<>(Arrays.asList(val));
            } else {
                ArrayList<T> tempVals = new ArrayList<>(values);
                for (T value : tempVals) {
                    if (equal(val, value)) {
                        values.remove(value);
                    }
                }
                values.add(val);
            }
            memoryServices.put(key, values);
            rewriteToFile(file, toFile(values));
            log.info("put to store: {}, key: {}, val: {}", storageName, key, val);
            listenerHandler.onEvent(PUT, storageName, key, val);
        } catch (IOException e) {
            log.info("failed operation is put to store: {}, key: {}, val: {}", storageName, key, e);
            return false;
        } finally {
            lock.unlock();
        }
        return true;
    }

    public boolean removeKey(String key) {
        String storeName = store.getFileName().toString();
        lock.lock();
        try {
            memoryServices.remove(key);
            Files.deleteIfExists(store.resolve(key));
            log.info("delete a key: {} from storage: {} ", key, storeName);
            listenerHandler.onEvent(REMOVE_KEY, storeName, key, null);
            return true;
        } catch (IOException e) {
            log.info("exception delete file {} from storage: {}", key, storeName, e);
        } finally {
            lock.unlock();
        }
        return false;
    }

    public boolean removeVal(String key, T val) {
        String storeName = store.getFileName().toString();
        lock.lock();
        try {
            List<T> vals = memoryServices.get(key);
            if (Objects.isNull(vals)) {
                vals = new ArrayList<>();
            }
            List<T> tempVals = new ArrayList<>(vals);
            for (T v : tempVals) {
                if (equal(v, val)) {
                    vals.remove(v);
                }
            }
            if (vals.isEmpty()) {
                log.info(" delete a pair key: {}, value: {} from storage: {}. The rest is empty.", key, val, storeName);
                removeKey(key);
            } else {
                memoryServices.put(key, vals);
                log.info(" delete a pair key: {}, value: {} from storage: {}", key, val, storeName);
                rewriteToFile(store.resolve(key), toFile(vals));
            }
            listenerHandler.onEvent(REMOVE_VAL, storeName, key, val);
            return true;
        } catch (IOException e) {
            log.info("exception when delete a pair key: {},value: {} from storage: {} ", key, val, storeName, e);
        } finally {
            lock.unlock();
        }
        return false;
    }

    public T getRand(String service) {
        List<T> res = get(service);
        return res.isEmpty() ? null : res.get(random.nextInt(res.size()));
    }

    public void clean() throws IOException {
        lock.lock();
        try {

            File[] files = store.toFile().listFiles();
            if (Objects.nonNull(files)) {
                for (File file : files) {
                    Files.deleteIfExists(file.toPath());
                }
            }
            memoryServices.clear();
            log.info("storage {} has been cleaned", store.getFileName().toString());
            listenerHandler.onEvent(CLEAN, storeName, null, null);
        } finally {
            lock.unlock();
        }
    }

    @PostConstruct
    protected void init() throws IOException {
        if (Files.notExists(store)) {
            Files.createDirectory(store);
        }
        try (Stream<Path> paths = Files.list(store)) {
            paths.forEach(p -> {
                String storeName = store.getFileName().toString();
                String key = p.getFileName().toString();
                try {
                    List<String> params = Files.readAllLines(p);
                    if (params.isEmpty()) {
                        log.info(" init storage: {}, key {} is empty and can't be initialized", storeName, key);
                    } else {
                        memoryServices.put(key, fromFile(params));
                        log.info("init storage: {}, key: {}, params:{}", storeName, key, params);
                    }
                } catch (IOException e) {
                    log.info(" error while initialization store: {}, key: {}", storeName, key, e);
                }
            });
        }
        listenerHandler.onEvent(INIT, storeName, null, null);
    }

    protected abstract List<T> fromFile(List<String> params);

    protected abstract List<String> toFile(List<T> params);

    protected abstract boolean equal(T left, T right);

    protected Path getStore() {
        return store;
    }

    protected Map<String, List<T>> getMemoryServices() {
        return new HashMap<>(memoryServices);
    }


    private void rewriteToFile(Path filePath, List<String> vals) throws IOException {
        Files.deleteIfExists(filePath);
        Files.createFile(filePath);
        Files.write(filePath, vals, WRITE);
    }
}
