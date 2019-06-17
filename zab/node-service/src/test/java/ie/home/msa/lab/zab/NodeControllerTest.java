package ie.home.msa.lab.zab;

import ie.home.msa.messages.ZElectionMessage;
import ie.home.msa.messages.ZElectionMessageBuilder;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

@Ignore
public class NodeControllerTest {

    @Test
    public void processMessage() {
        ZElectionMessage message = ZElectionMessageBuilder.createInitMessage("zab-node-service", "10.0.75.1:60971", 1);
        new RestTemplate().postForEntity("http://10.0.75.1:60956/election",message,Void.class);
    }
}