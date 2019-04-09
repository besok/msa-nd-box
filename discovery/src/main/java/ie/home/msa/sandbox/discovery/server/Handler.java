package ie.home.msa.sandbox.discovery.server;

import ie.home.msa.messages.ServiceMetricsMessage;

public interface Handler {
    void handle(String service, ServiceMetricsMessage.Metrics metrics);
}
