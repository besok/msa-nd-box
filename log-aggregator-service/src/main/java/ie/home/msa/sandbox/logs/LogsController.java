package ie.home.msa.sandbox.logs;

import ie.home.msa.messages.LogServiceMessage;
import ie.home.msa.messages.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class LogsController {

    private final LogStore logStore;

    public LogsController(LogStore logStore) {
        this.logStore = logStore;
    }

    @RequestMapping(path = "/logs", method = RequestMethod.POST)
    public void register(@RequestBody LogServiceMessage message) {
        log.info(" get new log message {}",message);
        List<String> logList = message.getBody().getLogList();
        Service service = message.getService();
        String serviceName = service.getName();
        String address = service.getAddress();

        logStore.setLogs(serviceName,address,logList);
    }
    @RequestMapping(path = "/{service}/{address}/last", method = RequestMethod.GET)
    public List<String> getLastLogs(@PathVariable String service,@PathVariable String address) {
        return logStore.getLastByAddress(service,address);
    }
    @RequestMapping(path = "/{service}/{address}/all", method = RequestMethod.GET)
    public List<String> getAllLogs(@PathVariable String service,@PathVariable String address) {
        return logStore.getAllByAddress(service,address);
    }
}
