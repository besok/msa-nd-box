package ie.home.msa.lab.zab;

import ie.home.msa.messages.ElectionMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class ElectionProcessor {
    private final ElectionNotificationReceiver notificationReceiver;

    private static final int THRESHOLD = 8000;
    private static int TIMEOUT = new Random().nextInt(1000);

    private final Map<Integer,ElectionMessage> outOfElectionMap;
    private final Map<Integer, ElectionMessage> receivedVoteMap;
    private final Lock lock;

    public ElectionProcessor(ElectionNotificationReceiver notificationReceiver) {
        this.notificationReceiver = notificationReceiver;
        this.receivedVoteMap = new HashMap<>();
        this.outOfElectionMap = new HashMap<>();
        this.lock = new ReentrantLock(true);
    }




}
