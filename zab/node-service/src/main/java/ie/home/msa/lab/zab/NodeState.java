package ie.home.msa.lab.zab;

import ie.home.msa.messages.ZElectionMessage;
import ie.home.msa.zab.ZNodeState;
import ie.home.msa.zab.ZNotification;
import ie.home.msa.zab.ZVote;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NodeState {
    private ZVote vote;
    private int round;
    private ZNodeState status;

    private int id;
    private String address;

    public NodeState() {
        round = 0;
        status = ZNodeState.ELECTION;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ZVote getVote() {
        return vote;
    }

    public void setVote(ZVote vote) {
        log.info(" set vote , old : {} , new : {}",this.vote,vote);
        this.vote = vote;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public ZNodeState getStatus() {
        return status;
    }

    public void setStatus(ZNodeState status) {
        this.status = status;
    }


    public ZElectionMessage message(){
        ZElectionMessage message = new ZElectionMessage();
        message.setService(ie.home.msa.messages.Service.of("zab-node-service",address));
        message.setStatus(status);
        message.setVersion(1);
        message.setDsc("");
        message.setBody(new ZNotification(vote,round,id));
        log.info(" message: current {}", this);
        return message;
    }

    public void roundInc(){
        round++;
    }

    @Override
    public String toString() {
        return "NodeState{" +
                "vote=" + vote +
                ", round=" + round +
                ", status=" + status +
                ", id=" + id +
                ", address='" + address + '\'' +
                '}';
    }
}
