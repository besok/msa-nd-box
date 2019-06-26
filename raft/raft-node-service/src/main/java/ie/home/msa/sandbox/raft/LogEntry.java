package ie.home.msa.sandbox.raft;

public class LogEntry {
    private int term;
    private int idx;
    private int command;

    public LogEntry() {
    }

    public LogEntry(int term, int idx) {
        this.term = term;
        this.idx = idx;
    }

    public LogEntry(int term, int idx, int command) {
        this.term = term;
        this.idx = idx;
        this.command = command;
    }

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
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
}
