package ie.home.msa.messages;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class AbstractEnvelopeTest {

    @Test
    public void next() {
        ServiceEnvelope envelope = EnvelopeBuilder.serviceEnvelope("test");
        envelope.next();
        envelope.next();
        envelope.next();

        Assert.assertEquals(envelope.message().getData(),"test");
        Assert.assertEquals(envelope.getVersion(),3);
        Assert.assertEquals(envelope.getState(), AbstractEnvelope.State.READY);
    }
}