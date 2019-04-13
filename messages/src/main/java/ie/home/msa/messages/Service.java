package ie.home.msa.messages;

import java.io.Serializable;

public class Service implements Serializable {
    private String name;
    private String address;

    public Service(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public Service() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public static Service of(String name,String address){
        return new Service(name,address);
    }
}
