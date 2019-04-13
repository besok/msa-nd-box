package ie.home.msa.sandbox.discovery.server;

import ie.home.msa.messages.Service;
import ie.home.msa.messages.ServiceRegisterMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CircuitBreakerRegistrationHandler implements RegistrationHandler {
    private final CircuitBreakerFileStorage storage;

    @Autowired
    public CircuitBreakerRegistrationHandler(CircuitBreakerFileStorage storage) {
        this.storage = storage;
    }

    @Override
    public ServiceRegisterMessage handle(ServiceRegisterMessage message) {
        Service service = message.getService();
        String servName = service.getName();
        String address = service.getAddress();
        if (Boolean.valueOf(message.getBody().getProperty("circuit-breaker"))) {
            storage.put(servName,new CircuitBreakerData(address,"ready"));
            storage.turnOn(servName,address);
        }
        else{
            storage.removeKey(servName);
        }
        return message;
    }
}
