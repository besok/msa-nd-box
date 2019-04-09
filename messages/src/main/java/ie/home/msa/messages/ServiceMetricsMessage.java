package ie.home.msa.messages;

import com.sun.javafx.font.Metrics;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ServiceMetricsMessage extends ServiceMessage<ServiceMetricsMessage.Metrics> {
    public static class Metrics implements Serializable{
        private Map<String,Integer> metrics;

        public Metrics merge(Metrics metrics){
            Map<String, Integer> metricsMap = metrics.getMetrics();
            this.metrics.putAll(metricsMap);
            return this;
        }
        public Metrics(Map<String, Integer> metrics) {
            this.metrics = metrics;
        }

        public Metrics() {
        }

        public Map<String, Integer> getMetrics() {
            return metrics;
        }

        public void setMetrics(Map<String, Integer> metrics) {
            this.metrics = metrics;
        }

        public static Metrics single(String metric, Integer val){
            HashMap<String, Integer> metricsMap = new HashMap<>();
            metricsMap.put(metric,val);
            return new Metrics(metricsMap);
        }
    }

}
