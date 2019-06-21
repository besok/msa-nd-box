#### intro:
The communication process between microservices is the most important thing.
That is why the messages should be standardized and being involved in the hierarchy

#### notes:
Root message is:
```java
public abstract class Message<E extends Enum<E>,T extends Serializable> implements Serializable {
    private int version;
    private String dsc;
    private T body;
    private E status;
    private Service service;
}
```
E - status defining state of message
T - entity or body of message
version - count of resending
dsc - description
service - a pair from name and address defining a sender

After that can composite some hierarchy:
```java
class Message<E,T >{}
class ServiceMessage<T> extends Message<ServiceStatus, T>{} //  enum ServiceStatus {READY,FAILED,UNDEFINED,RELOAD}
class GetServiceMessage extends ServiceMessage<Service> {} // class Service {String name;String address;}
```
