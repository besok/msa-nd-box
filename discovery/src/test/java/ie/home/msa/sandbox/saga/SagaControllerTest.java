package ie.home.msa.sandbox.saga;

import ie.home.msa.saga.Chapter;
import ie.home.msa.saga.Saga;
import ie.home.msa.saga.Status;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.*;

public class SagaControllerTest {

    @Test
    public void tempTest() {
        RestTemplate restTemplate = new RestTemplate();
        Saga saga = new Saga();
        saga.addChapter(new Chapter("create","creator-service",Status.READY,"test-data"));
        saga.addChapter(new Chapter("validate","validator-service",Status.READY,"test-data"));
        saga.addChapter(new Chapter("log","logger-service",Status.READY,"test-data"));

        restTemplate.postForEntity("http://localhost:62417/saga/event",saga,Boolean.class);
    }
}