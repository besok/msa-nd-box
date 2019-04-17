package ie.home.msa.sandbox.discovery.server;

import ie.home.msa.messages.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static ie.home.msa.messages.MessageBuilder.*;
import static ie.home.msa.messages.ServiceStatus.*;

@RestController
@Slf4j
public class ServiceController {

    private final ServiceRegistryFileStorage serviceRegistryFileStorage;
    private final CircuitBreakerFileStorage circuitBreakerStorage;
    private final ServiceRegistrator serviceRegistrator;
    private final LoadBalanceResolver loadBalanceResolver;

    @Autowired
    public ServiceController(ServiceRegistryFileStorage storage,
                             CircuitBreakerFileStorage circuitBreakerStorage,
                             ServiceRegistrator serviceRegistrator,
                             LoadBalanceResolver loadBalanceResolver) {
        this.serviceRegistryFileStorage = storage;
        this.circuitBreakerStorage = circuitBreakerStorage;
        this.serviceRegistrator = serviceRegistrator;
        this.loadBalanceResolver = loadBalanceResolver;
    }

    @RequestMapping(path = "/services/{service}", method = RequestMethod.GET)
    public GetServiceMessage getAddress(@PathVariable String service) {
        return loadBalanceResolver.resolve(service, getAddresses(service));
    }

    @RequestMapping(path = "/services/all/{service}", method = RequestMethod.GET)
    public GetAllNodesServiceMessage getAllAddresses(@PathVariable String service) {
        return MessageBuilder.nodesServiceMessage(service, getAddresses(service), READY);
    }

    @RequestMapping(path = "/services", method = RequestMethod.POST)
    public ServiceRegisterMessage register(@RequestBody ServiceRegisterMessage message) {
        return serviceRegistrator.register(message);
    }

    private List<String> getAddresses(@PathVariable String service) {
        List<String> addreses;
        if (circuitBreakerStorage.contains(service)) {
            addreses = circuitBreakerStorage.getAllReady(service)
                    .stream()
                    .map(CircuitBreakerData::getAddress)
                    .collect(Collectors.toList());
        } else {
            addreses = serviceRegistryFileStorage.get(service);
        }
        return addreses;
    }

}
