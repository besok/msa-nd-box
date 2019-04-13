package ie.home.msa.messages;

public class MessageBuilder {

    public static ServiceRegisterMessage registerMessage(String service,String address,String dsc){
        ServiceRegisterMessage mes = new ServiceRegisterMessage();
        mes.setStatus(ServiceStatus.READY);
        mes.setVersion(0);
        mes.setBody(new ServiceRegisterMessage.Properties());
        mes.setService(Service.of(service,address));
        mes.setDsc(dsc);
        return mes;
    }
    public static ServiceMetricsMessage metricsMessage(String service,String address,
            ServiceMetricsMessage.Metrics metrics,int version,ServiceStatus serviceStatus){
        ServiceMetricsMessage metricsMessage = new ServiceMetricsMessage();
        metricsMessage.setVersion(version);
        metricsMessage.setBody(metrics);
        metricsMessage.setStatus(serviceStatus);
        metricsMessage.setService(Service.of(service,address));
        return metricsMessage;
    }

    public static GetServiceMessage serviceMessage(String service,String address, ServiceStatus status){
        GetServiceMessage message = new GetServiceMessage();
        message.setVersion(1);
        message.setStatus(status);
        message.setBody(new ServiceRegisterMessage.Properties());
        message.setService(Service.of(service,address));
        return message;
    }

}
