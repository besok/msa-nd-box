package ie.home.msa.lab.batch;

import ie.home.msa.messages.FileCountTask;
import ie.home.msa.messages.FileCountTaskMessage;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.*;

public class WorkerControllerTest {

    @Test
    public void completedTask() {
        RestTemplate restTemplate = new RestTemplate();
        FileCountTaskMessage taskMessage = new FileCountTaskMessage();
        FileCountTask task = new FileCountTask("C:\\book");
        taskMessage.setBody(task);

        ResponseEntity<Long> resp = restTemplate.postForEntity("http://10.0.75.1:9999/task", taskMessage, Long.class);
        System.out.println(resp.getBody());
    }
}