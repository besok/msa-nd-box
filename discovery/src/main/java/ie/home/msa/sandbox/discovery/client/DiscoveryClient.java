package ie.home.msa.sandbox.discovery.client;

import ie.home.msa.messages.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.stream.Stream;

@Slf4j
@Component
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

    private String address;
    private RestTemplate restTemplate;
    private int port;

    public String getServiceName() {
        return serviceName;
    }

    @Autowired
    public DiscoveryClient(HealthAggregator aggregator) {
        this.aggregator = aggregator;
    }

    @Override
    public void onApplicationEvent(WebServerInitializedEvent webServerInitializedEvent) {
        this.port = webServerInitializedEvent.getWebServer().getPort();
        registration();
    }

    public int findIdx(String[] nodes) {
        for (int i = 0; i < nodes.length; i++) {
            String node = nodes[i];
            if (node.equals(address)) {
                return i;
            }
        }
        return -1;
    }

    public String[] getNodes() {
        try {
            String url = adminAddress + "/services/all/" + serviceName;
            ResponseEntity<GetAllNodesServiceMessage> resp = restTemplate.getForEntity(url, GetAllNodesServiceMessage.class);
            return Stream.of(resp.getBody().getBody())
                    .map(Service::getAddress)
                    .toArray(String[]::new);
        } catch (Exception ex) {
            log.error("trying to get node list for service {}", serviceName, ex);
        }
        return new String[0];
    }

    public void registration() {
        try {
            restTemplate = new RestTemplate();
            String URL = adminAddress + "/services";
            address = InetAddress.getLocalHost().getHostAddress() + ":" + port;

            aggregator.setServiceAndAddress(serviceName, address);
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

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public GetServiceMessage getAddress(String service) {
        String url = adminAddress + "services/" + service;
        ResponseEntity<GetServiceMessage> entity = restTemplate.getForEntity(url, GetServiceMessage.class);
        if(entity.getStatusCode().is2xxSuccessful()){
            return entity.getBody();
        }
        throw new DiscoveryClientException();
    }
}
