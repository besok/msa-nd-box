package ie.home.msa.messages;

import ie.home.msa.raft.AppendEntries;
import ie.home.msa.raft.RequestVote;

public class RaftAppendEntriesMessage extends Message<Empty, AppendEntries> {
    @Override
    public String toString() {
        return super.toString();
    }
}
