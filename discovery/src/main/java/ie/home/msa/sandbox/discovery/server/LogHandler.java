package ie.home.msa.sandbox.discovery.server;

import ie.home.msa.messages.ServiceMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
public class LogHandler implements Handler {

    @Override
    public void handle(String service, ServiceMessage.Metrics metrics) {
        Set<ServiceMessage.Metric> metricsSet = metrics.getMetrics();
        for (ServiceMessage.Metric metric : metricsSet) {
            log.info(" serv {} , metric {} , value {}", service, metric.getName(), metric.getValue());
        }
    }
}
