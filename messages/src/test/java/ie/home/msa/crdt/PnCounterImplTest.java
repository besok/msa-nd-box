package ie.home.msa.crdt;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

import static ie.home.msa.crdt.PnCounter.*;

public class PnCounterImplTest {
    @Test
    public void commonTest() {

        long[] incArrInit = {0,6,1,0,0};
        long[] incArrLeft = {0,0,0,0,1};

        PnCounterImpl pnCounterBase = new PnCounterImpl(5, 1);
        pnCounterBase.generate(Op.INCREMENT);
        pnCounterBase.generate(Op.INCREMENT);
        pnCounterBase.generate(Op.INCREMENT);
        pnCounterBase.merge(new State(incArrInit,incArrLeft));
        long value = pnCounterBase.value();
        Assert.assertEquals(value,6);

        pnCounterBase.update(new Effector(Op.INCREMENT,0));

        Assert.assertEquals(pnCounterBase.value(),7);
    }

}