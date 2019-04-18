package ie.home.msa.sandbox.replication;

import ie.home.msa.crdt.CRDTException;
import ie.home.msa.crdt.PnCounter;
import ie.home.msa.crdt.PnCounterImpl;
import ie.home.msa.sandbox.discovery.client.DiscoveryClient;
import ie.home.msa.sandbox.discovery.client.InitializationHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class PnCounterController implements InitializationHandler {

    private PnCounterService service;
    private DiscoveryClient discoveryClient;


    public PnCounterController(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @Override
    public boolean initialization() {
        String[] nodes = discoveryClient.getNodes();
        int idx = discoveryClient.findIdx(nodes);
        if(idx < 0){
            throw new CRDTException();
        }
        int nodeCount = nodes.length;
        PnCounter pnCounter =  new PnCounterImpl(nodeCount, idx);
        service = new PnCounterStateBasedService(discoveryClient,pnCounter,nodes,idx);
        log.info("init pn-counter, nodes: {}, local index: {}", nodeCount, idx);
        return true;
    }


    @RequestMapping(path = "/crdt/counter/inc", method = RequestMethod.GET)
    public void increment(){
        service.increment();
    }

    @RequestMapping(path = "/crdt/counter/value", method = RequestMethod.GET)
    public long value(){
        return service.value();
    }

    @RequestMapping(path = "/crdt/counter/dec", method = RequestMethod.GET)
    public void decrement() {
        service.decrement();
    }

    @RequestMapping(path = "/crdt/counter/update", method = RequestMethod.POST)
    public void update(@RequestBody PnCounter.Effector effector) {
        service.update(effector);
    }

    @RequestMapping(path = "/crdt/counter/merge", method = RequestMethod.POST)
    public void merge(@RequestBody PnCounter.State state) {
        service.merge(state);
    }

}
