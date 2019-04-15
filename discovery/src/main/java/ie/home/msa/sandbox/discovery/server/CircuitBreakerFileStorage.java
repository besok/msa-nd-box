package ie.home.msa.sandbox.discovery.server;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ie.home.msa.sandbox.discovery.server.StorageListenerHandler.FileStorageType.*;

@Service
public class CircuitBreakerFileStorage extends AbstractFileStorage<CircuitBreakerData> {


    public CircuitBreakerFileStorage(StorageListenerHandler handler) {
        super(CIRCUIT_BREAKER.getName(),handler);
    }

    @Override
    protected List<CircuitBreakerData> fromFile(List<String> params) {
        return params.stream()
                .map(e -> e.split("="))
                .map(CircuitBreakerData::new)
                .collect(Collectors.toList());
    }

    @Override
    protected List<String> toFile(List<CircuitBreakerData> params) {
        return params.stream().map(CircuitBreakerData::toString)
                .collect(Collectors.toList());
    }

    @Override
    protected boolean equal(CircuitBreakerData left, CircuitBreakerData right) {
        return left.getAddress().equals(right.getAddress());
    }


    public List<CircuitBreakerData> getAllReady(String service) {
        return get(service).stream().filter(e -> e.getStatus().equals("ready")).collect(Collectors.toList());
    }

    public void turnOff(String service, String address) {
        int v = 0;
        List<CircuitBreakerData> cbList = get(service);
        if (Objects.nonNull(cbList)) {
            for (CircuitBreakerData cbData : cbList) {
                if (cbData.getAddress().equals(address)) {
                    v = cbData.getVersion();
                }
            }
        }
        v++;
        put(service, new CircuitBreakerData(address, "failed", v));
    }

    public void turnOn(String service, String address) {
        put(service, new CircuitBreakerData(address, "ready"));
    }


}
