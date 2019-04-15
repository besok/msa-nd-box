package ie.home.msa.sandbox.discovery.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LoadBalancerFileStorage extends AbstractFileStorage<LoadBalancerData> {

    public LoadBalancerFileStorage(StorageListenerHandler handler) {
        super(StorageListenerHandler.FileStorageType.LOAD_BALANCER.getName(), handler);
    }

    @Override
    protected List<LoadBalancerData> fromFile(List<String> params) {
        return params.stream().map(LoadBalancerData::fromString).collect(Collectors.toList());
    }

    @Override
    protected List<String> toFile(List<LoadBalancerData> params) {
        return params.stream().map(LoadBalancerData::toString).collect(Collectors.toList());
    }

    @Override
    protected boolean equal(LoadBalancerData left, LoadBalancerData right) {
        return left.getAddress().equals(right.getAddress());
    }
}
