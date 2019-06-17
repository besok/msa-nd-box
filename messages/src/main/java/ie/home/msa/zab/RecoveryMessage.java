package ie.home.msa.zab;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class RecoveryMessage implements Serializable {
    private Zid zid;
    private List<WriteMessage> messageList;

    public Zid getZid() {
        return zid;
    }

    public void setZid(Zid zid) {
        this.zid = zid;
    }

    public List<WriteMessage> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<WriteMessage> messageList) {
        this.messageList = messageList;
    }

    public RecoveryMessage() {
    }

    @Override
    public String toString() {
        return "RecoveryMessage{" +
                "zid=" + zid +
                ", messageList=" + Arrays.toString(messageList.toArray()) +
                '}';
    }
}
