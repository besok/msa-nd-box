package ie.home.msa.sandbox.discovery.server;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class AbstractSingleFileStorageTest {

    private SimpleFileStorage simpleFileStorage;

    @Before
    public void setUp() throws Exception {
        simpleFileStorage = new SimpleFileStorage();
        simpleFileStorage.init();
    }

    @After
    public void tearDown() throws Exception {
        simpleFileStorage.clean();
    }

    @Test
    public void put() throws IOException {
        String test = "test";
        String val = "val";
        simpleFileStorage.put(test, val);
        String res = simpleFileStorage.get(test);
        Assert.assertEquals(res,val);
        List<String> resList = Files.readAllLines(Paths.get(new ClassPathResource("test")
                .getPath()));
        String serv = resList.get(0);
        Assert.assertEquals(serv,test);

    }

    @Test
    public void remove() throws IOException {
        String[] vals = {"1","2","3"};
        for (String val : vals) {
            simpleFileStorage.put(val,val);
        }
        simpleFileStorage.remove("1");
        Set<String> servSet = simpleFileStorage.getServices().keySet();
        HashSet<String> servSetFile = new HashSet<>(Files.readAllLines(Paths.get(new ClassPathResource("test").getPath())));
        Assert.assertFalse(servSet.contains("1"));
        boolean res = servSet.removeAll(servSetFile);
        Assert.assertTrue(res);
        assertTrue(servSet.isEmpty());
    }

    @Test
    public void clean() {
        simpleFileStorage.clean();
    }

    public class SimpleFileStorage extends AbstractSingleFileStorage<String> {
        public SimpleFileStorage() {
            super("test");
        }

        @Override
        protected String toFile(String serv, String val) {
            return serv;
        }

        @Override
        protected Map<String, String> fromFile(List<String> records) {
            return records.stream().collect(Collectors.toMap(e -> e, e -> e,(a,b)->a+b));
        }
    }
}