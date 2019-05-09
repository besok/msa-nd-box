package ie.home.msa.sandbox.discovery.client;

import ie.home.msa.messages.GetServiceMessage;
import ie.home.msa.messages.LogServiceMessage;
import lombok.extern.slf4j.Slf4j;
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

@Service
@Slf4j
public class LogAggregator {
    private final Path logDir;

    private final DiscoveryClient discoveryClient;
    private ExecutorService executor;

    public LogAggregator(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
        logDir = Paths.get(new ClassPathResource("logs").getPath());
        executor = Executors.newSingleThreadExecutor();
    }


    @PostConstruct
    public void scheduledThread() {
        this.executor.submit(() -> {
            while (true) {
                try {
                    Thread.sleep(5000);
                    List<String> logs = lookAt();
                    if (!logs.isEmpty()) {
                        LogServiceMessage message = makeMessage(logs);
                        GetServiceMessage logger = discoveryClient.getAddress("logs-aggregator-service");
                        String address = logger.getBody().getAddress();
                        sendLogs(message, address);
                    }
                } catch (Exception e) {
                    log.error(" scheduler log error ", e);
                }
            }
        });
    }

    private void sendLogs(LogServiceMessage message, String address) {
        ResponseEntity<Void> resp =
                discoveryClient.getRestTemplate().postForEntity("http://" + address + "/logs", message, Void.class);
        if (resp.getStatusCode().isError()) {
            log.error(" can't send logs to the slog service");
        } else {
            log.info(" send logs to the log service");
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
        List<Path> files = Files.list(logDir).filter(this::isNotCurrentLogFile).collect(Collectors.toList());
        ArrayList<String> logs = new ArrayList<>();
        for (Path file : files) {
            log.info("file {} will be removed ",file.getFileName().toString());
            logs.addAll(Files.readAllLines(file));
            Files.deleteIfExists(file);
        }
        return logs;
    }

    private boolean isNotCurrentLogFile(Path p) {
        return !p.getFileName().toString().equals("service.log");
    }


}
