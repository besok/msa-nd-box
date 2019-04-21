package ie.home.sandbox.crdt;

import ie.home.msa.crdt.LWWRegister;
import ie.home.msa.crdt.LWWRegisterImpl;
import ie.home.msa.sandbox.discovery.client.DiscoveryClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class LWWRegisterService {
    private LWWRegister register;
    private DiscoveryClient discoveryClient;


    public LWWRegisterService(DiscoveryClient discoveryClient) {
        this.register = new LWWRegisterImpl();
        this.discoveryClient=discoveryClient;
    }

    public void assign(String value){
        LWWRegister.Checker assign = register.assign(value);
        String[] nodes = discoveryClient.getNodes();
        RestTemplate restTemplate = discoveryClient.getRestTemplate();
        for (String node : nodes) {
            String url = "http://"+node +"/crdt/register/lww";
            ResponseEntity<Void> resp = restTemplate.postForEntity(url, assign, Void.class);
            log.info("merge result: {}, url: {}, state: {}",resp.getStatusCode(),url,assign);
        }

    }
    public String value(){
        return register.value();
    }
    public void merge(LWWRegister.Checker checker){
        log.info(" state before merge: {}, checker: {}", register, checker);
        register.merge(checker);
        log.info(" state after merge: {}", register);
    }
}
