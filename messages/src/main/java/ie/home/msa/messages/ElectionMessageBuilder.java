package ie.home.msa.messages;

import ie.home.msa.zab.ZNodeState;
import ie.home.msa.zab.ZNotification;
import ie.home.msa.zab.ZVote;
import ie.home.msa.zab.Zid;

public class ElectionMessageBuilder {
    public static ElectionMessage createInitMessage(String serviceName, String serviceAddress, int id){
        ElectionMessage m = new ElectionMessage();
        m.setStatus(ZNodeState.ELECTION);
        m.setVersion(0);
        m.setService(Service.of(serviceName,serviceAddress));
        m.setBody(new ZNotification(new ZVote(id,new Zid(0,0)),0,id));
        return m;
    }
}
