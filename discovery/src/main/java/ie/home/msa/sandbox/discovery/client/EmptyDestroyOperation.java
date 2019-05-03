package ie.home.msa.sandbox.discovery.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmptyDestroyOperation implements DestroyOperation {
    @Override
    public Boolean operate() {
        log.info(" destroy marker ");
        return true;
    }
}
