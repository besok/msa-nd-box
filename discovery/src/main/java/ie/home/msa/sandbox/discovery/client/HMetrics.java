package ie.home.msa.sandbox.discovery.client;

import ie.home.msa.messages.ServiceMessage;
import ie.home.msa.messages.ServiceMetricsMessage;

import static ie.home.msa.messages.ServiceMetricsMessage.*;

public interface HMetrics {
    ServiceMetricsMessage.Metrics metric();
}
