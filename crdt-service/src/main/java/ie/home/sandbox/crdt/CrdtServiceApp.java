package ie.home.sandbox.crdt;

import org.springframework.boot.SpringApplication;
import ie.home.msa.sandbox.discovery.client.EnableDiscoveryClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDiscoveryClient
public class CrdtServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(CrdtServiceApp.class);
    }
}
