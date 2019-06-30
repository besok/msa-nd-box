package ie.home.msa.sandbox.raft;

public class HeartBeatMessage {
    private int term;
    private int leaderId;

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public int getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(int leaderId) {
        this.leaderId = leaderId;
    }

    @Override
    public String toString() {
        return "HeartBeatMessage{" +
                "term=" + term +
                ", leaderId=" + leaderId +
                '}';
    }


}
