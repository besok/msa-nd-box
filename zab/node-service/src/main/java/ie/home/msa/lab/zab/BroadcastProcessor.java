package ie.home.msa.lab.zab;

import ie.home.msa.messages.*;
import ie.home.msa.sandbox.discovery.client.DiscoveryClient;
import ie.home.msa.zab.RecoveryStatus;
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

    private Set<String> ascNewLeaderSet;

    public BroadcastProcessor(DiscoveryClient client) {
        this.client = client;
        this.lock = new ReentrantLock(true);
        this.messageQueue = new HashSet<>();
        this.incomeMesSet = new HashSet<>();
        this.mesSet = new HashSet<>();
        this.ascNewLeaderSet = new HashSet<>();
    }

    Zid getLastZid() {
        return lastZid;
    }

    void setLastZid(Zid lastZid) {
        this.lastZid = lastZid;
    }

    public void setLeaderOther(String leaderAddress) {
        leaderInfo = new LeaderInfo(false, leaderAddress);
        ZRecoveryMessage m = ZRecoveryMessageBuilder.createMessage(
                client.getServiceName(),
                client.getServiceAddress(),
                RecoveryStatus.FOLLOWERINFO,
                getLastZid(),
                new ArrayList<>()
        );
        sendMessageToNode(m,leaderAddress,"recovery");
    }

    public void setLeaderItself(String leaderAddress) {
        lastZid.incEpoch();
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
                        ZWriteMessage commitMessage = buildMessage(leaderInfo.address, COMMIT, m);
                        sendMessageToNodes(filterByLeader(), commitMessage);
                        setLastZid(commitMessage.getBody().getZid());
                        log.info("quorum ... send commit ");
                        mesSet.add(m);
                        messageQueue.removeIf(v -> v.m.getBody().equals(message.getBody()));
                    }
                    break;
                case COMMIT:
                    boolean mes = removeMessageIf(m);
                    log.info("got commit message: if this node contains then fix it: {}", mes);
                    if (mes) {
                        mesSet.add(m);
                        setLastZid(m.getZid());
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
            sendMessageToNode(message, node, "message");
        }
    }

    private <T extends Message<?, ?>> void sendMessageToNode(T message, String address, String api) {
        try {
            ResponseEntity<Void> resp = client.getRestTemplate()
                    .postForEntity("http://" + address + "/" + api, message, Void.class);
            if (resp.getStatusCode().isError()) {
                log.error("something goes wrong : {}", resp.getStatusCodeValue());
            }
        } catch (Exception ex) {
            log.error("sending message is failed", ex);
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

    public void processRecovery(ZRecoveryMessage message) {
        lock.lock();
        try {
            log.info("got recovery message {}", message);
            RecoveryStatus status = message.getStatus();
            String[] nodes = client.getNodes();
            String sname = client.getServiceName();
            String sAdrr = client.getServiceAddress();
            Zid leaderZid = getLastZid();
            Zid incomeZid = message.getBody().getZid();
            ZRecoveryMessage recoveryMessage =
                    ZRecoveryMessageBuilder.createMessage(sname, sAdrr, status, leaderZid, new ArrayList<>());
            switch (status) {
                case FOLLOWERINFO:
                    recoveryMessage.setStatus(RecoveryStatus.SENDNEWLEADER);
                    sendMessageToNode(recoveryMessage, message.getService().getAddress(), "recovery");
                    if (leaderZid.compareTo(incomeZid) > 0) {
                        recoveryMessage.setStatus(RecoveryStatus.DIFF);
                        recoveryMessage.getBody().setMessageList(new ArrayList<>(mesSet));
                    } else {
                        recoveryMessage.setStatus(RecoveryStatus.TRUNC);
                    }
                    sendMessageToNode(recoveryMessage, message.getService().getAddress(), "recovery");
                    break;
                case ACKNEWLEADER:
                    ascNewLeaderSet.add(message.getService().getAddress());
                    int qs = ZabUtils.quorumSize(nodes.length);
                    if (qs <= ascNewLeaderSet.size()) {
                        leaderInfo.disableRecovery();
                    }
                    break;
                case SENDNEWLEADER:
                    if (incomeZid.getEpoch() < getLastZid().getEpoch()) {
                        // go to election
                    }
                case DIFF:
                case SNAP:
                    log.info("add all mes {}", incomeZid);
                    mesSet.addAll(message.getBody().getMessageList());
                    setLastZid(incomeZid);
                    recoveryMessage.setStatus(RecoveryStatus.ACKNEWLEADER);
                    sendMessageToNode(recoveryMessage, recoveryMessage.getService().getAddress(), "recovery");
                    leaderInfo.disableRecovery();

                    break;
                case TRUNC:
                    log.info("remove all mes > then {}", incomeZid);
                    mesSet.removeIf(m -> incomeZid.compareTo(m.getZid()) < 0);
                    sendMessageToNode(recoveryMessage, recoveryMessage.getService().getAddress(), "recovery");
                    leaderInfo.disableRecovery();
                    break;
            }
        } finally {
            lock.unlock();
        }
    }

    private static class LeaderInfo {
        private boolean isLeader;
        private String address;
        private boolean recoveryMode;

        LeaderInfo(boolean isLeader, String address) {
            this.isLeader = isLeader;
            this.address = address;
            this.recoveryMode = true;
        }

        public void disableRecovery() {
            recoveryMode = false;
            log.info(" next stage is broadcast ");
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
