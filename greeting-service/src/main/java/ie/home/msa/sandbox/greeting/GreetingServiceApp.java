package ie.home.msa.sandbox.greeting;

import ie.home.msa.sandbox.discovery.client.EnableDiscoveryClient;
import ie.home.msa.sandbox.replication.EnableConflictFreeReplicatedData;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@EnableDiscoveryClient
@EnableConflictFreeReplicatedData
public class GreetingServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(GreetingServiceApp.class,args);
    }
}
