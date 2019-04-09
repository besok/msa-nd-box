package ie.home.msa.sandbox.discovery.client;

import ie.home.msa.messages.MessageBuilder;
import ie.home.msa.messages.ServiceRegisterMessage;
import lombok.extern.slf4j.Slf4j;
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

    @Value("${spring.application.name}")
    private String serviceName;
    @Value("${service-discovery.admin.address:http://localhost:9000}")
    private String adminAddress;

    @Value("${circuit-breaker:false}")
    private String circuitBreaker;

    private RestTemplate restTemplate;


    private String URL;

    @Override
    public void onApplicationEvent(WebServerInitializedEvent webServerInitializedEvent) {
        try {
            restTemplate = new RestTemplate();
            URL = adminAddress + "/services";
            int port = webServerInitializedEvent.getWebServer().getPort();
            String address = InetAddress.getLocalHost().getHostAddress() + ":" + port;

            ServiceRegisterMessage message = MessageBuilder.registerMessage(serviceName, address,
                    " service");

            int cb = Boolean.valueOf(this.circuitBreaker) ? 1 : 0;
            message.getBody().putProp("circuit-breaker", cb);
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
