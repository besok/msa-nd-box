package ie.home.msa.messages;

public class MessageBuilder {

    public static ServiceRegisterMessage registerMessage(String service,String address,String dsc){
        ServiceRegisterMessage mes = new ServiceRegisterMessage();
        mes.setStatus(ServiceStatus.READY);
        mes.setVersion(0);
        mes.setBody(new ServiceRegisterMessage.Service(service,address));
        mes.setDsc(dsc);
        return mes;
    }
    public static ServiceMetricsMessage metricsMessage(ServiceMetricsMessage.Metrics metrics,int version,ServiceStatus serviceStatus){
        ServiceMetricsMessage metricsMessage = new ServiceMetricsMessage();
        metricsMessage.setVersion(version);
        metricsMessage.setBody(metrics);
        metricsMessage.setStatus(serviceStatus);
        return metricsMessage;
    }

    public static GetServiceMessage serviceMessage(String service,String address, ServiceStatus status){
        GetServiceMessage message = new GetServiceMessage();
        message.setVersion(1);
        message.setStatus(status);
        message.setBody(new ServiceRegisterMessage.Service(service,address));
        return message;
    }

}
