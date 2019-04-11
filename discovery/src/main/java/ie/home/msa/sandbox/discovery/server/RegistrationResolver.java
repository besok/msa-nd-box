package ie.home.msa.sandbox.discovery.server;

import ie.home.msa.messages.ServiceRegisterMessage;

public interface RegistrationResolver {
    ServiceRegisterMessage resolve(ServiceRegisterMessage message);
}
