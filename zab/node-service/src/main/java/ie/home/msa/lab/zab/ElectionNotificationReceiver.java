package ie.home.msa.lab.zab;

import ie.home.msa.messages.ElectionMessage;
import ie.home.msa.sandbox.discovery.client.DiscoveryClient;
import ie.home.msa.sandbox.discovery.client.InitializationOperation;
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
public class ElectionNotificationReceiver implements InitializationOperation {
    private String[] nodes;
    private ElectionMessage currentMessage;
    private Deque<ElectionMessage> electionQueue;
    private Lock lock;

    private final DiscoveryClient client;


    public ElectionMessage getCurrentMessage() {
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
                log.info("get from q = {}",mes);
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
            if (currentMessage.getStatus() == ELECTION) {
                electionQueue.push(message);
                if (message.getStatus() == ELECTION) {
                    int currentRound = currentMessage.getBody().getRound();
                    int incomingRound = message.getBody().getRound();
                    if (currentRound > incomingRound) {
                        sendMessage(message);
                    }
                }
            } else if (message.getStatus() == ELECTION) {
                sendMessage(message);
            }
        } finally {
            lock.unlock();
        }
    }

    public void sendMessage(ElectionMessage incomingMessage) {
        String address = incomingMessage.getService().getAddress();
        ResponseEntity<Void> resp = client.getRestTemplate().postForEntity("http://" + address + "/election", currentMessage, Void.class);
        if (resp.getStatusCode().is2xxSuccessful()) {
            log.info("send message: incoming:{}, sending: {} ", incomingMessage, currentMessage);
        } else {
            String phrase = resp.getStatusCode().getReasonPhrase();
            int code = resp.getStatusCodeValue();
            log.info("error {}-{} send message: incoming:{}, sending: {} ", code, phrase, incomingMessage, currentMessage);
        }
    }


    @Override
    public Boolean operate() {
        return electionStateInit();
    }

    public boolean electionStateInit() {
        this.nodes = client.getNodes();
        int id = ZabUtils.find(client.getServiceAddress(), nodes);
        this.currentMessage.getBody().roundInc();
        this.currentMessage.getBody().setId(id);
        this.currentMessage.getBody().getVote().setId(id);
        this.currentMessage.setStatus(ELECTION);
        log.info(" initial operation: current {}, nodes {}", currentMessage, Arrays.toString(nodes));
        return true;
    }

}
