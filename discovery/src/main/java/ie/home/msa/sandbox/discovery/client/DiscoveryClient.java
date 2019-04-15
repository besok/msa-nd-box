package ie.home.msa.sandbox.discovery.client;

import ie.home.msa.messages.MessageBuilder;
import ie.home.msa.messages.ServiceRegisterMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Service
@Slf4j
public class DiscoveryClient implements ApplicationListener<WebServerInitializedEvent> {

    private final HealthAggregator aggregator;
    @Value("${spring.application.name:default-service}")
    private String serviceName;
    @Value("${service-discovery.admin.address:http://localhost:9000}")
    private String adminAddress;

    @Value("${circuit-breaker:false}")
    private String circuitBreaker;

    @Value("${load-balance-strategy:#{null}}")
    private String loadBalance;

    private RestTemplate restTemplate;
    private int port;

    private String URL;

    @Autowired
    public DiscoveryClient(HealthAggregator aggregator) {
        this.aggregator = aggregator;
    }

    @Override
    public void onApplicationEvent(WebServerInitializedEvent webServerInitializedEvent) {
        this.port = webServerInitializedEvent.getWebServer().getPort();
        registration();
    }

    public void registration(){
        try {
            restTemplate = new RestTemplate();
            URL = adminAddress + "/services";
            String address = InetAddress.getLocalHost().getHostAddress() + ":" + port;

            aggregator.setServiceAndAddress(serviceName,address);
            ServiceRegisterMessage message = MessageBuilder.registerMessage(serviceName, address,
                    " service");

            message.getBody().putProperty("circuit-breaker", this.circuitBreaker);
            message.getBody().putProperty("load-balance-strategy", this.loadBalance);
            ResponseEntity<ServiceRegisterMessage> exchange = restTemplate.exchange(URL, HttpMethod.POST,
                    new HttpEntity<>(message), ServiceRegisterMessage.class);
            if (exchange.getStatusCode().isError()) {
                throw new DiscoveryClientException();
            }
            log.info("registration for {} in service discovery admin is {}",
                    serviceName, exchange.getBody().getStatus());

        } catch (UnknownHostException e) {
            throw new DiscoveryClientException(e);
        }
    }

}
