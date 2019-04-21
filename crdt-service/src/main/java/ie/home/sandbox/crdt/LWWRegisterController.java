package ie.home.sandbox.crdt;

import ie.home.msa.crdt.LWWRegister;
import ie.home.msa.sandbox.discovery.client.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

@RestController
public class LWWRegisterController {
    private LWWRegisterService service;

    public LWWRegisterController(DiscoveryClient discoveryClient) {
        service = new LWWRegisterService(discoveryClient);
    }

    @RequestMapping(path = "/crdt/register/lww/value", method = RequestMethod.GET)
    public String value(){
        return service.value();
    }
    @RequestMapping(path = "/crdt/register/lww/assign/{value}", method = RequestMethod.GET)
    public void assign(@PathVariable String value){
        service.assign(value);
    }

    @RequestMapping(path = "/crdt/register/lww", method = RequestMethod.POST)
    public void merge(@RequestBody LWWRegister.Checker checker) {
        service.merge(checker);
    }
}
