package ie.home.msa.messages;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ServiceRegisterMessage extends ServiceMessage<ServiceRegisterMessage.Service> {


    public static class Service implements Serializable {
        private String name;
        private String address;

        private Map<String, Integer> properties;

        public Map<String, Integer> getProperties() {
            return properties;
        }

        public void setProperties(Map<String, Integer> properties) {
            this.properties = properties;
        }

        public Service(String name, String address) {
            this.name = name;
            this.address = address;
            this.properties = new HashMap<>();
        }

        public void putProp(String prop, Integer val) {
            properties.put(prop, val);
        }

        public int getProp(String prop) {
            return properties.get(prop);
        }

        public Service() {
            this.properties = new HashMap<>();
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
    }
}
