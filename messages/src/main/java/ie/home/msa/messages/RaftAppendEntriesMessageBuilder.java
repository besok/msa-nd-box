package ie.home.msa.messages;

import ie.home.msa.raft.AppendEntries;
import ie.home.msa.raft.Entry;

import static ie.home.msa.messages.Empty.EMPTY;

public class RaftAppendEntriesMessageBuilder {
    public static RaftAppendEntriesMessage build(String address,
                                                 int term,
                                                 int lId,
                                                 int prevLogIdx,
                                                 int prevLogTerm,
                                                 int cIdx,
                                                 Entry[] entries){
        RaftAppendEntriesMessage m = new RaftAppendEntriesMessage();
        m.setStatus(EMPTY);
        m.setVersion(1);
        m.setService(Service.of("",address));
        AppendEntries b = new AppendEntries();

        b.setTerm(term);
        b.setLeaderId(lId);
        b.setPevLogIdx(prevLogIdx);
        b.setPrevLogTerm(prevLogTerm);
        b.setCommitIdx(cIdx);
        b.setEntries(entries);


        m.setBody(b);
        return m;
    }
}
