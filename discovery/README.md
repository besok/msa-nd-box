#### intro:
it is a major library to implement most of the patterns there. 
Most of all patterns provide common principle to communicate **client | worker | whoever** with **server | admin |  whoever**

---

#### service-discovery
every service while starting to send a registration request to admin service. Also server send his properties to config interacting.
It can be useful if there is more than one instance and it passes through a load balanced strategy
Then whoever wanting to interact with the microservice should ask admin-service to give the address
- [DiscoveryClient#registration](src/main/java/ie/home/msa/sandbox/discovery/client/DiscoveryClient.java)
- common approach on admin side it is to process all config properties through [Service registrator](src/main/java/ie/home/msa/sandbox/discovery/server/ServiceRegistrator.java)
  - and more precisely is [ServiceRegistrationHandler](src/main/java/ie/home/msa/sandbox/discovery/server/ServiceRegistrationHandler.java) 
  
#### persistence-storage
admin service needs to save all coming information to persistent storage. For that, persistent file storage has been added.
the common principle of this storage is classic key-value storage wherein a key is a file and values are lines of the file.
- class [AbstractFileStorage](src/main/java/ie/home/msa/sandbox/discovery/server/AbstractFileStorage.java) provides tha logic
  - it is thread safe(i presume :) )
  - to inherit needs to implement 3 methods:
         - transform values to strings(to save to a text file line by line)
         - transform strings to values(to get them from a file)
         - equal entity method(to find special val)

basic impl:         
```java
public class PlainValueFileStorage extends AbstractFileStorage<String> {
    public PlainValueFileStorage(String directory, StorageListenerHandler handler) {
        super(directory, handler);
    }

    @Override
    protected List<String> fromFile(List<String> params) {
        return params;
    }

    @Override
    protected List<String> toFile(List<String> params) {
        return params;
    }

    @Override
    protected boolean equal(String left, String right) {
        return left.equals(right);
    }
}

```
- also, this storage provides a concept of storage listeners. 
  - In common case, every action with storage(PUT, GET, REMOVE_KEY, REMOVE_VAL, INIT, CLEAN)\
kicks off a listener handler which notifies all listeners(actually invokes all listeners, because the listener is a lambda only)
    
  
example:
```java
@Service
@Slf4j
public class LoadBalanceStorageListener implements StorageListener {
    private LoadBalanceResolver resolver;

    public LoadBalanceStorageListener(LoadBalanceResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public <T> void onEvent(Event event, String storage, String key, T val) {
        if (storage.equals(LOAD_BALANCER.getName())) {
            if (event == Event.PUT) {
                resolver.addService(key);
            } else if (event == Event.REMOVE_KEY) {
                resolver.removeService(key);
            }
        }
    }
}
```  

#### load-balancer  
when there is more than one service instance it is a common approach to have a strategy to return some a service instance.
In that case it can be set while initialization by a parameter load-balance-strategy to ROBIN or RANDOM
- RANDOM is random
- ROBIN is round-robin

#### health-metrics
the admin service needs to ensure all stored services are alive and have a good condition/state otherwise to do something.
the basic approach to do that is a health check process:
 - periodically the admin service asks all services by sending a message to certain service api
 - every service returns metric message
 - based on this message the admin service decides what to do next

- on admin side it is provided by [MetricHandler](src/main/java/ie/home/msa/sandbox/discovery/server/MetricHandler.java)
- on client side it is provided by [HMetrics](src/main/java/ie/home/msa/sandbox/discovery/server/MetricHandler.java)

the same approach suits close and init operations. Every server has endpoint to invoke init operations and destroy operations. When the endpoint is invoked all operations have being running.
- on client side it is provided by implementing [InitializationOperation](src/main/java/ie/home/msa/sandbox/discovery/client/InitializationOperation.java) and [DestroyOperation](src/main/java/ie/home/msa/sandbox/discovery/client/DestroyOperation.java)
- internally it is provided by [ApplicationRestarter](src/main/java/ie/home/msa/sandbox/discovery/client/ApplicationRestarter.java)

#### circuit-breaker
The basic idea for a circuit breaker is to be a proxy between a service and its invoker.\
If the service has some degradation in performance it should be disabled or terminated or restarted or whatever.

It can be achieved by measuring performance every invoke(in that library at least)
- on the client side, it is provided by finding all methods marked @CircuitBreaker and proxying them by method measuring the time of executing
- if the time more threshold the admin server should react
```java
@Service
public class ImportantService {
    @CircuitBreaker(2) // 2 sec
    public int importantOperation(){
        // implementation
    }
 
}
```
- the implementation on the client side by:
   - [CircuitBreakerBeanPostProcessor](src/main/java/ie/home/msa/sandbox/discovery/client/CircuitBreakerBeanPostProcessor.java),
   - [CircuitBreakerHealth](src/main/java/ie/home/msa/sandbox/discovery/client/CircuitBreakerHealth.java)
   - [CircuitBreakerMethodStore](src/main/java/ie/home/msa/sandbox/discovery/client/CircuitBreakerMethodStore.java)

- admin side provides a new storage for that.