package ie.home.msa.sandbox.discovery.server;

import java.io.Serializable;

public class CircuitBreakerData implements Serializable {
    private String address;
    private String status;
    private int version;

    public CircuitBreakerData(String address, String status) {
        this.address = address;
        this.status = status;
        this.version= 0;
    }

    public CircuitBreakerData(String address, String status, int version) {
        this.address = address;
        this.status = status;
        this.version = version;
    }

    public CircuitBreakerData(String[] vals) {
        this.address = vals[0];
        this.status = vals[1];
        this.version= Integer.valueOf(vals[2]);
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String toString() {
        return address + "=" + status+"="+version;
    }


}