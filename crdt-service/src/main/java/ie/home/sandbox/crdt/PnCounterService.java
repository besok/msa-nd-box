package ie.home.sandbox.crdt;

import ie.home.msa.crdt.PnCounter;
import ie.home.msa.sandbox.discovery.client.DiscoveryClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

/**
 * base service for counter. it has 2 implementation:
 * operation based : @see {@link PnCounterOperationBasedService}
 * state based @see {@link PnCounterStateBasedService}
 */
@Slf4j
public abstract class PnCounterService {

    protected DiscoveryClient discoveryClient;
    protected PnCounter pnCounter;
    protected int localIdx;
    private String[] nodes;

    public PnCounterService(DiscoveryClient discoveryClient, PnCounter pnCounter, String[] nodes, int localIdx) {
        this.discoveryClient = discoveryClient;
        this.pnCounter = pnCounter;
        this.nodes = nodes;
        this.localIdx = localIdx;
    }

    public long value() {
        return pnCounter.value();
    }

    public abstract void increment();
    public abstract void decrement();

    protected void updateNodes(PnCounter.Effector effector) {
        RestTemplate restTemplate = discoveryClient.getRestTemplate();
        int ind = 0;
        for (String node : nodes) {
            if (ind != localIdx) {
                String url = "http://" + node + "/crdt/counter/update";
                restTemplate.postForLocation(url, effector);
                log.info(" sending delta to {}", url);
            }
            ind++;
        }
    }
    protected void mergeNodes(PnCounter.State state) {
        RestTemplate restTemplate = discoveryClient.getRestTemplate();
        int ind = 0;
        for (String node : nodes) {
            if (ind != localIdx) {
                String url = "http://" + node + "/crdt/counter/merge";
                restTemplate.postForLocation(url, state);
                log.info(" sending state to {}", url);
            }
            ind++;
        }
    }
    protected void merge(PnCounter.State state) {
        log.info(" getting update : state before {}, state {}", pnCounter, state);
        pnCounter.merge(state);
        log.info(" getting update : state after {}", pnCounter);
    }
    protected void update(@RequestBody PnCounter.Effector effector) {
        log.info(" getting update : state before {}", pnCounter);
        pnCounter.update(effector);
        log.info(" getting update : state after {}", pnCounter);
    }

}
