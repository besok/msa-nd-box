package ie.home.msa.sandbox.discovery.client;

import ie.home.msa.messages.ServiceMetricsMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HealthRestPoint {


    private final List<InitializationHandler> initializationHandlers;
    private final HealthAggregator aggregator;
    private final ApplicationRestarter restarter;

    public HealthRestPoint(List<InitializationHandler> initializationHandlers,
                           HealthAggregator aggregator, ApplicationRestarter restarter) {
        this.initializationHandlers = initializationHandlers;
        this.aggregator = aggregator;
        this.restarter = restarter;
    }

    @RequestMapping(path = "/health",method = RequestMethod.GET)
    public ServiceMetricsMessage health(){
        return aggregator.checkHealth();
    }

    @RequestMapping(path = "/init",method = RequestMethod.GET)
    public Boolean init(){
        boolean result = true;
        for (InitializationHandler initializationHandler : initializationHandlers) {
            if(!initializationHandler.initialization()){
                result = false;
            }
        }
        return result;
    }

    @RequestMapping(path = "/close",method = RequestMethod.GET)
    public void restart(){
        restarter.close();
    }
}
