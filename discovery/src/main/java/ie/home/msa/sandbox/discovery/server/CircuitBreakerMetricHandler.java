package ie.home.msa.sandbox.discovery.server;

import ie.home.msa.messages.Service;
import lombok.extern.slf4j.Slf4j;
import ie.home.msa.messages.ServiceMetricsMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class CircuitBreakerMetricHandler implements MetricHandler {
    private final CircuitBreakerFileStorage storage;

    @Autowired
    public CircuitBreakerMetricHandler(CircuitBreakerFileStorage storage) {
        this.storage = storage;
    }

    @Override
    public ServiceMetricsMessage handle(ServiceMetricsMessage metricsMessage) {
        Map<String, Integer> metricsMap = metricsMessage.getBody().getMetrics();
        Integer integer = metricsMap.get("circuit-breaker");
        if(integer > 0){
            Service service = metricsMessage.getService();
            storage.turnOff(service.getName(),service.getAddress());
        }
        return metricsMessage;
    }
}
