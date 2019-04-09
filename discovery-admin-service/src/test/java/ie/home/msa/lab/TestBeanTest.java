package ie.home.msa.lab;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestBeanTest {
    @Autowired
    private TestBean testBean;

    @Test
    public void test() {
        System.out.println(testBean.slowMethod());
        System.out.println(testBean.fastMethod());
        System.out.println(CircuitBreakerMethodStore.checkTroubles());
    }
}