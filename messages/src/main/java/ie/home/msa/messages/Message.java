package ie.home.msa.messages;


import java.io.Serializable;
import java.util.Map;

public class Message<T extends Serializable> implements Serializable {
    private T data;

    public T getData() {
        return data;
    }

    public Message<T> setData(T data){
        this.data = data;
        return this;
    }

    public static  <M extends Message<K>,K extends Serializable>  M build(K data,Type type){
        switch (type){
            case SERVICE_METRICS: {
                if(data instanceof ServiceMessage.Metrics) {
                    return (M) new ServiceMessage((ServiceMessage.Metrics) data);
                }

            }
            default: return null;
        }
    }

    public enum Type {
        SERVICE_METRICS,CHAT,DATA
    }
}
