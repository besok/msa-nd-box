package ie.home.msa.sandbox.discovery.client;

import ie.home.msa.messages.GetServiceMessage;
import ie.home.msa.messages.LogServiceMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class LogAggregator {
    private final Path logDir;

    @Value("${logs.log-service-name:none}")
    private String logServiceName;

    private final DiscoveryClient discoveryClient;
    private ExecutorService executor;

    public LogAggregator(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
        logDir = Paths.get(new ClassPathResource("service-logs").getPath());
        executor = Executors.newSingleThreadExecutor();
    }


    @PostConstruct
    public void scheduledThread() {
        if(!logServiceName.equals("none")) {
            this.executor.submit(() -> {
                while (true) {
                    try {
                        Thread.sleep(5000);
                        List<String> logs = lookAt();
                        if (!logs.isEmpty()) {
                            LogServiceMessage message = makeMessage(logs);
                            GetServiceMessage logger = discoveryClient.getAddress(logServiceName);
                            String address = logger.getBody().getAddress();
                            sendLogs(message, address);
                        }
                    } catch (Exception e) {
                        log.error(" error while sending logs ", e);
                    }
                }
            });
        }
    }

    private void sendLogs(LogServiceMessage message, String address) {
        ResponseEntity<Void> resp =
                discoveryClient.getRestTemplate().postForEntity("http://" + address + "/logs", message, Void.class);
        if (resp.getStatusCode().isError()) {
            log.error(" can't send logs to the logs-aggregator-service");
        } else {
            log.info(" send logs to the logs-aggregator-service");
        }
    }

    public LogServiceMessage makeMessage(List<String> logs) {
        LogServiceMessage message = new LogServiceMessage();
        message.setService(thisService());
        message.setVersion(1);
        message.setBody(LogServiceMessage.Logs.of(logs));
        return message;
    }

    private ie.home.msa.messages.Service thisService() {
        return ie.home.msa.messages.Service.of(discoveryClient.getServiceName(), discoveryClient.getServiceAddress());
    }

    public List<String> lookAt() throws IOException {
        try(Stream<Path> list = Files.list(logDir)) {
            List<Path> files = list.filter(this::isNotCurrentLogFile).collect(Collectors.toList());
            ArrayList<String> logs = new ArrayList<>();
            for (Path file : files) {
                logs.addAll(Files.readAllLines(file));
                log.info("log-file {} has been processed and will be removed ", file.getFileName().toString());
                Files.deleteIfExists(file);
            }
            return logs;
        }
    }

    private boolean isNotCurrentLogFile(Path p) {
        return !p.getFileName().toString().equals("service.log");
    }


}
