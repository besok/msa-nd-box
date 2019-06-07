package ie.home.msa.messages;

import ie.home.msa.zab.*;

import java.util.List;

public class ZRecoveryMessageBuilder {
    public static ZRecoveryMessage createMessage(String serviceName,
                                                     String serviceAddress,
                                                     RecoveryStatus status,
                                                     Zid zid,
                                                     List<WriteMessage> messages){
        ZRecoveryMessage m = new ZRecoveryMessage();
        m.setStatus(status);
        m.setVersion(0);
        m.setService(Service.of(serviceName,serviceAddress));
        RecoveryMessage rm = new RecoveryMessage();
        rm.setZid(zid);
        rm.setMessageList(messages);
        m.setBody(rm);
        return m;
    }
}
