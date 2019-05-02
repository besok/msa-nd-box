package ie.home.msa.lab.batch;

import ie.home.msa.sandbox.discovery.client.EnableDiscoveryClient;
import ie.home.msa.sandbox.discovery.server.EnableDiscoveryServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDiscoveryClient
@EnableDiscoveryServer
public class ResourceManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ResourceManagerApplication.class);
    }
}
