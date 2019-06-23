package ie.home.msa.messages;

import ie.home.msa.raft.RequestVote;

import static ie.home.msa.messages.Empty.EMPTY;

public class RaftRequestVoteMessageBuilder {
    public static RaftRequestVoteMessage build(String address,int id,int term,int lastLogIdx,int lastLogTerm){
        RaftRequestVoteMessage m = new RaftRequestVoteMessage();
        m.setStatus(EMPTY);
        m.setVersion(1);
        m.setService(Service.of("",address));
        RequestVote body = new RequestVote();
        body.setCandidateId(id);
        body.setTerm(term);
        body.setLastLogIdx(lastLogIdx);
        body.setLastLogTerm(lastLogTerm);
        m.setBody(body);
        return m;
    }
}
