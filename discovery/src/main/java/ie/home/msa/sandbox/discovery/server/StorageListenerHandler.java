package ie.home.msa.sandbox.discovery.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class StorageListenerHandler {
    private final List<StorageListener> storageListeners;

    @Autowired
    public StorageListenerHandler(List<StorageListener> storageListeners) {
        this.storageListeners = storageListeners;
    }

    public <T> void onEvent(StorageListener.Event event, String storage, String key, T val) {
        for (StorageListener listener : storageListeners) {
            String l = listener.getClass().getSimpleName();
            log.info("on event: {}, listener: {}, storage {},  key {}, val {}", event, l, storage, key, val);
            listener.onEvent(event, storage, key, val);
        }
    }

    public enum FileStorageType {
        CIRCUIT_BREAKER("circuit_breaker"),
        SERVICE_REGISTRY("service_registry")
        ;
        private String name;

        FileStorageType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
