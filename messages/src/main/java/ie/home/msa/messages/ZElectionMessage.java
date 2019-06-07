package ie.home.msa.messages;

import ie.home.msa.zab.ZNodeState;
import ie.home.msa.zab.ZNotification;

public class ZElectionMessage extends Message<ZNodeState, ZNotification> {
    @Override
    public String toString() {

        String address = getService().getAddress();
        String status = getStatus().toString();
        String notification = getBody().toString();

        return String.join("",
                "{", notification, ",", status, ",", address, "}"
        );
    }
}
