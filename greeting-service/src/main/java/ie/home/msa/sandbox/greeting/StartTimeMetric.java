package ie.home.msa.sandbox.greeting;

import ie.home.msa.messages.ServiceMessage;
import ie.home.msa.sandbox.discovery.client.Health;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class StartTimeMetric implements Health {

    private LocalDateTime ldtStart;

    @Override
    public ServiceMessage.Metric health() {
        LocalDateTime ldt = LocalDateTime.now();
        long fromStart = ChronoUnit.SECONDS.between(ldtStart, ldt);
        return new ServiceMessage.Metric("sec-from-start",Math.toIntExact(fromStart));
    }


    @PostConstruct
    private void init(){
        ldtStart = LocalDateTime.now();
    }
}
