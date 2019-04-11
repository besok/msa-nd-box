package ie.home.msa.sandbox.discovery.server;

import ie.home.msa.messages.MessageBuilder;
import ie.home.msa.messages.ServiceRegisterMessage;
import ie.home.msa.messages.ServiceStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class ServiceController {

    private final ServiceRegistryFolderStorage serviceRegistryFolderStorage;
    private final CircuitBreakerFileStorage circuitBreakerStorage;

    private final ServiceRegistrator serviceRegistrator;
    @Autowired
    public ServiceController(ServiceRegistryFolderStorage storage,
                             CircuitBreakerFileStorage circuitBreakerStorage,
                             ServiceRegistrator serviceRegistrator) {
        this.serviceRegistryFolderStorage = storage;
        this.circuitBreakerStorage = circuitBreakerStorage;
        this.serviceRegistrator = serviceRegistrator;
    }

    @RequestMapping(path = "/services/{service}", method = RequestMethod.GET)
    public ServiceRegisterMessage getAddress(@PathVariable String service) {
        if (circuitBreakerStorage.get(service)) {
           return MessageBuilder.serviceMessage(service, "",ServiceStatus.FAILED);
        }
        return MessageBuilder.serviceMessage(service, serviceRegistryFolderStorage.get(service),ServiceStatus.READY);
    }

    @RequestMapping(path = "/services", method = RequestMethod.POST)
    public ServiceRegisterMessage register(@RequestBody ServiceRegisterMessage message) {
        return serviceRegistrator.register(message);
    }


}
