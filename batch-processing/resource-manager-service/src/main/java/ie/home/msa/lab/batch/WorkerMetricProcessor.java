package ie.home.msa.lab.batch;

import ie.home.msa.messages.Service;
import ie.home.msa.messages.ServiceMetricsMessage;
import ie.home.msa.sandbox.discovery.server.MetricHandler;

import java.util.Map;

public class WorkerMetricProcessor implements MetricHandler {
    @Override
    public ServiceMetricsMessage handle(ServiceMetricsMessage val) {
        Map<String, Integer> metricsMap = val.getBody().getMetrics();
        Integer integer = metricsMap.get("worker-busy");
        return null;
    }
}
