package ie.home.msa.zab;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class ZidTest {

    @Test
    public void compareTo() {
        Zid zidLeft = new Zid(1,1);
        Zid zidRight = new Zid(1,1);
        Assert.assertEquals(0,zidLeft.compareTo(zidRight));
        Assert.assertEquals(0,zidRight.compareTo(zidLeft));

        zidLeft.incEpoch();
        Assert.assertEquals(1,zidLeft.compareTo(zidRight));
        Assert.assertEquals(-1,zidRight.compareTo(zidLeft));

        zidRight.incCounter();
        zidRight.incCounter();

        Assert.assertEquals(1,zidLeft.compareTo(zidRight));
        Assert.assertEquals(-1,zidRight.compareTo(zidLeft));

        zidRight.incEpoch();

        Assert.assertEquals(2,zidRight.compareTo(zidLeft));

        zidLeft.incCounter();
        zidLeft.incCounter();

        Assert.assertEquals(0,zidRight.compareTo(zidLeft));

    }
}