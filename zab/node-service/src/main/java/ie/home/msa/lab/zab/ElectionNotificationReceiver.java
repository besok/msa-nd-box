package ie.home.msa.lab.zab;

import ie.home.msa.messages.ElectionMessage;
import ie.home.msa.sandbox.discovery.client.DiscoveryClient;
import ie.home.msa.sandbox.discovery.client.InitializationOperation;
import ie.home.msa.zab.Zid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static ie.home.msa.messages.ElectionMessageBuilder.*;
import static ie.home.msa.zab.ZNodeState.ELECTION;

@Service
@Slf4j
public class ElectionNotificationReceiver  {
    protected String[] nodes;
    protected ElectionMessage currentMessage;
    protected final DiscoveryClient client;
    private Deque<ElectionMessage> electionQueue;
    private Lock lock;



    public ElectionMessage getState() {
        return currentMessage;
    }

    public Optional<ElectionMessage> pop() {
        lock.lock();
        try {
            if (electionQueue.isEmpty()) {
                log.info("get from q = empty");
                return Optional.empty();
            } else {
                ElectionMessage mes = electionQueue.pop();
                log.info("get from q = {}", mes);
                return Optional.of(mes);
            }
        } finally {
            lock.unlock();
        }

    }

    public ElectionNotificationReceiver(DiscoveryClient client) {
        this.client = client;
        this.electionQueue = new ArrayDeque<>();
        this.lock = new ReentrantLock(true);
        this.currentMessage = createInitMessage(client.getServiceName(), client.getServiceAddress(), 0);
    }

    public void processMessage(ElectionMessage message) {
        log.info(" process message {} ", message);
        lock.lock();
        try {
            String address = message.getService().getAddress();
            if (currentMessage.getStatus() == ELECTION) {
                electionQueue.push(message);
                if (message.getStatus() == ELECTION) {
                    int currentRound = currentMessage.getBody().getRound();
                    int incomingRound = message.getBody().getRound();
                    if (currentRound > incomingRound) {
                        sendMessage(address, currentMessage);
                    }
                }
            } else if (message.getStatus() == ELECTION) {
                sendMessage(address, currentMessage);
            }
        } finally {
            lock.unlock();
        }
    }

    public void sendMessage(String address, ElectionMessage message) {
        ResponseEntity<Void> resp = client.getRestTemplate().postForEntity("http://" + address + "/election",
                message, Void.class);
        if (resp.getStatusCode().is2xxSuccessful()) {
            log.info(" send message: address {}, sending: {} ", address, message);
        } else {
            String phrase = resp.getStatusCode().getReasonPhrase();
            int code = resp.getStatusCodeValue();
            log.info(" error {}-{} send message: incoming:{}, sending: {} ", code, phrase, address, message);
        }
    }

    public void sendMessageToOthers() {
        String address = client.getServiceAddress();
        String[] nodes = ZabUtils.filter(address, this.nodes);
        lock.lock();
        try {
            for (String node : nodes) {
                try {
                    sendMessage(node, currentMessage);
                    log.info(" send message {} to {}", currentMessage, address);
                } catch (Exception ex) {
                    log.info(" error to send {} to {}",currentMessage, node, ex);
                }
            }

        } finally {
            lock.unlock();
        }
    }

    public boolean electionStateInit(Zid zid) {
        this.nodes = client.getNodes();
        int id = ZabUtils.find(client.getServiceAddress(), nodes);
        this.currentMessage.getBody().roundInc();
        this.currentMessage.getBody().setId(id);
        this.currentMessage.getBody().getVote().setId(id);
        this.currentMessage.getBody().getVote().setZid(zid);
        this.currentMessage.setStatus(ELECTION);
        log.info(" initial operation: current {}, nodes {}", currentMessage, Arrays.toString(nodes));
        return true;
    }

}
