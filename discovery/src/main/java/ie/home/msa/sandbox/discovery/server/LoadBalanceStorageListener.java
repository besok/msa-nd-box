package ie.home.msa.sandbox.discovery.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static ie.home.msa.sandbox.discovery.server.StorageListenerHandler.FileStorageType.*;

@Service
@Slf4j
public class LoadBalanceStorageListener implements StorageListener {
    private LoadBalanceResolver resolver;

    public LoadBalanceStorageListener(LoadBalanceResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public <T> void onEvent(Event event, String storage, String key, T val) {
        if (storage.equals(LOAD_BALANCER.getName())) {
            if (event == Event.PUT) {
                resolver.addService(key);
            } else if (event == Event.REMOVE_KEY) {
                resolver.removeService(key);
            }
        }
    }
}
