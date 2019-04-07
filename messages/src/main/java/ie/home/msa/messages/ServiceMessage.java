package ie.home.msa.messages;


import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class ServiceMessage extends Message<ServiceMessage.Metrics> {


    public ServiceMessage() {
        setData( new Metrics());
    }

    public ServiceMessage(Metrics metrics) {
        setData(metrics);
    }
    public ServiceMessage(Metric metric) {
        this();
        getData().add(metric);
    }
    public ServiceMessage(Map<String,Integer> metrics) {
        this(Metrics.from(metrics));
    }



    public static class Metrics implements Serializable {
        private Set<Metric> metrics;

        public Metrics() {
            metrics = new HashSet<>();
        }

        public Metrics(Set<Metric> metrics) {
            this.metrics = metrics;
        }

        public Set<Metric> getMetrics() {
            return metrics;
        }

        public void setMetrics(Set<Metric> metrics) {
            this.metrics = metrics;
        }
        public void add(Metric metric){
            metrics.add(metric);
        }
        public static Metrics from(Metric metric){
            Set<Metric> metricsList = new HashSet<>();
            metricsList.add(metric);
            return new Metrics(metricsList);
        }
        public static Metrics from(Set<Metric> metrics){
            return new Metrics(metrics);
        }
        public static Metrics from(Map<String,Integer> metricsMap){
            Set<Metric> metrics = metricsMap.entrySet().stream()
                    .map((k) -> new Metric(k.getKey(), k.getValue()))
                    .collect(Collectors.toSet());
            return new Metrics(metrics);
        }
    }

    public static class Metric implements Serializable{
        private String name;
        private int value;

        public Metric() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public Metric(String name, int value) {
            this.name = name;
            this.value = value;
        }
    }
}
