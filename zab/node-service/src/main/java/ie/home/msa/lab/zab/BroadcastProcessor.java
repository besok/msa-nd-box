package ie.home.msa.lab.zab;

import ie.home.msa.zab.Zid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BroadcastProcessor {

    private Zid lastZid;

    Zid getLastZid() {
        return lastZid;
    }

    void setLastZid(Zid lastZid) {
        this.lastZid = lastZid;
    }
}
