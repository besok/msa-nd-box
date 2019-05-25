package ie.home.msa.zab;

import java.io.Serializable;

public class ZNotification implements Serializable {
    private ZVote vote;
    private int round;
    private int id;

    public ZNotification() {
    }

    public ZNotification(ZVote vote, int round, int id) {
        this.vote = vote;
        this.round = round;
        this.id = id;
    }


    public void roundInc(){
        round++;
    }

    public ZVote getVote() {
        return vote;
    }

    public void setVote(ZVote vote) {
        this.vote = vote;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "{" +
                "vote=" + vote +
                ", round=" + round +
                ", id=" + id +
                '}';
    }
}
