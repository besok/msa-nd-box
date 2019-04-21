package ie.home.sandbox.crdt;

import ie.home.msa.crdt.PnCounter;
import ie.home.msa.sandbox.discovery.client.DiscoveryClient;
import lombok.extern.slf4j.Slf4j;

import static ie.home.msa.crdt.PnCounter.Op.DECREMENT;
import static ie.home.msa.crdt.PnCounter.Op.INCREMENT;

/**
 * sending update event for input event
 * operation is not idempotent. It must be ces (local event consistency)
 */
@Slf4j
public class PnCounterOperationBasedService extends PnCounterService {

    public PnCounterOperationBasedService(DiscoveryClient discoveryClient,
                                          PnCounter pnCounter,
                                          String[] nodes,
                                          int localIdx) {
        super(discoveryClient,pnCounter,nodes,localIdx);
    }


    public void increment() {
        PnCounter.Effector effector = pnCounter.generate(INCREMENT);
        log.info(" increment node: {}", this.localIdx);
        updateNodes(effector);
    }

    public void decrement() {
        PnCounter.Effector effector = pnCounter.generate(DECREMENT);
        log.info(" decrement node: {}", this.localIdx);
        updateNodes(effector);

    }
}
