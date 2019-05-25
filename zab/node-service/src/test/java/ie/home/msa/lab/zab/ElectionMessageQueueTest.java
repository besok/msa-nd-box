package ie.home.msa.lab.zab;

import ie.home.msa.messages.ElectionMessage;
import ie.home.msa.messages.ElectionMessageBuilder;
import ie.home.msa.messages.Service;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class ElectionMessageQueueTest {

    @Test
    public void pop() {
        ElectionMessageQueue queue = new ElectionMessageQueue();

        for (int i = 0; i < 10; i++) {
        ElectionMessage electionMessage = ElectionMessageBuilder.createInitMessage("","",i);
            queue.push(electionMessage);
        }
        Optional<ElectionMessage> last= queue.pop();
        Assert.assertTrue(last.isPresent());
        Assert.assertEquals(9,last.get().getBody().getId());

    }




}