package ie.home.msa.sandbox.discovery.server;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Component
public class RetryFileStorage extends AbstractSingleFileStorage<String> {
    public RetryFileStorage() {
        super("retry-list");
    }

    @Override
    protected String toFile(String serv, String val) {
        return serv+":"+val;
    }

    @Override
    protected Map<String, String> fromFile(List<String> records) {
        return records.stream()
                .map(e -> e.split(":"))
                .collect(Collectors.toMap(p -> p[0], p -> p[1]));
    }
}
