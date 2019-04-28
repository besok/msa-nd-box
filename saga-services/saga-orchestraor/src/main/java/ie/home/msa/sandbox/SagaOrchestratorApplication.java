package ie.home.msa.sandbox;

import ie.home.msa.sandbox.discovery.client.EnableDiscoveryClient;
import ie.home.msa.sandbox.saga.orchestrator.EnableSagaOrchestrator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDiscoveryClient
@EnableSagaOrchestrator
public class SagaOrchestratorApplication {
    public static void main(String[] args) {
        SpringApplication.run(SagaOrchestratorApplication.class);
    }

}
