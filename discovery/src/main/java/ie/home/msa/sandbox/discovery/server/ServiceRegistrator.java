package ie.home.msa.sandbox.discovery.server;

import ie.home.msa.messages.ServiceRegisterMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ServiceRegistrator {
    private final List<RegistrationHandler> handlers;

    @Autowired
    public ServiceRegistrator(List<RegistrationHandler> handlers) {
        this.handlers = handlers;
    }

    public ServiceRegisterMessage register(ServiceRegisterMessage message){
        for (RegistrationHandler handler : handlers) {
            handler.handle(message);
        }
        return message;
    }
}
