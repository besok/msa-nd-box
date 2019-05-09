package ie.home.msa.sandbox.logs;

import ie.home.msa.messages.LogServiceMessage;
import ie.home.msa.messages.Service;
import ie.home.msa.messages.ServiceRegisterMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class LogsController {
    @RequestMapping(path = "/logs", method = RequestMethod.POST)
    public void register(@RequestBody LogServiceMessage message) {
        List<String> logList = message.getBody().getLogList();
        Service service = message.getService();
        System.out.println(" service "+ service.getName() + " address "+ service.getAddress());
        logList.forEach(System.out::println);
    }
}
