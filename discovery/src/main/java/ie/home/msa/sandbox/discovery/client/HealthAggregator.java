package ie.home.msa.sandbox.discovery.client;

import ie.home.msa.messages.MessageBuilder;
import ie.home.msa.messages.ServiceMetricsMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import static ie.home.msa.messages.ServiceStatus.*;

@Component
@Slf4j
public class HealthAggregator {
    private final List<Health> healthList;

    private AtomicInteger ver = new AtomicInteger(0);

    @Autowired
    public HealthAggregator(List<Health> healthList) {
        this.healthList = healthList;
    }

    public ServiceMetricsMessage checkHealth() {
        int v = ver.incrementAndGet();
        return collectMetrics()
                .map(m -> MessageBuilder.metricsMessage(m,v, READY))
                .orElse(MessageBuilder.metricsMessage(null,v, FAILED));
    }

    private Optional<ServiceMetricsMessage.Metrics> collectMetrics() {
        return healthList.stream()
                .map(Health::health)
                .reduce(ServiceMetricsMessage.Metrics::merge);
    }

}
