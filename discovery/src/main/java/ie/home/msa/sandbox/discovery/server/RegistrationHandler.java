package ie.home.msa.sandbox.discovery.server;

import ie.home.msa.messages.ServiceRegisterMessage;

public interface RegistrationHandler extends Handler<ServiceRegisterMessage,ServiceRegisterMessage> {}
