package ie.home.msa.lab.zab;

import ie.home.msa.messages.ElectionMessage;
import ie.home.msa.sandbox.discovery.client.DiscoveryClient;
import ie.home.msa.zab.ZVote;
import ie.home.msa.zab.Zid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static ie.home.msa.lab.zab.ZabUtils.*;
import static ie.home.msa.zab.ZNodeState.ELECTION;

@Service
@Slf4j
public class ElectionNotificationReceiver {
    protected String[] nodes;
    protected final DiscoveryClient client;
    private final ElectionMessageQueue queue;
    private Lock lock;

    private final NodeState state;


    public ElectionNotificationReceiver(DiscoveryClient client, ElectionMessageQueue queue, NodeState state) {
        this.client = client;
        this.queue = queue;
        this.state = state;
        this.lock = new ReentrantLock(true);
    }

    public void processMessage(ElectionMessage message) {
        lock.lock();
        try {
            log.info(" income  {} , current {}", message, state);
            String address = message.getService().getAddress();
            if (state.getStatus() == ELECTION) {
                queue.push(message);
                if (message.getStatus() == ELECTION) {
                    int crRound = state.getRound();
                    if (crRound > round(message)) {
                        sendMessage(address, state.message());
                    }
                }
            } else if (message.getStatus() == ELECTION) {
                sendMessage(address, state.message());
            }
        } finally {
            lock.unlock();
        }
    }

    public void sendMessage(String address, ElectionMessage message) {
        ResponseEntity<Void> resp = client.getRestTemplate().postForEntity("http://" + address + "/election",
                message, Void.class);
        if (resp.getStatusCode().is2xxSuccessful()) {
            log.info(" send message: {} to address {}", message, address);
        } else {
            String phrase = resp.getStatusCode().getReasonPhrase();
            int code = resp.getStatusCodeValue();
            log.info(" error {} - {} send message:{} to address: {} ", code, phrase, message, address);
        }
    }

    public void sendMessageToOthers() {
        String address = client.getServiceAddress();
        String[] filteredNodes = filter(address, this.nodes);
        lock.lock();
        try {
            for (String node : filteredNodes) {
                ElectionMessage message = state.message();
                try {
                    sendMessage(node, message);
                } catch (Exception ex) {
                    log.info(" [broadcast] error to send to others {} to {}", message, node, ex);
                }
            }

        } finally {
            lock.unlock();
        }
    }

    public boolean electionStateInit(Zid zid) {
        String serviceAddress = client.getServiceAddress();
        nodes = client.getNodes();
        int id = find(serviceAddress, nodes);
        state.setStatus(ELECTION);
        state.roundInc();
        state.setVote(new ZVote(id, zid));
        state.setAddress(serviceAddress);
        state.setId(id);
        return true;
    }

}
