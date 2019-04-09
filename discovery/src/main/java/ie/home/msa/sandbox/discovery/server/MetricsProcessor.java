package ie.home.msa.sandbox.discovery.server;

import ie.home.msa.messages.ServiceMessage;
import ie.home.msa.messages.ServiceMetricsMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class MetricsProcessor {
    final List<Handler> handlers;

    @Autowired
    public MetricsProcessor(List<Handler> handlers) {
        this.handlers = handlers;
    }

    public void process(String service, ServiceMetricsMessage.Metrics metrics){
        for (Handler handler : handlers) {
            handler.handle(service,metrics);
        }
    }
}
