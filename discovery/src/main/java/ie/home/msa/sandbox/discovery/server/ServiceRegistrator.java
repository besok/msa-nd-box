package ie.home.msa.sandbox.discovery.server;

import ie.home.msa.messages.ServiceRegisterMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ServiceRegistrator {
    private final List<RegistrationResolver> resolvers;

    @Autowired
    public ServiceRegistrator(List<RegistrationResolver> resolvers) {
        this.resolvers = resolvers;
    }

    public ServiceRegisterMessage register(ServiceRegisterMessage message){
        for (RegistrationResolver resolver : resolvers) {
            resolver.resolve(message);
        }
        return message;
    }
}
