package ie.home.msa.sandbox.discovery.server;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class CircuitBreakerFileStorageTest {

    private CircuitBreakerFileStorage fileStorage;

    @Before
    public void init() throws IOException {
        fileStorage = new CircuitBreakerFileStorage(new StorageListenerHandler(new ArrayList<>()));
        fileStorage.init();
    }

    @After
    public void clean() throws IOException {
        fileStorage.clean();
    }

    @Test
    public void turnOffTest() {
        String testKey = "testKey";
        String testVal = "testVal";
        fileStorage.turnOff(testKey, testVal);
        fileStorage.turnOff(testKey, testVal);
        fileStorage.turnOff(testKey, testVal);

        CircuitBreakerData cbData = fileStorage.get(testKey).get(0);
        Assert.assertEquals(cbData.getAddress(), testVal);
        Assert.assertEquals(cbData.getVersion(), 3);
    }
}