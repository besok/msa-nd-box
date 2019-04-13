package ie.home.msa.sandbox.discovery.server;

public interface StorageListener {

    <T> void onEvent(Event event,String storage, String key, T val);

    enum Event {
        PUT, GET, REMOVE_KEY, REMOVE_VAL, INIT, CLEAN
    }
}