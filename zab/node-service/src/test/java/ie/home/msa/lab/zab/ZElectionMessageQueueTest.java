package ie.home.msa.lab.zab;

import ie.home.msa.messages.ZElectionMessage;
import ie.home.msa.messages.ZElectionMessageBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class ZElectionMessageQueueTest {

    @Test
    public void pop() {
        ElectionMessageQueue queue = new ElectionMessageQueue();

        for (int i = 0; i < 10; i++) {
        ZElectionMessage electionMessage = ZElectionMessageBuilder.createInitMessage("","",i);
            queue.push(electionMessage);
        }
        Optional<ZElectionMessage> last= queue.pop();
        Assert.assertTrue(last.isPresent());
        Assert.assertEquals(9,last.get().getBody().getId());

    }




}