package ie.home.msa.messages;


public class TaskMessageBuilder {

    public static FileCountTaskMessage fileCountTaskMessage(String directory,String service, String address, String dsc) {
       FileCountTaskMessage message = new FileCountTaskMessage();
       message.setDsc(dsc);
       message.setService(Service.of(service,address));
       message.setStatus(TaskStatus.READY);
       message.setVersion(0);
       FileCountTask task = new FileCountTask(directory);
       message.setBody(task);
       return message;
    }



}
