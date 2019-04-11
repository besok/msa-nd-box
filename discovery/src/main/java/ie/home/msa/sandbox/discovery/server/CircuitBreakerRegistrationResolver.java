package ie.home.msa.sandbox.discovery.server;

import ie.home.msa.messages.ServiceRegisterMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CircuitBreakerRegistrationResolver implements RegistrationResolver {
    private final CircuitBreakerFileStorage storage;

    @Autowired
    public CircuitBreakerRegistrationResolver(CircuitBreakerFileStorage storage) {
        this.storage = storage;
    }

    @Override
    public ServiceRegisterMessage resolve(ServiceRegisterMessage message) {
        ServiceRegisterMessage.Service serv = message.getBody();
        String servName = serv.getName();
        if (Boolean.valueOf(serv.getProp("circuit-breaker"))) {
            storage.put(servName,true);
            storage.turnOn(servName);
        }
        else{
            storage.remove(servName);
        }
        return message;
    }
}
