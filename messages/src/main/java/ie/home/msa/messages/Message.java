package ie.home.msa.messages;

import java.io.Serializable;

public abstract class Message<E extends Enum<E>,T extends Serializable> implements Serializable {
    private int version;
    private String dsc;
    private T body;
    private E status;

    private Service service;

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getDsc() {
        return dsc;
    }

    public void setDsc(String dsc) {
        this.dsc = dsc;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public E getStatus() {
        return status;
    }

    public void setStatus(E status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Message{" +
                "version=" + version +
                ", dsc='" + dsc + '\'' +
                ", body=" + body +
                ", status=" + status +
                ", service=" + service +
                '}';
    }
}
