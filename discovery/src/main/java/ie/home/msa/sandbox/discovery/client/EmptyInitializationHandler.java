package ie.home.msa.sandbox.discovery.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmptyInitializationHandler implements InitializationHandler {
    @Override
    public boolean initialization() {
        log.info(" init marker ");
        return true;
    }
}
