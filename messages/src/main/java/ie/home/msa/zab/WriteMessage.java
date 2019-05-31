package ie.home.msa.zab;

import java.io.Serializable;

public class WriteMessage implements Serializable {
    private Zid zid;
    private Object data;

    public WriteMessage(Zid zid, Object data) {
        this.zid = zid;
        this.data = data;
    }

    public Zid getZid() {
        return zid;
    }

    public void setZid(Zid zid) {
        this.zid = zid;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WriteMessage message = (WriteMessage) o;

        if (zid.compareTo(message.zid) != 0) return false;
        return data.equals(message.data);
    }

    @Override
    public int hashCode() {
        int result = zid.hashCode();
        result = 31 * result + data.hashCode();
        return result;
    }
}
