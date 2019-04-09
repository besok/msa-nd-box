package ie.home.msa.sandbox.discovery.client;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CircuitBreakerMethodStore {
    private static Map<String,Integer>  params;
    private static ConcurrentHashMap<String,Integer> values;
    static {
        params = new HashMap<>();
        values = new ConcurrentHashMap<>();
    }

    public static void put(String key,Integer val){
        params.put(key,val);
        values.put(key,0);
    }
    public static boolean contains(String key){
        return params.containsKey(key);
    }

    public static void update(String key,Integer val){
        values.put(key,val);
    }

    public static boolean checkTroubles(){
        for (Map.Entry<String, Integer> entry : params.entrySet()) {
            Integer param = entry.getValue();
            Integer value = values.get(entry.getKey());
            if(param > 0 && value > param){
                return true;
            }
        }
        return false;
    }
}
