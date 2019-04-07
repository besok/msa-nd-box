package ie.home.msa.sandbox.discovery.client;

import ie.home.msa.messages.ServiceMessage;
import org.springframework.stereotype.Component;

@Component
public class BasicHealth implements Health {
    @Override
    public ServiceMessage.Metric health() {

        return new ServiceMessage.Metric("pulse",1);
    }
}
