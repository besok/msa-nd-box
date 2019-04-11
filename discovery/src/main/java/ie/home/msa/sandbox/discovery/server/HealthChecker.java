package ie.home.msa.sandbox.discovery.server;

import ie.home.msa.messages.ServiceMetricsMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class HealthChecker {
    private ScheduledExecutorService executorService;
    private final ServiceRegistryFolderStorage storage;
    private final CircuitBreakerFileStorage circuitBreakerStorage;
    private RestTemplate restTemplate;
    private final MetricsProcessor processor;

    @Autowired
    public HealthChecker(ServiceRegistryFolderStorage storage, CircuitBreakerFileStorage circuitBreakerStorage, MetricsProcessor processor) {
        this.storage = storage;
        this.circuitBreakerStorage = circuitBreakerStorage;
        this.processor = processor;
        this.restTemplate = new RestTemplate();
        this.executorService = new ScheduledThreadPoolExecutor(1);
    }

    @PostConstruct
    public void init() {
        executorService.scheduleAtFixedRate(checkStatus(), 5, 10, TimeUnit.SECONDS);
    }

    private Runnable checkStatus() {
        return () -> {
            Map<String, String> services = storage.getServices();
            for (Map.Entry<String, String> serviceEntry : services.entrySet()) {
                String serv = serviceEntry.getKey();
                String addr = serviceEntry.getValue();
                String url = "http://" + addr + "/health";
                try {
                    ResponseEntity<ServiceMetricsMessage> r = restTemplate.getForEntity(url, ServiceMetricsMessage.class);
                    if (r.getStatusCode().isError()) {
                        log.info(" service {} is unavailable , e {}", serv, r.getStatusCode().getReasonPhrase());
                    } else {
                        ServiceMetricsMessage message = r.getBody();
                        log.info(" service {} is available {} ", serv,addr);
                        processor.process(serv, message.getBody());
                    }
                } catch (Exception e) {
                    log.info("service {} at url {} is unavailable ", serv, addr);
                    circuitBreakerStorage.turnOff(serv);
                }
            }
        };
    }
}
