package ie.home.msa.messages;

import java.io.Serializable;

public class ServiceRegisterMessage extends ServiceMessage<ServiceRegisterMessage.Service> {


    public static class Service implements Serializable {
        private String name;
        private String address;

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
    }
}
