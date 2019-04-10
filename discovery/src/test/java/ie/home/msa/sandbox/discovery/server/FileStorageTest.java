package ie.home.msa.sandbox.discovery.server;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FileStorageTest {
    ServiceRegistryFolderStorage fileStorage;

    @Before
    public void setUp() throws Exception {
        fileStorage = new ServiceRegistryFolderStorage();
        fileStorage.init();
        fileStorage.put("service", "data");
    }

    @After
    public void tearDown() throws Exception {
        fileStorage.clean();
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
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        List<Callable<Boolean>> runnableList = IntStream.range(0, 100).mapToObj(this::putCallable)
                .collect(Collectors.toList());
        int i = 0;
        while (i < 10) {

            List<Future<Boolean>> futures = executorService.invokeAll(runnableList);

            for (Future<Boolean> future : futures) {
                Boolean bool = future.get();
                Assert.assertTrue(bool);
            }
            String fromFile = Files.readAllLines(fileStorage.store().resolve("service")).get(0);
            String fromMemory = fileStorage.get("service");
            Assert.assertEquals(fromFile, fromMemory);
            i++;
        }

    }
    @Test
    public void getConcurrentTest() throws InterruptedException, ExecutionException, IOException {
        fileStorage.clean();
        fileStorage.put("service","data");
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        List<Callable<String>> runnableList = IntStream.range(0, 1000).mapToObj(this::getCallable)
                .collect(Collectors.toList());
        int i = 0;
        while (i < 10) {
            List<Future<String>> futures = executorService.invokeAll(runnableList);
            for (Future<String> future : futures) {
                future.get();
            }
            String fromFile = Files.readAllLines(fileStorage.store().resolve("service")).get(0);
            String fromMemory = fileStorage.get("service");
            Assert.assertEquals(fromFile, fromMemory);
            i++;
        }
    }


    private Callable<Boolean> putCallable(int i) {
        return () -> fileStorage.put("service", "data" + i);
    }
    private Callable<String> getCallable(int i) {
        return () -> fileStorage.get("service");
    }

}