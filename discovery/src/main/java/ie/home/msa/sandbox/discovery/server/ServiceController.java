package ie.home.msa.sandbox.discovery.server;

import ie.home.msa.messages.ServiceRegisterMessage;
import ie.home.msa.messages.ServiceStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static ie.home.msa.messages.MessageBuilder.*;
import static ie.home.msa.messages.ServiceStatus.*;

@RestController
@Slf4j
public class ServiceController {

    private final ServiceRegistryFileStorage serviceRegistryFileStorage;
    private final CircuitBreakerFileStorage circuitBreakerStorage;

    private final ServiceRegistrator serviceRegistrator;

    @Autowired
    public ServiceController(ServiceRegistryFileStorage storage,
                             CircuitBreakerFileStorage circuitBreakerStorage,
                             ServiceRegistrator serviceRegistrator) {
        this.serviceRegistryFileStorage = storage;
        this.circuitBreakerStorage = circuitBreakerStorage;
        this.serviceRegistrator = serviceRegistrator;
    }

    @RequestMapping(path = "/services/{service}", method = RequestMethod.GET)
    public ServiceRegisterMessage getAddress(@PathVariable String service) {
        if (circuitBreakerStorage.contains(service)) {
            return circuitBreakerStorage
                    .getOneReady(service)
                    .map(e -> serviceMessage(service, e.getAddress(), READY))
                    .orElseGet(() -> serviceMessage(service, "", FAILED));
        }
        return serviceMessage(service, serviceRegistryFileStorage.getRand(service), READY);
    }

    @RequestMapping(path = "/services", method = RequestMethod.POST)
    public ServiceRegisterMessage register(@RequestBody ServiceRegisterMessage message) {
        return serviceRegistrator.register(message);
    }

}
