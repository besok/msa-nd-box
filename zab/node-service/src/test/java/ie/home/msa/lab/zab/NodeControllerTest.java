package ie.home.msa.lab.zab;

import ie.home.msa.messages.ElectionMessage;
import ie.home.msa.messages.ElectionMessageBuilder;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.*;

@Ignore
public class NodeControllerTest {

    @Test
    public void processMessage() {
        ElectionMessage message = ElectionMessageBuilder.createInitMessage("zab-node-service", "10.0.75.1:60971", 1);
        new RestTemplate().postForEntity("http://10.0.75.1:60956/election",message,Void.class);
    }
}