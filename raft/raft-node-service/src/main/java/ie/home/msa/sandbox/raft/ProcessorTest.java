package ie.home.msa.sandbox.raft;


import ie.home.msa.sandbox.discovery.client.DiscoveryClient;
import org.junit.Test;

public class ProcessorTest {

    @Test
    public void setNewCommitIdx() {
        Processor processor = new Processor(new DiscoveryClient(null));
        processor.setNewCommitIdx(new int[]{2,2,3,3,3});
    }
}
