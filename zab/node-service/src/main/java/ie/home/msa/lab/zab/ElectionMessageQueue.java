package ie.home.msa.lab.zab;

import ie.home.msa.messages.ZElectionMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class ElectionMessageQueue {
    private Deque<ZElectionMessage> electionQueue;
    private final Lock lock;

    public ElectionMessageQueue() {
        lock = new ReentrantLock(true);
        this.electionQueue = new ArrayDeque<>();
    }

    public void push(ZElectionMessage mes) {
        lock.lock();
        try {
            electionQueue.push(mes);
            log.info("put to the queue {} ", mes);
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        return electionQueue.isEmpty();
    }

    public Optional<ZElectionMessage> pop() {
        lock.lock();
        try {
            if (electionQueue.isEmpty()) {
                log.info("get from q = empty");
            } else {
                ZElectionMessage pop = electionQueue.pop();
                log.info("get from q = {}", pop);
                return Optional.of(pop);
            }
        } finally {
            lock.unlock();
        }
        return Optional.empty();
    }

    public void clear() {

        lock.lock();
        try {
            electionQueue.clear();
            log.info(" queue is cleared");
        } finally {
            lock.unlock();
        }
    }


}
