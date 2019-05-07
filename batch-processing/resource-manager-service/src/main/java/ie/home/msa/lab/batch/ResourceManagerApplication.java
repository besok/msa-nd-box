package ie.home.msa.lab.batch;

import ie.home.msa.sandbox.discovery.server.EnableServiceDiscoveryMediator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableServiceDiscoveryMediator
public class ResourceManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ResourceManagerApplication.class);
    }
}
