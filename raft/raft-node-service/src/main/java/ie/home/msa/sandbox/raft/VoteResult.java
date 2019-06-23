package ie.home.msa.sandbox.raft;

public class VoteResult {
    private int term;
    private boolean vote;

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public boolean isVote() {
        return vote;
    }

    public void setVote(boolean vote) {
        this.vote = vote;
    }

    public VoteResult(int term, boolean vote) {
        this.term = term;
        this.vote = vote;
    }

    public VoteResult() {
    }

    @Override
    public String toString() {
        return "VoteResult{" +
                "term=" + term +
                ", vote=" + vote +
                '}';
    }
}
