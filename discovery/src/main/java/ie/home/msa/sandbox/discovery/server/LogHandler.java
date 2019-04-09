package ie.home.msa.sandbox.discovery.server;

import ie.home.msa.messages.ServiceMessage;
import ie.home.msa.messages.ServiceMetricsMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class LogHandler implements Handler {

    @Override
    public void handle(String service, ServiceMetricsMessage.Metrics metrics) {
        Map<String, Integer> metricsMap = metrics.getMetrics();
        for (Map.Entry<String, Integer> metric : metricsMap.entrySet()) {
            log.info(" serv {} , metric {} , value {}", service, metric.getKey(), metric.getValue());
        }
    }
}
