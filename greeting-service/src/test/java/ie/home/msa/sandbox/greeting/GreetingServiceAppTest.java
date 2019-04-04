package ie.home.msa.sandbox.greeting;

import ie.home.msa.messages.sandbox.discovery.client.DiscoveryClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GreetingServiceAppTest {
    @Autowired
    DiscoveryClient client;

    @Test
    public void test() {
        String service1 = client.getAddress("service1");
        System.out.println(service1);
    }
}