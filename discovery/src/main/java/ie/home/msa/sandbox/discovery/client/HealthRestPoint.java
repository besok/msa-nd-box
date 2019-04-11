package ie.home.msa.sandbox.discovery.client;

import ie.home.msa.messages.ServiceMetricsMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthRestPoint {

    private final HealthAggregator aggregator;
    private final ApplicationRestarter restarter;
    @Autowired
    public HealthRestPoint(HealthAggregator aggregator, ApplicationRestarter restarter) {
        this.aggregator = aggregator;
        this.restarter = restarter;
    }

    @RequestMapping(path = "/health",method = RequestMethod.GET)
    public ServiceMetricsMessage health(){
        return aggregator.checkHealth();
    }


    @RequestMapping(path = "/close",method = RequestMethod.GET)
    public void restart(){
        restarter.close();
    }
}
