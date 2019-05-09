package ie.home.msa.sandbox.logs;

import ie.home.msa.sandbox.discovery.client.EnableDiscoveryClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDiscoveryClient
public class LogsApplication {
    public static void main(String[] args) {
        SpringApplication.run(LogsApplication.class);
    }
}
