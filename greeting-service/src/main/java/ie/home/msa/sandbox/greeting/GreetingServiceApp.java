package ie.home.msa.sandbox.greeting;

import ie.home.msa.messages.sandbox.discovery.client.EnableDiscoveryClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootApplication
@EnableDiscoveryClient
public class GreetingServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(GreetingServiceApp.class);
    }
}
