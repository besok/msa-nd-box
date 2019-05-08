package ie.home.msa.lab.batch;

public class Worker {
    private int id;
    private String address;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Worker() {
    }

    public Worker(int id, String address) {
        this.id = id;
        this.address = address;
    }

    @Override
    public String toString() {
        return "Worker{" +
                "size=" + id +
                ", address='" + address + '\'' +
                '}';
    }
}
