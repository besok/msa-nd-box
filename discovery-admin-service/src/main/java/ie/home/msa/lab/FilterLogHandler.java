package ie.home.msa.lab;

import ie.home.msa.messages.ServiceMessage;
import ie.home.msa.sandbox.discovery.server.Handler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FilterLogHandler implements Handler {
    @Override
    public void handle(String service, ServiceMessage.Metrics metrics) {
        metrics.getMetrics().stream()
                .filter(m -> m.getValue() > 20)
                .forEach(m -> printLog(service, m));
    }

    private void printLog(String service, ServiceMessage.Metric m) {
        log.error(" service {} has critical metric {} = {}",service,m.getName(),m.getValue());
    }
}
