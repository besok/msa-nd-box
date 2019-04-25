package ie.home.msa.saga;


import java.io.Serializable;

public class Chapter implements Serializable {
    private String title;
    private String service;
    private Status status;

    private Object inputData;
    private Object outputData;

    public Object getOutputData() {
        return outputData;
    }

    public void setOutputData(Object outputData) {
        this.outputData = outputData;
    }

    public Chapter() {
    }

    public Chapter(String title, String service, Status status, Object inputData) {
        this.title = title;
        this.service = service;
        this.status = status;
        this.inputData = inputData;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Object getInputData() {
        return inputData;
    }

    public void setInputData(Object inputData) {
        this.inputData = inputData;
    }

    @Override
    public String toString() {
        return "Chapter{" +
                "title='" + title + '\'' +
                ", service='" + service + '\'' +
                ", status=" + status +
                ", inputData=" + inputData +
                ", outputData=" + outputData +
                '}';
    }
}
