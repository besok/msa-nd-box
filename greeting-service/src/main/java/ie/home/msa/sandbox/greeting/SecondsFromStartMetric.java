package ie.home.msa.sandbox.greeting;

import ie.home.msa.messages.ServiceMetricsMessage;
import ie.home.msa.sandbox.discovery.client.Health;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static ie.home.msa.messages.ServiceMetricsMessage.*;
import static java.time.temporal.ChronoUnit.SECONDS;

@Component
public class SecondsFromStartMetric implements Health {
    private LocalDateTime ldtStart;

    @PostConstruct
    private void postInit() {
        this.ldtStart = LocalDateTime.now();
    }

    @Override
    public Metrics health() {
        return Metrics.single(
                "seconds-from-start",
                Math.toIntExact(SECONDS.between(ldtStart, LocalDateTime.now())));
    }
}
