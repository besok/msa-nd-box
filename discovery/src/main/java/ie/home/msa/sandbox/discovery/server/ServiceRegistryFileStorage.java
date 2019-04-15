package ie.home.msa.sandbox.discovery.server;

import org.springframework.stereotype.Service;

import java.util.List;

import static ie.home.msa.sandbox.discovery.server.StorageListenerHandler.FileStorageType.SERVICE_REGISTRY;

@Service
public class ServiceRegistryFileStorage extends PlainValueFileStorage{

    public ServiceRegistryFileStorage(StorageListenerHandler handler) {
        super(SERVICE_REGISTRY.getName(),handler);
    }

}
