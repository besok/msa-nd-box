package ie.home.msa.messages;

import java.util.Map;

public class EnvelopeBuilder {
    public static ServiceEnvelope serviceMetricsEnvelope(ServiceMessage.Metrics metrics){
        return new ServiceEnvelope(metrics);
    }
}
