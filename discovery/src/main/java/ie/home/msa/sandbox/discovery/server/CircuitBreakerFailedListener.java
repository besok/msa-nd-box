package ie.home.msa.sandbox.discovery.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import static ie.home.msa.sandbox.discovery.server.StorageListenerHandler.FileStorageType.CIRCUIT_BREAKER;

@Service
@Slf4j
public class CircuitBreakerFailedListener implements StorageListener {


    private final CircuitBreakerFileStorage cbStorage;
    private final ServiceRegistryFileStorage srStorage;
    private final LoadBalancerFileStorage lbStorage;

    public CircuitBreakerFailedListener(
            @Lazy CircuitBreakerFileStorage cbStorage,
            @Lazy ServiceRegistryFileStorage srStorage,
            @Lazy LoadBalancerFileStorage lbStorage) {
        this.cbStorage = cbStorage;
        this.srStorage = srStorage;
        this.lbStorage = lbStorage;
    }


    @Override
    public <T> void onEvent(Event event, String service, String key, T val) {
        if(service.equals(CIRCUIT_BREAKER.getName()) && (event == Event.PUT)){
            CircuitBreakerData data = (CircuitBreakerData) val;
            String address = data.getAddress();
            if(data.getVersion() > 5){
                log.info("service {} with address {} is unavailable long time. The service will be removed",
                        service,address);
                cbStorage.removeVal(key,data);
                srStorage.removeVal(key,address);
                lbStorage.removeVal(key,new LoadBalancerData(address,""));
            }
        }
    }
}
