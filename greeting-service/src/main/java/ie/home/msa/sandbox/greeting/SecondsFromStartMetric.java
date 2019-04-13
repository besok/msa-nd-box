package ie.home.msa.sandbox.greeting;

import ie.home.msa.sandbox.discovery.client.HMetrics;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.SECONDS;

@Component
public class SecondsFromStartMetric implements HMetrics {
    private LocalDateTime ldtStart;

    @PostConstruct
    private void postInit() {
        this.ldtStart = LocalDateTime.now();
    }

    @Override
    public ie.home.msa.messages.ServiceMetricsMessage.Metrics metric() {
        return ie.home.msa.messages.ServiceMetricsMessage.Metrics.single(
                "seconds-from-start",
                Math.toIntExact(SECONDS.between(ldtStart, LocalDateTime.now())));
    }
}
