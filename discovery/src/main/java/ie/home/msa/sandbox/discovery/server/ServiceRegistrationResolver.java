package ie.home.msa.sandbox.discovery.server;

import ie.home.msa.messages.ServiceRegisterMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ServiceRegistrationResolver implements RegistrationResolver {
    private final ServiceRegistryFolderStorage serviceRegistryFolderStorage;

    @Autowired
    public ServiceRegistrationResolver(ServiceRegistryFolderStorage serviceRegistryFolderStorage) {
        this.serviceRegistryFolderStorage = serviceRegistryFolderStorage;
    }

    @Override
    public ServiceRegisterMessage resolve(ServiceRegisterMessage message) {
        ServiceRegisterMessage.Service serv = message.getBody();
        String servName = serv.getName();
        serviceRegistryFolderStorage.put(servName, serv.getAddress());
        return message;
    }
}
