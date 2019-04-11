package ie.home.msa.sandbox.discovery.server;

import ie.home.msa.messages.ServiceRegisterMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RetryApplicationRegistrationResolver implements RegistrationResolver {
    private final RetryFileStorage fileStorage;

    @Autowired
    public RetryApplicationRegistrationResolver(RetryFileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    @Override
    public ServiceRegisterMessage resolve(ServiceRegisterMessage message) {
        String retry = message.getBody().getProp("retry");
        if(retry.equals("false")){
            return message;
        }
        String name = message.getBody().getName();
        fileStorage.put(name,retry);
        log.info("retry service {} {}",name,retry);
        return message;
    }
}
