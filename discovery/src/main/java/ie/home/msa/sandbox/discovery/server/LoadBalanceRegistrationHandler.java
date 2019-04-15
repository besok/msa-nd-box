package ie.home.msa.sandbox.discovery.server;

import ie.home.msa.messages.ServiceRegisterMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoadBalanceRegistrationHandler implements RegistrationHandler {
    private final LoadBalancerFileStorage storage;

    @Autowired
    public LoadBalanceRegistrationHandler(LoadBalancerFileStorage storage) {
        this.storage = storage;
    }


    @Override
    public ServiceRegisterMessage handle(ServiceRegisterMessage message) {
        ie.home.msa.messages.Service service = message.getService();
        String servName = service.getName();
        String address = service.getAddress();
        String strategy = message.getBody().getProperty("load-balance-strategy");
        if (strategy != null) {
            storage.put(servName,new LoadBalancerData(strategy,address));
        }
        else{
            storage.removeKey(servName);
        }
        return message;
    }
}
