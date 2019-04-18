package ie.home.msa.sandbox.replication;

import ie.home.msa.crdt.PnCounter;
import ie.home.msa.sandbox.discovery.client.DiscoveryClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import static ie.home.msa.crdt.PnCounter.Op.DECREMENT;
import static ie.home.msa.crdt.PnCounter.Op.INCREMENT;

/**
 * sending state for every event.
 * operation is idempotent
 */
@Slf4j
public class PnCounterStateBasedService extends PnCounterService {
    public PnCounterStateBasedService(DiscoveryClient discoveryClient,
                                      PnCounter pnCounter,
                                      String[] nodes,
                                      int localIdx) {
        super(discoveryClient, pnCounter, nodes, localIdx);
    }

    public void increment() {
        pnCounter.generate(INCREMENT);
        PnCounter.State state = pnCounter.state();
        log.info(" increment node: {}, state{}", this.localIdx, state);
        mergeNodes(state);
    }

    public void decrement() {
        pnCounter.generate(DECREMENT);
        PnCounter.State state = pnCounter.state();
        log.info(" decrement node: {}, state{}", this.localIdx, state);
        mergeNodes(state);
    }

}
