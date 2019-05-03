package ie.home.msa.sandbox.discovery.client;

import ie.home.msa.messages.ServiceMetricsMessage;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HealthRestPoint {


    private final HealthAggregator aggregator;
    private final ApplicationRestarter restarter;

    public HealthRestPoint(HealthAggregator aggregator, ApplicationRestarter restarter) {
        this.aggregator = aggregator;
        this.restarter = restarter;
    }

    @RequestMapping(path = "/health",method = RequestMethod.GET)
    public ServiceMetricsMessage health(){
        return aggregator.checkHealth();
    }

    @RequestMapping(path = "/init",method = RequestMethod.GET)
    public Boolean init(){
        return restarter.init();
    }

    @RequestMapping(path = "/close",method = RequestMethod.GET)
    public boolean close(){
        return restarter.close();
    }
    @RequestMapping(path = "/close/now",method = RequestMethod.GET)
    public boolean closeImmediately(){
        return restarter.closeImmediately();
    }
}
