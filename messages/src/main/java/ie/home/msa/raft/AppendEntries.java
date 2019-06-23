package ie.home.msa.raft;

import java.io.Serializable;
import java.util.Arrays;

public class AppendEntries implements Serializable {
    private int term;
    private int leaderId;
    private int pevLogIdx;
    private int prevLogTerm;

    private Entry[] entries;
    private int commitIdx;

    public AppendEntries() {
    }

    public AppendEntries(int term, int leaderId, int pevLogIdx, int prevLogTerm, Entry[] entries, int commitIdx) {
        this.term = term;
        this.leaderId = leaderId;
        this.pevLogIdx = pevLogIdx;
        this.prevLogTerm = prevLogTerm;
        this.entries = entries;
        this.commitIdx = commitIdx;
    }

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

    public int getPevLogIdx() {
        return pevLogIdx;
    }

    public void setPevLogIdx(int pevLogIdx) {
        this.pevLogIdx = pevLogIdx;
    }

    public int getPrevLogTerm() {
        return prevLogTerm;
    }

    public void setPrevLogTerm(int prevLogTerm) {
        this.prevLogTerm = prevLogTerm;
    }

    public Entry[] getEntries() {
        return entries;
    }

    public void setEntries(Entry[] entries) {
        this.entries = entries;
    }

    public int getCommitIdx() {
        return commitIdx;
    }

    public void setCommitIdx(int commitIdx) {
        this.commitIdx = commitIdx;
    }

    @Override
    public String toString() {
        return "AppendEntries{" +
                "term=" + term +
                ", leaderId=" + leaderId +
                ", pevLogIdx=" + pevLogIdx +
                ", prevLogTerm=" + prevLogTerm +
                ", entries=" + (entries != null ? Arrays.toString(entries) : "null") +
                ", commitIdx=" + commitIdx +
                '}';
    }
}
