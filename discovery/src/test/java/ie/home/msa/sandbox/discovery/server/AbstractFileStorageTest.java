package ie.home.msa.sandbox.discovery.server;

import org.junit.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class AbstractFileStorageTest {

    TestFileStorage fileStorage;

    @Before
    public void setUp() throws Exception {
        fileStorage = new TestFileStorage();
        fileStorage.init();
    }

    @After
    public void cleanAfterTest() throws Exception {
        fileStorage.clean();
    }

    @Test
    public void getTest() {
        String testKey = "testKey";
        String testValue = "testValue";
        fileStorage.put(testKey, testValue);
        List<String> vals = fileStorage.get(testKey);
        Assert.assertFalse(vals.isEmpty());
        Assert.assertEquals(vals.get(0), testValue);
    }

    @Test
    public void getListTest() {
        String testKey = "testKey";
        String testValue1 = "testValue1";
        String testValue2 = "testValue2";
        String testValue3 = "testValue3";
        fileStorage.put(testKey, testValue1);
        fileStorage.put(testKey, testValue2);
        fileStorage.put(testKey, testValue3);
        Object[] vals = fileStorage.get(testKey).toArray();
        Object[] testVal = Arrays.asList(testValue1, testValue2, testValue3).toArray();
        Assert.assertArrayEquals(vals, testVal);
    }

    @Test
    public void getRandTest() {
        String testKey = "testKey";
        String testValue1 = "testValue1";
        String testValue2 = "testValue2";
        String testValue3 = "testValue3";
        fileStorage.put(testKey, testValue1);
        fileStorage.put(testKey, testValue2);
        fileStorage.put(testKey, testValue3);
        List<String> vals = Arrays.asList(testValue1, testValue2, testValue3);
        for (int i = 0; i < 100; i++) {
            String rand = fileStorage.getRand(testKey);
            Assert.assertTrue(vals.contains(rand));
        }
    }

    @Test
    public void removeKeyTest() {
        String testKey = "testKey";
        String testValue1 = "testValue1";
        String testValue2 = "testValue2";
        String testValue3 = "testValue3";
        fileStorage.put(testKey, testValue1);
        fileStorage.put(testKey, testValue2);
        fileStorage.put(testKey, testValue3);
        boolean res = fileStorage.removeKey(testKey);
        Assert.assertTrue(res);
        Assert.assertFalse(fileStorage.getMemoryServices().containsKey(testKey));
        Assert.assertFalse(fileStorage.getStore().resolve(testKey).toFile().exists());
    }

    @Test
    public void putTest() throws IOException {
        String testKey = "testKey";
        String testValue1 = "testValue1";
        boolean res = fileStorage.put(testKey, testValue1);
        Assert.assertTrue(res);
        Assert.assertTrue(fileStorage.get(testKey).contains(testValue1));
        Path keyFile = fileStorage.getStore().resolve(testKey);
        Assert.assertTrue(Files.readAllLines(keyFile).contains(testValue1));
    }

    @Test
    public void putListTest() {
        String testKey = "testKey";
        String testValue1 = "testValue1";
        String testValue2 = "testValue2";
        String testValue3 = "testValue3";
        fileStorage.put(testKey, testValue1);
        fileStorage.put(testKey, testValue2);
        fileStorage.put(testKey, testValue3);
        List<String> vals = fileStorage.get(testKey);
        List<String> testVals = new ArrayList<>(Arrays.asList(testValue1, testValue2, testValue3));
        vals.removeAll(testVals);
        testVals.retainAll(vals);
        Assert.assertTrue(vals.isEmpty());
        Assert.assertTrue(testVals.isEmpty());
    }

    @Test
    public void putConcurrentTest() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        String testKey = "testKey";
        String testVal = "testVal_";
        List<Callable<Boolean>> callableList = IntStream.range(0, 100)
                .mapToObj(i -> callablePut(testKey, testVal + i))
                .collect(Collectors.toList());
        List<Future<Boolean>> futures = executor.invokeAll(callableList);
        for (Future<Boolean> future : futures) {
            Assert.assertTrue(future.get());
        }
        List<String> params = fileStorage.get(testKey);
        assertEquals(100, params.size());
        for (int i = 0; i < 100; i++) {
            assertTrue(params.contains(testVal + i));
        }
    }

    @Test
    public void removeValueListTest() throws IOException {
        String testKey = "testKey";
        String testValue1 = "testValue1";
        String testValue2 = "testValue2";
        String testValue3 = "testValue3";
        fileStorage.put(testKey, testValue1);
        fileStorage.put(testKey, testValue2);
        fileStorage.put(testKey, testValue3);

        List<String> vals = new ArrayList<>(fileStorage.get(testKey));

        for (String val : vals) {
            boolean res = fileStorage.removeVal(testKey, val);
            Assert.assertTrue(res);
            List<String> inVals = fileStorage.get(testKey);
            if (inVals != null) {
                Assert.assertFalse(inVals.contains(val));
                Path resolve = fileStorage.getStore().resolve(testKey);
                Assert.assertFalse(Files.readAllLines(resolve).contains(val));
            }
        }
    }

    @Test
    public void removeValueTest() throws IOException {
        String testKey = "testKey";
        String testValue1 = "testValue1";
        String testValue2 = "testValue2";
        String testValue3 = "testValue3";
        fileStorage.put(testKey, testValue1);
        fileStorage.put(testKey, testValue2);
        fileStorage.put(testKey, testValue3);


        boolean res = fileStorage.removeVal(testKey, testValue1);
        Assert.assertTrue(res);
        List<String> inVals = fileStorage.get(testKey);
        Assert.assertFalse(inVals.contains(testValue1));
        Path resolve = fileStorage.getStore().resolve(testKey);
        Assert.assertFalse(Files.readAllLines(resolve).contains(testValue1));
    }




    private Callable<Boolean> callablePut(String key, String val) {
        return () -> fileStorage.put(key, val);
    }

    public class TestFileStorage extends AbstractFileStorage<String> {
        TestFileStorage() {
            super("test",
                    new StorageListenerHandler(new ArrayList<>()));
        }

        @Override
        protected List<String> fromFile(List<String> params) {
            return params;
        }

        @Override
        protected List<String> toFile(List<String> params) {
            return params;
        }

        @Override
        protected boolean equal(String left, String right) {
            return left.equals(right);
        }
    }
}