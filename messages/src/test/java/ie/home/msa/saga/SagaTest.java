package ie.home.msa.saga;

import org.junit.Assert;
import org.junit.Test;

import static ie.home.msa.saga.Status.*;
import static org.junit.Assert.*;

public class SagaTest {

    @Test
    public void test() {
        Saga saga = new Saga();
        saga.addChapter(new Chapter("t1","s1", READY,""));
        saga.addChapter(new Chapter("t2","s2", READY,""));
        saga.addChapter(new Chapter("t3","s3", READY,""));
        saga.addChapter(new Chapter("t4","s4", READY,""));
        Assert.assertEquals(saga.size(),4);
    }
}