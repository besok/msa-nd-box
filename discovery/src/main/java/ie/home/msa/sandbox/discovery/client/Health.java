package ie.home.msa.sandbox.discovery.client;

import ie.home.msa.messages.ServiceMessage;

public interface Health {
    ServiceMessage.Metric health();
}
