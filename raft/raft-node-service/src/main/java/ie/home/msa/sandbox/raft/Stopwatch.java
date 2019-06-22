package ie.home.msa.sandbox.raft;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

@Slf4j
public class Stopwatch {

    private final int threshold;
    private AtomicInteger timer;

    private Supplier<Boolean> action;

    public Stopwatch(Supplier<Boolean> action) {
        this.threshold = new Random().nextInt(350) + 150;
        this.timer = new AtomicInteger(0);
        this.action = action;
        log.info(" thershold:{},",threshold);
    }

    public void reset() {
        timer.set(0);
    }


    public void watch() {
        CompletableFuture.runAsync(() -> {
            int step = 50;
            while (true) {
                if (timer.addAndGet(step) > threshold) {
                    log.info("threshold is done : {} > {}",timer.get(),threshold);
                    if (action.get()) sleep(step);
                } else sleep(step);
            }
        });
    }

    private void sleep(int step) {
        try {
            Thread.sleep(step);
        } catch (InterruptedException ignore) {
            Thread.currentThread().interrupt();
        }
    }


}
