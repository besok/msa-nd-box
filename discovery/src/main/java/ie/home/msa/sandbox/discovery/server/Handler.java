package ie.home.msa.sandbox.discovery.server;

import ie.home.msa.messages.ServiceMessage;

public interface Handler {
    void handle(String service, ServiceMessage.Metrics metrics);
}
