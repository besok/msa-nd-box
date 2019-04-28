package ie.home.msa.sandbox.discovery.client;

import org.springframework.stereotype.Component;

import static ie.home.msa.messages.ServiceMetricsMessage.*;

@Component
public class PulseMetric implements HMetrics {

    @Override
    public Metrics metric() {
        return Metrics.single("pulse",1);
    }
}
