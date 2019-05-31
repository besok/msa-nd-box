package ie.home.msa.messages;

import ie.home.msa.zab.*;

public class ZWriteMessageBuilder {
    public static ZWriteMessage createMessage(String serviceAddress,WriteStatus status,Zid zid,Object object){
        ZWriteMessage m = new ZWriteMessage();
        m.setStatus(status);
        m.setVersion(0);
        m.setService(Service.of("zan-node-service",serviceAddress));
        m.setBody(new WriteMessage(zid,object));
        return m;
    }
}
