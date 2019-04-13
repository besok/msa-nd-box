package ie.home.msa.sandbox.discovery.client;

import org.springframework.stereotype.Component;

@Component
public class PulseMetric implements HMetrics {

    @Override
    public ie.home.msa.messages.ServiceMetricsMessage.Metrics metric() {
        return ie.home.msa.messages.ServiceMetricsMessage.Metrics.single("pulse",1);
    }
}
