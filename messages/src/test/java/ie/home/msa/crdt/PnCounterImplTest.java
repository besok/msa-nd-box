package ie.home.msa.crdt;

import org.junit.Assert;
import org.junit.Test;

import static ie.home.msa.crdt.PnCounter.*;

public class PnCounterImplTest {
    @Test
    public void name() {

        long[] incArrInit = {0,6,1,0,0};
        long[] incArrLeft = {0,0,0,0,1};

        PnCounterImpl pnCounterBase = new PnCounterImpl(5, 1);
        pnCounterBase.generate(Op.INCREMENT);
        pnCounterBase.generate(Op.INCREMENT);
        pnCounterBase.generate(Op.INCREMENT);
        pnCounterBase.merge(incArrInit,incArrLeft);
        long value = pnCounterBase.value();
        Assert.assertEquals(value,6);

        pnCounterBase.update((incArr, decArr) -> {
            for (int i = 0; i < 5; i++) {
                incArr[i]=0;
                decArr[i]=0;
            }
            incArr[0]=1;
        });

        Assert.assertEquals(pnCounterBase.value(),1);
    }


}