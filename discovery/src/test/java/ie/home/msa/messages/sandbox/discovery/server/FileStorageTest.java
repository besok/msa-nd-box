package ie.home.msa.messages.sandbox.discovery.server;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FileStorageTest {
    FileStorage fileStorage;

    @Before
    public void setUp() throws Exception {
        fileStorage = new FileStorage();
        fileStorage.fileInit();
    }

    @Test
    public void putTest() {
        fileStorage.put("service2",":8080");
        String serv = fileStorage.get("service1");
        String serv1 = fileStorage.get("service2");
        Assert.assertEquals(serv,":8080");
        Assert.assertEquals(serv1,":8080");
    }

}