package ie.home.msa.sandbox.discovery.client;

import ie.home.msa.messages.ServiceMetricsMessage;
import org.springframework.stereotype.Component;

@Component
public class BasicHealth implements Health {
    @Override
    public ServiceMetricsMessage.Metrics health() {
        return ServiceMetricsMessage.Metrics.single("pulse",1);
    }
}
