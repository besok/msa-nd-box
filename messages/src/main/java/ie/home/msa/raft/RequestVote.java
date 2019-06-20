package ie.home.msa.raft;

import java.io.Serializable;

public class RequestVote implements Serializable {
    private int candidateId;
    private int term;
    private int lastLogIdx;
    private int lastLogTerm;

    public int getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public int getLastLogIdx() {
        return lastLogIdx;
    }

    public void setLastLogIdx(int lastLogIdx) {
        this.lastLogIdx = lastLogIdx;
    }

    public int getLastLogTerm() {
        return lastLogTerm;
    }

    public void setLastLogTerm(int lastLogTerm) {
        this.lastLogTerm = lastLogTerm;
    }

    @Override
    public String toString() {
        return "RequestVote{" +
                "candidateId=" + candidateId +
                ", term=" + term +
                ", lastLogIdx=" + lastLogIdx +
                ", lastLogTerm=" + lastLogTerm +
                '}';
    }
}
