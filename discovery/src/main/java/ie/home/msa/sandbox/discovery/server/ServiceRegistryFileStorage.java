package ie.home.msa.sandbox.discovery.server;

import org.springframework.stereotype.Service;

import java.util.List;

import static ie.home.msa.sandbox.discovery.server.StorageListenerHandler.FileStorageType.SERVICE_REGISTRY;

@Service
public class ServiceRegistryFileStorage extends AbstractFileStorage<String>{


    public ServiceRegistryFileStorage(StorageListenerHandler handler) {

        super(SERVICE_REGISTRY.getName(),handler);
    }


    @Override
    protected List<String> fromFile(List<String> params) {
        return params;
    }

    @Override
    protected List<String> toFile(List<String> params) {
        return params;
    }

    @Override
    protected boolean equal(String left, String right) {
        return left.equals(right);
    }
}
