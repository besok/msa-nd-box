package ie.home.msa.messages.sandbox.discovery.server;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.Repeat;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FileStorageTest {
    FileStorage fileStorage;

    @Before
    public void setUp() throws Exception {
        fileStorage = new FileStorage();
        fileStorage.fileInit();
        fileStorage.put("service", "data");
    }


    @Test
    public void getTest() {
        String service = fileStorage.get("service");
        Assert.assertEquals("data", service);
    }

    @Test
    public void putTest() {
        Boolean res = fileStorage.put("service", "data");
        Assert.assertEquals(true, res);
    }

    @Test
    public void putConcurrentTest() throws InterruptedException, ExecutionException, IOException {
        List<Callable<Boolean>> runnableList = IntStream.range(0, 1000).mapToObj(this::runnable)
                .collect(Collectors.toList());

        ExecutorService executorService = Executors.newFixedThreadPool(20);
        List<Future<Boolean>> futures = executorService.invokeAll(runnableList);

        for (Future<Boolean> future : futures) {
            Boolean bool = future.get();
            Assert.assertTrue(bool);
        }
        List<String> service = Files.readAllLines(fileStorage.store().resolve("service"));
        String addr = fileStorage.get("service");
        Assert.assertEquals(addr,"data999");
        Assert.assertEquals(service.get(0),"data999");

    }

    private Callable<Boolean> runnable(int i) {
        return () -> fileStorage.put("service", "data" + i);
    }

}