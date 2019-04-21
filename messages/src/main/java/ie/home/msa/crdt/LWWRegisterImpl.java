package ie.home.msa.crdt;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class LWWRegisterImpl implements LWWRegister {
    private String value;
    private long id;

    public LWWRegisterImpl() {
        this.id = 0;
    }

    @Override
    public synchronized String value() {
        return value;
    }

    @Override
    public synchronized Checker assign(String value) {
        this.value = value;
        this.id = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        return new Checker(id,value);
    }

    @Override
    public synchronized void merge(Checker checker) {
        if (checker.newWin(id)) {
            this.value = checker.getValue();
        }
    }

    @Override
    public String toString() {
        return "LWWRegisterImpl{" +
                "value='" + value + '\'' +
                ", id=" + id +
                '}';
    }
}
