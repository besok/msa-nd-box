package ie.home.msa.lab.zab;

import ie.home.msa.messages.ElectionMessage;
import ie.home.msa.zab.ZNotification;
import ie.home.msa.zab.ZVote;
import ie.home.msa.zab.Zid;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ZabUtilsTest {

    @Test
    public void getTimeout() {
        int initTimeout = 10;
        int threshold = 30;

        initTimeout = ZabUtils.setTimeout(initTimeout, threshold);
        Assert.assertEquals(initTimeout,20);

        initTimeout = ZabUtils.setTimeout(initTimeout, threshold);
        Assert.assertEquals(initTimeout,30);

        initTimeout = ZabUtils.setTimeout(initTimeout, threshold);
        Assert.assertEquals(initTimeout,30);
    }

    @Test
    public void checkQuorum() {
        ElectionMessage electionMessage = new ElectionMessage();
        Map<Integer, ElectionMessage> map = new HashMap<>();
        ZVote zVote = new ZVote(1,new Zid(1,1));
        electionMessage.setBody(new ZNotification(zVote,1,1));
        map.put(1,electionMessage);
        map.put(2,electionMessage);
        map.put(3,electionMessage);
        map.put(4,electionMessage);
        map.put(5,electionMessage);

        Assert.assertTrue(ZabUtils.checkQuorum(zVote, map, 5));
        Assert.assertTrue(ZabUtils.checkQuorum(zVote, map, 9));
        Assert.assertFalse(ZabUtils.checkQuorum(zVote, map, 10));

        map.clear();
        map.put(1,electionMessage);
        map.put(2,electionMessage);
        map.put(3,electionMessage);
        map.put(4,electionMessage);

        Assert.assertTrue(ZabUtils.checkQuorum(zVote, map, 6));
    }

    @Test
    public void filter() {
        String[] filteredNodes = ZabUtils.filter("123:123", new String[]{"123:123", "123:12", "123:1234"});
        Assert.assertArrayEquals(filteredNodes, new String[]{"123:12", "123:1234"});

    }
}