package ie.home.msa.sandbox.discovery.server;

import ie.home.msa.messages.ServiceRegisterMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ServiceRegistrator {
    private final List<RegistrationHandler> resolvers;

    @Autowired
    public ServiceRegistrator(List<RegistrationHandler> resolvers) {
        this.resolvers = resolvers;
    }

    public ServiceRegisterMessage register(ServiceRegisterMessage message){
        for (RegistrationHandler resolver : resolvers) {
            resolver.handle(message);
        }
        return message;
    }
}
