package ie.home.msa.sandbox.greeting;

import ie.home.msa.sandbox.discovery.client.DestroyOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SimpleDestroyOperation implements DestroyOperation {

    @Override
    public Boolean operate() {
        log.info(" destroy ... ");
        return true;
    }
}
