package ie.home.msa.lab.zab;

import ie.home.msa.messages.ElectionMessage;
import ie.home.msa.messages.ElectionMessageBuilder;
import ie.home.msa.sandbox.discovery.client.DiscoveryClient;
import ie.home.msa.sandbox.discovery.client.InitializationOperation;
import ie.home.msa.zab.ZNodeState;
import ie.home.msa.zab.ZNotification;
import ie.home.msa.zab.Zid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.OptionalInt;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

import static ie.home.msa.messages.ElectionMessageBuilder.*;
import static ie.home.msa.zab.ZNodeState.ELECTION;

@Service
@Slf4j
public class ElectionProcessor implements InitializationOperation {
    private Map<>
    private String[] nodes;
    private Zid lastZid;
    private ElectionMessage currentMessage;
    private Deque<ElectionMessage> electionQueue;
    private Lock lock;

    private final DiscoveryClient client;

    public ElectionProcessor(DiscoveryClient client) {
        this.client = client;
        this.electionQueue = new ArrayDeque<>();
        this.lock = new ReentrantLock(true);
        this.currentMessage = createInitMessage(client.getServiceName(), client.getServiceAddress(), 0);
        this.lastZid = new Zid(0, 0);
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
        int id = Utils.find(client.getServiceAddress(), nodes);
        this.currentMessage.getBody().roundInc();
        this.currentMessage.getBody().setId(id);
        this.currentMessage.getBody().getVote().setId(id);
        this.currentMessage.setStatus(ELECTION);
        log.info(" initial operation: current {}, nodes {}", currentMessage, Arrays.toString(nodes));
        return true;
    }

}
