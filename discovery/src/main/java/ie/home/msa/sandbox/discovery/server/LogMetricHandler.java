package ie.home.msa.sandbox.discovery.server;

import ie.home.msa.messages.Service;
import ie.home.msa.messages.ServiceMetricsMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class LogMetricHandler implements MetricHandler {

    @Override
    public ServiceMetricsMessage  handle(ServiceMetricsMessage message) {
        Map<String, Integer> metricsMap = message.getBody().getMetrics();
        Service service = message.getService();
        for (Map.Entry<String, Integer> metric : metricsMap.entrySet()) {
            log.info(" serv {} , address {},  metric {} , value {}", service.getName(),service.getAddress(),
                    metric.getKey(), metric.getValue());
        }
        return message;
    }
}
