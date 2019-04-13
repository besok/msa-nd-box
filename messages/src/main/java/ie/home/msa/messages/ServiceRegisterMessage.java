package ie.home.msa.messages;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ServiceRegisterMessage extends ServiceMessage<ServiceRegisterMessage.Properties> {


    public static class Properties implements Serializable {
        private Map<String, String> properties;

        public Map<String, String> getProperties() {
            return properties;
        }

        public void setProperties(Map<String, String> properties) {
            this.properties = properties;
        }

        public void putProperty(String prop, String val) {
            properties.put(prop, val);
        }

        public String getProperty(String prop) {
            return properties.get(prop);
        }

        public Properties() {
            this.properties = new HashMap<>();
        }

    }
}
