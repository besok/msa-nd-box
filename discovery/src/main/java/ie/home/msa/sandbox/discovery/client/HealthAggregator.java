package ie.home.msa.sandbox.discovery.client;

import ie.home.msa.messages.EnvelopeBuilder;
import ie.home.msa.messages.ServiceEnvelope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ie.home.msa.messages.ServiceMessage.*;
import static ie.home.msa.messages.ServiceMessage.Metrics.*;

@Component
@Slf4j
public class HealthAggregator {
    private final List<Health> healthList;

    @Autowired
    public HealthAggregator(List<Health> healthList) {
        this.healthList = healthList;
    }

    public ServiceEnvelope checkHealth() {
        return EnvelopeBuilder.serviceMetricsEnvelope(from(collectMetrics()));
    }

    private Set<Metric> collectMetrics() {
        Set<Metric> metrics = healthList.stream()
                .map(Health::health)
                .collect(Collectors.toSet());
        log.info(" service has collected metrics {} ",metrics.size());
        return metrics;
    }

}
