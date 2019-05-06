package ie.home.msa.lab.batch;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class Workers {
    private List<Worker> workerList;


    public int size(){
        return workerList.size();
    }

    public Workers() {
        workerList = new CopyOnWriteArrayList<>();
    }

    public void add(Worker worker){
        workerList.add(worker);
    }

    public Optional<Worker> findBy(String address){
       return workerList.stream().filter(w -> w.getAddress().equals(address)).findFirst();
    }

    public void destroy(String address){
        findBy(address).ifPresent(Worker::destroy);
    }



}
