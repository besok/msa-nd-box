package ie.home.msa.sandbox.discovery.client;

import ie.home.msa.messages.ServiceEnvelope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthRestPoint {

    private final HealthAggregator aggregator;

    @Autowired
    public HealthRestPoint(HealthAggregator aggregator) {
        this.aggregator = aggregator;
    }

    @RequestMapping(path = "/health",method = RequestMethod.GET)
    public ServiceEnvelope health(){
        return aggregator.checkHealth();
    }
}
