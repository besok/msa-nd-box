package ie.home.msa.sandbox.discovery.client;

import ie.home.msa.messages.ServiceMetricsMessage;
import org.springframework.stereotype.Component;

@Component
public class CircuitBreakerHealth implements HMetrics {
    @Override
    public ServiceMetricsMessage.Metrics metric() {
        return ServiceMetricsMessage.Metrics.single(
                "circuit-breaker",
                CircuitBreakerMethodStore.checkTroubles() ? 1 : 0);
    }
}
