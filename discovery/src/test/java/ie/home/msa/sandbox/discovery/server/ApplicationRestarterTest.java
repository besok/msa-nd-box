package ie.home.msa.sandbox.discovery.server;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ApplicationRestarterTest {
    @Autowired
    private ApplicationRestarter restarter;
    @Autowired
    private RetryFileStorage fileStorage;

    @Test
    public void startApplication() throws IOException, InterruptedException {
        fileStorage.put("service","C:\\projects\\msa-nd-box\\greeting-service\\target\\greeting-service-1.0.jar");
        restarter.startApplication("service");
    }
}