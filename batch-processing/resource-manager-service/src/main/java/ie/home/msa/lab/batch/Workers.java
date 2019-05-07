package ie.home.msa.lab.batch;

import ie.home.msa.sandbox.discovery.server.StorageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import static ie.home.msa.sandbox.discovery.server.StorageListenerHandler.FileStorageType.SERVICE_REGISTRY;

@Service
@Slf4j
public class Workers implements StorageListener {
    private List<Worker> workerList;

    public List<Worker> getWorkerList() {
        return workerList;
    }

    private int id() {
        return workerList.size();
    }

    public Workers() {
        workerList = new CopyOnWriteArrayList<>();
    }

    void add(Worker worker) {
        log.info(" add worker {} ", worker);
        workerList.add(worker);
    }

    private void remove(String address) {
        boolean res = workerList.removeIf(w -> w.getAddress().equals(address));
        log.info(" remove worker {} = {}", address, res);
    }


    @Override
    public <T> void onEvent(Event event, String storage, String key, T val) {
        if (Objects.equals(storage, SERVICE_REGISTRY.getName())) {
            switch (event) {
                case PUT:
                    add(new Worker(id(), (String) val));
                    break;
                case REMOVE_VAL:
                    remove((String) val);
                    break;
                default:
            }
        }
    }
}
