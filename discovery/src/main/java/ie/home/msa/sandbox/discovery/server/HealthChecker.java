package ie.home.msa.sandbox.discovery.server;

import ie.home.msa.messages.ServiceMetricsMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class HealthChecker {
    private ScheduledExecutorService executorService;
    private RestTemplate restTemplate;

    private final ServiceRegistryFileStorage storage;
    private final CircuitBreakerFileStorage circuitBreakerStorage;
    private final MetricsProcessor processor;


    @Autowired
    public HealthChecker(ServiceRegistryFileStorage storage,
                         CircuitBreakerFileStorage circuitBreakerStorage,
                         MetricsProcessor processor) {
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
            try {
                Map<String, List<String>> services = storage.getMemoryServices();
                for (Map.Entry<String, List<String>> serviceEntry : services.entrySet()) {
                    String serv = serviceEntry.getKey();
                    List<String> addr = new ArrayList<>(serviceEntry.getValue());
                    for (String a : addr) {
                        String url = "http://" + a + "/health";
                        try {
                            ResponseEntity<ServiceMetricsMessage> r = restTemplate.getForEntity(url, ServiceMetricsMessage.class);
                            if (r.getStatusCode().isError()) {
                                log.info(" service {} is unavailable , e {}", serv, r.getStatusCode().getReasonPhrase());
                            } else {
                                ServiceMetricsMessage message = r.getBody();
                                log.info(" service {} is available {} ", serv, a);
                                processor.process(message);
                            }
                        } catch (Exception e) {
                            log.error("service {} at url {} is unavailable, exception: {}", serv, a, e.getMessage());
                            circuitBreakerStorage.turnOff(serv, a);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("common error ",e);
            }
        };
    }
}
