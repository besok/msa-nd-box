package ie.home.msa.sandbox.greeting;

import ie.home.msa.sandbox.discovery.client.CircuitBreaker;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class SlowController {
    private AtomicInteger i = new AtomicInteger(1);


    @CircuitBreaker(1)
    @RequestMapping(path = "test",method = RequestMethod.GET)
    public int count(){
        if(i.get() > 10){
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return i.incrementAndGet();
    }
}
