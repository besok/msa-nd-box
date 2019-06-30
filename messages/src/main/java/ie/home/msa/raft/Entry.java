package ie.home.msa.raft;

public class Entry {
    private int term;
    private int idx;
    private int command;

    public Entry() {
    }

    public Entry(int term, int idx, int command) {
        this.term = term;
        this.idx = idx;
        this.command = command;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    @Override
    public String toString() {
        return "{" +
                "term=" + term +
                ", idx=" + idx +
                ", command=" + command +
                '}';
    }
}
