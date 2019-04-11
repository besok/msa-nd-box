package ie.home.msa.sandbox.discovery.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CircuitBreakerFileStorage extends AbstractSingleFileStorage<Boolean> {

    public CircuitBreakerFileStorage() {
        super("circuit-list");
    }

    public void turnOff(String service) {
        if (getServices().containsKey(service)) {
            getServices().put(service, true);
            log.info(" service {} is turn off", service);
        }
    }
    public void turnOn(String service) {
        if (getServices().containsKey(service)) {
            getServices().put(service, false);
            log.info(" service {} is turn on", service);
        }
    }

    @Override
    protected String toFile(String serv, Boolean val) {
        return serv;
    }

    @Override
    protected Map<String, Boolean> fromFile(List<String> records) {
        return records.stream().collect(Collectors.toMap(s->s,s->false));
    }

}
