package ie.home.msa.lab;

import ie.home.msa.sandbox.discovery.client.EnableDiscoveryClient;
import ie.home.msa.sandbox.discovery.server.EnableDiscoveryServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDiscoveryServer
public class DiscoveryAdminApp {
    public static void main(String[] args) {
        SpringApplication.run(DiscoveryAdminApp.class);
    }
}
