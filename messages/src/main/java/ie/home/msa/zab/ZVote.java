package ie.home.msa.zab;

import java.io.Serializable;
import java.util.Objects;

public class ZVote implements Serializable,Comparable<ZVote> {
    private int id;
    private Zid zid;

    public ZVote() {
    }

    public ZVote(int id, Zid zid) {
        this.id = checkNegative(id);
        this.zid = zid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = checkNegative(id);
    }

    public Zid getZid() {
        return zid;
    }

    public void setZid(Zid zid) {
        this.zid = zid;
    }

    @Override
    public String toString() {
        return "ZVote{" +
                "id=" + id +
                ", zid=" + zid +
                '}';
    }

    @Override
    public int compareTo(ZVote o) {
        Zid nextZid = o.getZid();
        int resZid = zid.compareTo(nextZid);
        if(resZid != 0) {
            return resZid;
        }

        return id - o.id;
    }
    private int checkNegative(int el){
        if(el < 0) {
            throw new IllegalArgumentException("must be positive ");
        }
        return el;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return this.compareTo((ZVote) o) == 0;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (zid != null ? zid.hashCode() : 0);
        return result;
    }
}
