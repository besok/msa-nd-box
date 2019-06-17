package ie.home.msa.messages;

import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Service service = (Service) o;

        if (!Objects.equals(name, service.name)) return false;
        return Objects.equals(address, service.address);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (address != null ? address.hashCode() : 0);
        return result;
    }
}
