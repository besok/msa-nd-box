package ie.home.msa.sandbox.logger;

import ie.home.msa.sandbox.discovery.client.EnableDiscoveryClient;
import ie.home.msa.sandbox.saga.EnableSaga;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDiscoveryClient
@EnableSaga
public class LoggerApplication {
    public static void main(String[] args) {
        SpringApplication.run(LoggerApplication.class);
    }
}
