package ie.home.msa.lab.batch;

public class Worker {
    private String address;
    private Process process;
    private int id;



    public Worker() {
    }


    public void destroy(){
        process.destroy();
    }


    @Override
    public String toString() {
        return "Worker{" +
                "address='" + address + '\'' +
                ", process=" + process +
                '}';
    }

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

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }
}
