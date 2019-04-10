package ie.home.msa.sandbox.discovery.server;

import ie.home.msa.messages.ServiceMetricsMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class CircuitBreakerHandler implements Handler {
    private final AbstractSingleFileStorage storage;

    @Autowired
    public CircuitBreakerHandler(AbstractSingleFileStorage storage) {
        this.storage = storage;
    }

    @Override
    public void handle(String service, ServiceMetricsMessage.Metrics metrics) {
        Map<String, Integer> metricsMap = metrics.getMetrics();
        Integer integer = metricsMap.get("circuit-breaker");
        if(integer > 0){
            storage.turnOff(service);
            log.info(" service {} is turn off",service);
        }
    }
}
