package ie.home.msa.sandbox.discovery.server;

import ie.home.msa.messages.GetServiceMessage;
import ie.home.msa.messages.Message;
import ie.home.msa.messages.MessageBuilder;
import ie.home.msa.messages.ServiceStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.Objects.*;

@Service
@Slf4j
public class LoadBalanceResolver {
    private final Map<String, Integer> indexMap;
    private final LoadBalancerFileStorage storage;

    public LoadBalanceResolver(@Lazy LoadBalancerFileStorage storage) {
        this.storage = storage;
        this.indexMap = new HashMap<>();
    }

    public synchronized void addService(String service) {
        indexMap.putIfAbsent(service, 0);
        log.info("add service to index map");
    }

    public synchronized void removeService(String service) {
        indexMap.remove(service);
    }

    private synchronized void put(String service, Integer val) {
        indexMap.put(service, val);
    }

    public GetServiceMessage resolve(String service, List<String> address) {
        List<LoadBalancerData> loadBalancerData = storage.get(service);
        if (Objects.nonNull(loadBalancerData) && !loadBalancerData.isEmpty()) {
            String strategy = loadBalancerData.get(0).getStrategy();
            return switchUp(strategy).process(service, address);
        } else {
            return switchUp( "").process(service, address);
        }
    }


    private Processor switchUp( String strategy) {
        LoadBalanceStrategy str = LoadBalanceStrategy.from(strategy)
                .orElseThrow(RuntimeException::new);

        switch (str) {
            case ROBIN:
                return new RobinProcessor();
            case RANDOM:
            default:
                return new RandomProcessor();
        }

    }

    private interface Processor {
        GetServiceMessage process(String service, List<String> address);
    }

    private static class RandomProcessor implements Processor {
        private Random random;

        public RandomProcessor() {
            this.random = new Random();
        }

        @Override
        public GetServiceMessage process(String service, List<String> address) {
            if (address.isEmpty()) {
                return MessageBuilder.serviceMessage(service, "", ServiceStatus.FAILED);
            } else {
                int idx = random.nextInt(address.size());
                log.info("resolve strategy random for service {} with idx {}", service, idx);
                return MessageBuilder.serviceMessage(service, address.get(idx), ServiceStatus.READY);
            }
        }
    }

    private class RobinProcessor implements Processor {

        @Override
        public GetServiceMessage process(String service, List<String> address) {
            Integer idx = indexMap.get(service);
            int resIdx = idx < address.size() - 1 ? ++idx : 0;
            log.info("resolve strategy round-robin for service {} with idx {}", service, resIdx);
            put(service, resIdx);
            return MessageBuilder.serviceMessage(service, address.get(resIdx), ServiceStatus.READY);
        }
    }


}
