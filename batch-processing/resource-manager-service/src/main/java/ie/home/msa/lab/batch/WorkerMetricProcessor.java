package ie.home.msa.lab.batch;

import ie.home.msa.messages.ServiceMetricsMessage;
import ie.home.msa.sandbox.discovery.server.MetricHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class WorkerMetricProcessor implements MetricHandler {

    private final WorkerManager workerManager;

    public WorkerMetricProcessor(WorkerManager workerManager) {
        this.workerManager = workerManager;
    }

    @Override
    public ServiceMetricsMessage handle(ServiceMetricsMessage val) {
        Map<String, Integer> metricsMap = val.getBody().getMetrics();
        int integer = metricsMap.get("worker-busy");
        if (integer == 0) {
            String address = val.getService().getAddress();
            if (!workerManager.isQueueTaskEmpty()) {
                workerManager.sendTaskToWorker(address);
            } else {
                workerManager.removeWorker(address);
            }
        }
        return val;
    }
}
