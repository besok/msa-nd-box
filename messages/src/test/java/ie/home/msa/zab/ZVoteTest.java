package ie.home.msa.zab;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class ZVoteTest {

    @Test
    public void compareTo() {
        ZVote voteLeft = new ZVote(1,new Zid(1,1));
        ZVote voteRight = new ZVote(1,new Zid(1,1));

        Assert.assertEquals(0,voteLeft.compareTo(voteRight));

        voteLeft.setId(2);
        Assert.assertEquals(1,voteLeft.compareTo(voteRight));

        voteLeft.setId(1);

        Assert.assertTrue(voteLeft.equals(voteRight));
        Assert.assertTrue(voteRight.equals(voteLeft));

    }
}