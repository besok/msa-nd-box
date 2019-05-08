package ie.home.msa.messages;

import java.io.Serializable;
import java.util.List;

public interface Task<V> extends Serializable {

    List<Task<V>> split();

    Task<V> process();

    boolean accumulate(V data);

    V getResult();

}
