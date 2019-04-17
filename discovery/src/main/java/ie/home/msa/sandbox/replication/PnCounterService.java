package ie.home.msa.sandbox.replication;

import ie.home.msa.crdt.PnCounter;
import ie.home.msa.crdt.PnCounterImpl;
import ie.home.msa.sandbox.discovery.client.DiscoveryClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@Slf4j
@RestController
public class PnCounterService {

    @Value("${data-replication-node-count}")
    private String nodeCount;

    private PnCounter pnCounter;
    private DiscoveryClient discoveryClient;
    private String[] nodes;
    private int localIdx;
    public PnCounterService(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @RequestMapping(path = "/crdt/init", method = RequestMethod.GET)
    public void initialization() {
        this.nodes = discoveryClient.getNodes();
        this.localIdx = discoveryClient.findIdx(nodes);
        int nodeCount = Integer.parseInt(this.nodeCount);
        this.pnCounter = new PnCounterImpl(nodeCount, this.localIdx);
        log.info("init pn-counter, nodes: {}, local index: {}", nodeCount, this.localIdx);
    }


    @RequestMapping(path = "/crdt/pncounter/inc", method = RequestMethod.GET)
    public void increment() {
        PnCounter.Effector effector = pnCounter.generate(PnCounter.Op.INCREMENT);
        log.info(" increment node: {}",this.localIdx);
        updateNodes(effector);
    }

    private void updateNodes(PnCounter.Effector effector) {
        RestTemplate restTemplate = discoveryClient.getRestTemplate();

        for (String node : nodes) {
            String url = "http://" + node + "/crdt/pncounter/update";
            restTemplate.postForLocation(url, effector);
            log.info(" sending delta to {}", url);
        }
    }

    @RequestMapping(path = "/crdt/pncounter/dec", method = RequestMethod.GET)
    public void decrement() {
        PnCounter.Effector effector = pnCounter.generate(PnCounter.Op.DECREMENT);
        log.info(" decrement node: {}",this.localIdx);
        updateNodes(effector);

    }




    @RequestMapping(path = "/crdt/pncounter/update", method = RequestMethod.POST)
    public boolean update(@RequestBody PnCounter.Effector effector) {
        log.info(" getting update : state before {}",pnCounter);
        pnCounter.update(effector);
        log.info(" getting update : state after {}",pnCounter);
        return true;
    }
}
