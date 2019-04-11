package ie.home.msa.sandbox.discovery.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApplicationRestarter {
    @Autowired
    private ApplicationContext context;
    public void close() {
        SpringApplication.exit(context, () -> 1);
    }
}
