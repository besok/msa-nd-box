package ie.home.msa.lab;

import org.springframework.stereotype.Service;

@Service
public class TestBean {

    @CircuitBreaker(1)
    public String slowMethod(){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "hello! i am slow method";
    }

    public String fastMethod(){
        return "hello! i am a fast method";
    }

}
