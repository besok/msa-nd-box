package ie.home.msa.messages;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class AbstractEnvelopeTest {

    @Test
    public void next() {
        Map<String,Integer> map = new HashMap<>();
        map.put("data",1);
        ServiceEnvelope envelope = EnvelopeBuilder.serviceMetricsEnvelope(ServiceMessage.Metrics.from(map));
        envelope.next();
        envelope.next();
        envelope.next();

        Assert.assertEquals(envelope.getMessage().getData().getMetrics()
                .iterator().next().getValue(),1);
        Assert.assertEquals(envelope.getVersion(),3);
        Assert.assertEquals(envelope.getState(), AbstractEnvelope.State.READY);
    }
}