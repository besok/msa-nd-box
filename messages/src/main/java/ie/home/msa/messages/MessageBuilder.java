package ie.home.msa.messages;

import java.util.List;

public class MessageBuilder {

    public static ServiceRegisterMessage registerMessage(String service, String address, String dsc) {
        ServiceRegisterMessage mes = new ServiceRegisterMessage();
        mes.setStatus(ServiceStatus.READY);
        mes.setVersion(0);
        mes.setBody(new ServiceRegisterMessage.Properties());
        mes.setService(Service.of(service, address));
        mes.setDsc(dsc);
        return mes;
    }

    public static ServiceMetricsMessage metricsMessage(String service, String address,
                                                       ServiceMetricsMessage.Metrics metrics, int version, ServiceStatus serviceStatus) {
        ServiceMetricsMessage metricsMessage = new ServiceMetricsMessage();
        metricsMessage.setVersion(version);
        metricsMessage.setBody(metrics);
        metricsMessage.setStatus(serviceStatus);
        metricsMessage.setService(Service.of(service, address));
        return metricsMessage;
    }

    public static GetServiceMessage serviceMessage(String service, String address, ServiceStatus status) {
        GetServiceMessage message = new GetServiceMessage();
        message.setVersion(1);
        message.setStatus(status);
        message.setBody(Service.of(service, address));
        return message;
    }

    public static GetAllNodesServiceMessage nodesServiceMessage(String service, List<String> address, ServiceStatus status) {
        GetAllNodesServiceMessage message = new GetAllNodesServiceMessage();
        message.setVersion(1);
        message.setStatus(status);
        message.setBody(address.stream().map(a -> Service.of(service, a)).toArray(Service[]::new));
        return message;
    }

}
