package ie.home.msa.sandbox.discovery.server;

import ie.home.msa.messages.Service;
import ie.home.msa.messages.ServiceRegisterMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ServiceRegistrationHandler implements RegistrationHandler {
    private final ServiceRegistryFileStorage serviceRegistryFileStorage;

    @Autowired
    public ServiceRegistrationHandler(ServiceRegistryFileStorage serviceRegistryFolderStorage) {
        this.serviceRegistryFileStorage = serviceRegistryFolderStorage;
    }

    @Override
    public ServiceRegisterMessage handle(ServiceRegisterMessage message) {
        Service service = message.getService();
        serviceRegistryFileStorage.put(service.getName(), service.getAddress());
        return message;
    }
}
