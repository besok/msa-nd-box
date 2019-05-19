package ie.home.msa.lab.zab;

import ie.home.msa.sandbox.discovery.client.EnableDiscoveryClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDiscoveryClient
public class ZabNodeApp {
    public static void main(String[] args) {
        SpringApplication.run(ZabNodeApp.class);
    }
}
