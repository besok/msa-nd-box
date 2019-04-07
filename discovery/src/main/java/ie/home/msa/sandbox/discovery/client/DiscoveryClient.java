package ie.home.msa.sandbox.discovery.client;

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

    private RestTemplate restTemplate;


    private String URL;

    @Override
    public void onApplicationEvent(WebServerInitializedEvent webServerInitializedEvent) {
        try {
            restTemplate = new RestTemplate();
            URL = adminAddress + "/services";
            int port = webServerInitializedEvent.getWebServer().getPort();
            String address = InetAddress.getLocalHost().getHostAddress() + ":" + port;

            String service = serviceName + "=" + address;

            ResponseEntity<Boolean> exchange = restTemplate.exchange(URL, HttpMethod.POST,
                    new HttpEntity<>(service), Boolean.class);
            if(exchange.getStatusCodeValue() != 200){
                throw new DiscoveryClientException();
            }
            log.info("registration for {} in service discovery admin is {}",serviceName,exchange.getBody());

        } catch (UnknownHostException e) {
            throw new DiscoveryClientException(e);
        }
    }

    public String getAddress(String service){
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(URL + "/" + service, String.class);
        if(responseEntity.getStatusCodeValue() != 200){
            throw new DiscoveryClientException();
        }
        return responseEntity.getBody();
    }
}
