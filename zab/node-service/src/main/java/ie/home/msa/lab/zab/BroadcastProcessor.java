package ie.home.msa.lab.zab;

import ie.home.msa.messages.Message;
import ie.home.msa.messages.ZWriteMessage;
import ie.home.msa.messages.ZWriteMessageBuilder;
import ie.home.msa.sandbox.discovery.client.DiscoveryClient;
import ie.home.msa.zab.WriteMessage;
import ie.home.msa.zab.WriteStatus;
import ie.home.msa.zab.Zid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static ie.home.msa.zab.WriteStatus.*;

@Slf4j
@Service
public class BroadcastProcessor {
    private LeaderInfo leaderInfo;
    private Zid lastZid;
    private final Lock lock;
    private final DiscoveryClient client;

    private Set<QValue> messageQueue;
    private Set<WriteMessage> incomeMesSet;
    private Set<WriteMessage> mesSet;

    public BroadcastProcessor(DiscoveryClient client) {
        this.client = client;
        this.lock = new ReentrantLock(true);
        this.messageQueue = new HashSet<>();
        this.incomeMesSet = new HashSet<>();
        this.mesSet = new HashSet<>();
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

    public void processObject(String obj) {
        lock.lock();
        try {
            if (Objects.isNull(leaderInfo)) {
                log.info("got object {}, but it is not broadcast phase ", obj);
            } else {
                if (leaderInfo.isLeader) {
                    log.info(" get object, process as leader");
                    processMessageAsLeader(obj);
                } else {
                    log.info(" get object, send to leader");
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
            log.info(" come message {}", message);
            WriteStatus status = message.getStatus();
            WriteMessage m = message.getBody();
            switch (status) {
                case INCOME:
                    incomeMesSet.add(m);
                    String address = client.getServiceAddress();
                    ZWriteMessage mesToSend = buildMessage(address, ACK, m);
                    ResponseEntity<Void> resp = client.getRestTemplate().postForEntity(
                            "http://" + leaderInfo.address + "/message", mesToSend, Void.class
                    );
                    if (resp.getStatusCode().isError()) {
                        log.error("something goes wrong to send ack mes: {}", resp.getStatusCodeValue());
                    } else {
                        log.info("send ack message to leader {}", mesToSend);
                    }
                    break;
                case ACK:
                    if (checkQuorum(message)) {
                        log.info("quorum ... send commit ");
                        ZWriteMessage commitMessage = buildMessage(leaderInfo.address, COMMIT, m);
                        sendMessageToNodes(filterByLeader(), commitMessage);
                        mesSet.add(m);
                        messageQueue.removeIf(v -> v.m.getBody().equals(message.getBody()));
                    }
                    break;
                case COMMIT:
                    boolean mes = removeMessageIf(m);
                    log.info("got commit message: if this node contains then fix it: {}", mes);
                    if (mes) {
                        mesSet.add(m);
                    }
                    break;
            }
        } catch (Exception ex) {
            log.error("processing message {} is failed", message, ex);
        } finally {
            lock.unlock();
        }
    }

    private ZWriteMessage buildMessage(String address, WriteStatus status, WriteMessage message) {
        return ZWriteMessageBuilder.createMessage(
                address,
                status,
                message.getZid(),
                message.getData()
        );
    }

    private boolean removeMessageIf(WriteMessage message) {
        return incomeMesSet.removeIf(e -> e.equals(message));

    }


    private void processMessageAsLeader(String obj) {
        String[] nodes = filterByLeader();
        lastZid.incCounter();
        ZWriteMessage message = ZWriteMessageBuilder.createMessage(
                leaderInfo.address,
                INCOME,
                lastZid,
                obj
        );
        lock.lock();
        try {
            messageQueue.add(new QValue(message, ZabUtils.quorumSize(nodes.length)));
            sendMessageToNodes(nodes, message);
        } finally {
            lock.unlock();
        }
    }

    private void sendMessageToNodes(String[] nodes, ZWriteMessage message) {
        for (String node : nodes) {
            try {
                ResponseEntity<Void> resp = client.getRestTemplate()
                        .postForEntity("http://" + node + "/message", message, Void.class);
                if (resp.getStatusCode().isError()) {
                    log.error("something goes wrong : {}", resp.getStatusCodeValue());
                } else {
                    log.info("send to node {}, message {}", node, message);
                }
            } catch (Exception ex) {
                log.error("sending message is failed", ex);
            }
        }
    }

    private String[] filterByLeader() {
        return ZabUtils.filter(leaderInfo.address, client.getNodes());
    }

    private void sendToLeader(Object message) {
        log.info("got write message {}, send it to leader", message);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<Void> resp = client.getRestTemplate()
                .postForEntity("http://" + leaderInfo.address + "/write", new HttpEntity<>(message, headers), Void.class);
        if (resp.getStatusCode().isError()) {
            log.error("something goes wrong : {}", resp.getStatusCodeValue());
        }
    }

    private boolean checkQuorum(ZWriteMessage mes) {
        String address = mes.getService().getAddress();
        return messageQueue.stream()
                .filter(v -> v.m.getBody().equals(mes.getBody()))
                .map(v -> v.check(address))
                .findAny()
                .orElse(false);
    }

    private static class LeaderInfo {
        private boolean isLeader;
        private String address;

        LeaderInfo(boolean isLeader, String address) {
            this.isLeader = isLeader;
            this.address = address;
        }
    }

    private static class QValue {
        private ZWriteMessage m;
        private Set<String> counter;
        private int qs;

        QValue(ZWriteMessage m, int qs) {
            this.counter = new HashSet<>();
            this.m = m;
            this.qs = qs-1;
        }


        boolean check(String address) {
            int prevSize = counter.size();
            counter.add(address);
            int newSize = counter.size();
            log.info("put address:{}, prev:{}, new:{}", address, prevSize, newSize);
            return newSize >= qs;
        }
    }
}
