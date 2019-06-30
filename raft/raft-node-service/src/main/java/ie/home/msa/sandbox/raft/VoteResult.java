package ie.home.msa.sandbox.raft;

public class VoteResult {
    private int term;
    private int mIdx;
    private boolean vote;

    public int getmIdx() {
        return mIdx;
    }

    public void setmIdx(int mIdx) {
        this.mIdx = mIdx;
    }

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

    public VoteResult(int term, int mIdx, boolean vote) {
        this.term = term;
        this.mIdx = mIdx;
        this.vote = vote;
    }

    public VoteResult() {
    }

    @Override
    public String toString() {
        return "{" +
                "term=" + term +
                ", vote=" + vote +
                '}';
    }
}
