package ie.home.msa.sandbox.raft;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RaftUtilsTest {

    @Test
    public void findMaxIdx() {
        Assert.assertEquals(Optional.of(5),RaftUtils.findMaxInQs(new int[]{6, 6, 5, 5, 4}));
        Assert.assertEquals(Optional.of(5),RaftUtils.findMaxInQs(new int[]{6, 6, 5, 5, 5}));
        Assert.assertEquals(Optional.of(7),RaftUtils.findMaxInQs(new int[]{6, 6, 7,7,7}));
        Assert.assertEquals(Optional.of(1),RaftUtils.findMaxInQs(new int[]{1,1,1,1,1}));
        Assert.assertEquals(Optional.of(1),RaftUtils.findMaxInQs(new int[]{1,1,1}));
        Assert.assertEquals(Optional.of(1),RaftUtils.findMaxInQs(new int[]{1,1,2}));
        Assert.assertEquals(Optional.of(2),RaftUtils.findMaxInQs(new int[]{1,2,2}));
        Assert.assertEquals(Optional.of(2),RaftUtils.findMaxInQs(new int[]{1,2,3}));
        Assert.assertEquals(Optional.of(1),RaftUtils.findMaxInQs(new int[]{1}));
        Assert.assertEquals(Optional.empty(),RaftUtils.findMaxInQs(new int[]{}));

    }


}