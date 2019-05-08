package ie.home.msa.lab.batch.worker;

import ie.home.msa.messages.ServiceMetricsMessage;
import ie.home.msa.sandbox.discovery.client.HMetrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkMetric implements HMetrics {

    private final JobService jobService;

    @Autowired
    public WorkMetric(JobService jobService) {
        this.jobService = jobService;
    }


    @Override
    public ServiceMetricsMessage.Metrics metric() {
        return ServiceMetricsMessage.Metrics.single("worker-busy",jobService.getWork());
    }
}
