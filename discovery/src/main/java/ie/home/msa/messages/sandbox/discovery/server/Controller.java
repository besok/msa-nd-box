package ie.home.msa.messages.sandbox.discovery.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class Controller {

    private final FileStorage storage;

    @Autowired
    public Controller(FileStorage storage) {
        this.storage = storage;
    }

    @RequestMapping(path = "/services/{service}",method = RequestMethod.GET)
    public String getAddress(@PathVariable String service){
        return storage.get(service);
    }

    @RequestMapping(path = "/services",method = RequestMethod.POST)
    public boolean put(@RequestBody String service){
        String[] pair = service.split("=");
        return storage.put(pair[0], pair[1]);
    }



}
