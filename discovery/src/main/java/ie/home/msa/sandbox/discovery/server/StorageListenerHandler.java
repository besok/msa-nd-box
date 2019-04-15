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
            listener.onEvent(event, storage, key, val);
        }
    }

    public enum FileStorageType {
        CIRCUIT_BREAKER("circuit_breaker_storage"),
        SERVICE_REGISTRY("service_registry_storage"),
        LOAD_BALANCER("load_balancer_storage"),
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
