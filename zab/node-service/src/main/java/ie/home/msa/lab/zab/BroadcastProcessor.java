package ie.home.msa.lab.zab;

import ie.home.msa.messages.ZWriteMessage;
import ie.home.msa.messages.ZWriteMessageBuilder;
import ie.home.msa.sandbox.discovery.client.DiscoveryClient;
import ie.home.msa.zab.WriteMessage;
import ie.home.msa.zab.WriteStatus;
import ie.home.msa.zab.Zid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class BroadcastProcessor {
    private LeaderInfo leaderInfo;
    private Zid lastZid;
    private final Lock lock;
    private final DiscoveryClient client;

    private Queue<QValue> messageQueue;
    private Set<WriteMessage> writeMessageSet;

    public BroadcastProcessor(DiscoveryClient client) {
        this.client = client;
        this.lock = new ReentrantLock(true);
        this.messageQueue = new ArrayDeque<>();
        this.writeMessageSet = new HashSet<>();
    }

    Zid getLastZid() {
        return lastZid;
    }

    void setLastZid(Zid lastZid) {
        this.lastZid = lastZid;
    }

    public void setLeaderOther(String leaderAddress) {
        leaderInfo = new LeaderInfo(false, leaderAddress);
    }

    public void setLeaderItself(String leaderAddress) {
        leaderInfo = new LeaderInfo(true, leaderAddress);
    }

    public void processObject(Object obj) {
        lock.lock();
        try {
            if (Objects.isNull(leaderInfo)) {
                log.info("got object {}, but it is not broadcast phase ", obj);
            } else {
                if (leaderInfo.isLeader) {
                    processMessageAsLeader(obj);
                } else {
                    sendToLeader(obj);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public void commitMessage(ZWriteMessage message) {
        lock.lock();
        try {
            WriteStatus status = message.getStatus();
            switch (status) {
                case INCOME:
                    writeMessageSet.add(message.getBody());
                    ZWriteMessage mesToSend = ZWriteMessageBuilder.createMessage(
                            client.getServiceAddress(),
                            WriteStatus.ACK,
                            message.getBody().getZid(),
                            message.getBody().getData());
                    ResponseEntity<Void> resp = client.getRestTemplate().postForEntity(
                            "http://" + leaderInfo.leaderAddress + "/message", mesToSend, Void.class
                    );
                    if (resp.getStatusCode().isError()) {
                        log.error("something goes wrong : {}", resp.getStatusCodeValue());
                    }
                    log.info(" come message {}, put to queue , send with dif status", message, mesToSend);
                    break;
                case ACK:

                case COMMIT:
            }
        } catch (Exception ex) {
            log.error("processing message {} is failed", message, ex);
        } finally {
            lock.unlock();
        }
    }

    private void processMessageAsLeader(Object obj) {
        String[] nodes = ZabUtils.filter(leaderInfo.leaderAddress, client.getNodes());
        lastZid.incCounter();
        ZWriteMessage message = ZWriteMessageBuilder.createMessage(leaderInfo.leaderAddress, WriteStatus.INCOME, lastZid, obj);
        lock.lock();
        try {
            messageQueue.add(new QValue(message, ZabUtils.quorumSize(nodes.length)));
            for (String node : nodes) {
                try {
                    ResponseEntity<Void> resp = client.getRestTemplate()
                            .postForEntity("http://" + node + "/message", message, Void.class);
                    if (resp.getStatusCode().isError()) {
                        log.error("something goes wrong : {}", resp.getStatusCodeValue());
                    }
                } catch (Exception ex) {
                    log.error("sending message is failed", ex);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    private void sendToLeader(Object message) {
        log.info("got write message {}, send it to leader", message);
        ResponseEntity<Void> resp = client.getRestTemplate()
                .postForEntity("http://" + leaderInfo.leaderAddress + "/write", message, Void.class);
        if (resp.getStatusCode().isError()) {
            log.error("something goes wrong : {}", resp.getStatusCodeValue());
        }
    }


    private class LeaderInfo {
        private boolean isLeader;
        private String leaderAddress;

        public LeaderInfo(boolean isLeader, String leaderAddress) {
            this.isLeader = isLeader;
            this.leaderAddress = leaderAddress;
        }
    }

    private class QValue {
        private ZWriteMessage m;
        private Set<String> counter;
        private int qs;

        public QValue(ZWriteMessage m, int qs) {
            this.counter = new HashSet<>();
            this.m = m;
            this.qs = qs;
        }


        boolean check(String address) {
            counter.add(address);
            return counter.size() >= qs;
        }
    }
}
