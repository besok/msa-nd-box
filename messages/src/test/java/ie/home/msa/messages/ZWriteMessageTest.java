package ie.home.msa.messages;

import ie.home.msa.zab.WriteMessage;
import ie.home.msa.zab.WriteStatus;
import ie.home.msa.zab.Zid;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class ZWriteMessageTest {

    @Test
    public void equals() {
        String t = "+";
        ZWriteMessage m1 = new ZWriteMessage();
        m1.setBody(new WriteMessage(new Zid(),t));
        m1.setService(Service.of("",""));
        m1.setStatus(WriteStatus.INCOME);
        ZWriteMessage m2 = new ZWriteMessage();
        m2.setBody(new WriteMessage(new Zid(),t));
        m2.setService(Service.of("",""));
        m2.setStatus(WriteStatus.INCOME);
        Assert.assertEquals(m1,m2);
    }
}