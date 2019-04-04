package ie.home.msa.messages;


import java.io.Serializable;

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
            case SERVICE: {
                if(data instanceof String){
                    return (M) new ServiceMessage((String)data);
                }
                else {
                    return (M) new ServiceMessage(data.toString());
                }
            }
            default: return null;
        }
    }

    public enum Type {
        SERVICE,CHAT,DATA
    }
}
