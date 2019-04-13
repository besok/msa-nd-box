package ie.home.msa.sandbox.discovery.server;

import java.io.IOException;
import java.util.List;

public interface FileStorage<T> {
    List<T> get(String key);
    boolean contains(String key);
    boolean put(String key, T val);
    boolean removeKey(String key);
    boolean removeVal(String key, T val);
    T getRand(String service);
    void clean() throws IOException;
}
