package ie.home.msa.messages.sandbox.discovery.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Service
public class DiscoveryClient implements ApplicationListener<WebServerInitializedEvent> {

    @Value("${spring.application.name}")
    private String serviceName;
    @Value("${service-discovery.admin.address}")
    private String adminAddress;

    private RestTemplate restTemplate;


    @Override
    public void onApplicationEvent(WebServerInitializedEvent webServerInitializedEvent) {
        try {
            String url = adminAddress + "/services";
            int port = webServerInitializedEvent.getWebServer().getPort();
            String address = InetAddress.getLocalHost().getHostAddress() + ":" + port;

            restTemplate = new RestTemplate();
            String s = serviceName + "=" + address;

            ResponseEntity<Boolean> exchange = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(s), Boolean.class);
            Boolean res = exchange.getBody();
            System.out.println("registration : " + res);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
