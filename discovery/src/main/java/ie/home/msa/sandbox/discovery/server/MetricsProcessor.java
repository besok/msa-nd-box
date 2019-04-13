package ie.home.msa.sandbox.discovery.server;

import ie.home.msa.messages.ServiceMetricsMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MetricsProcessor {
    final List<MetricHandler> metricHandlers;

    @Autowired
    public MetricsProcessor(List<MetricHandler> metricHandlers) {
        this.metricHandlers = metricHandlers;
    }

    public ServiceMetricsMessage process(ServiceMetricsMessage metricsMessage){
        for (MetricHandler metricHandler : metricHandlers) {
            metricHandler.handle(metricsMessage);
        }
        return metricsMessage;
    }
}
