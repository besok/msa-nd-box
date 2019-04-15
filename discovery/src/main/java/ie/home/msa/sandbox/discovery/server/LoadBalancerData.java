package ie.home.msa.sandbox.discovery.server;

public class LoadBalancerData {
    private String address;
    private String strategy;


    public LoadBalancerData(String address, String strategy) {
        this.address = address;
        this.strategy = strategy;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return address+"="+strategy;
    }
    public static LoadBalancerData fromString(String val){
        String[] vals = val.split("=");
        return new LoadBalancerData(vals[0],vals[1]);
    }
}
