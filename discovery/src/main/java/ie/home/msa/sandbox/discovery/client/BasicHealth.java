package ie.home.msa.sandbox.discovery.client;

import ie.home.msa.messages.ServiceMetricsMessage;
import org.springframework.stereotype.Component;

import static ie.home.msa.messages.ServiceMetricsMessage.*;

@Component
public class BasicHealth implements Health {
    @Override
    public Metrics health() {
        return Metrics.single("pulse",1);
    }
}
