## Tiny implementation patterns for microservice architecture
This is a tiny implementation of basic patterns for microservice architecture.
It based on spring boot/web, java stack.
The major communication channel is an HTTP focused on async communication(the answer is returned immediately).
Common logic is provided by [discovery project](/discovery).

---

### [health + metrics](discovery/README.md#health-metrics)
the admin service needs to ensure all stored services are alive and have a good condition/state otherwise to do something.
- healthcheck / metrics
- close operations
- init operations

--- 

### [circuit breaker](discovery/README.md#circuit-breaker)    
The basic idea for a circuit breaker is to be a proxy between a service and its invoker.\
If the service has some degradation in performance it should be disabled or terminated or restarted or whatever.\

---

### [messaging](messages/README.md)
The communication process between microservices is the most important thing.\
That is why the messages should be standardized and being involved in the hierarchy.

- message hierarchy
- message builders 
---
### [service discovery](discovery/README.md#service-discovery)
every service while starting to send a registration request to admin service. Also server send his properties to config interacting.
It can be useful if there is more than one instance and it passes through a load balanced strategy   
- service registry
---   
### [persistent storage](discovery/README.md#persistence-storage)
admin service needs to save all coming information to persistent storage. For that, persistent file storage has been added.
the common principle of this storage is classic key-value storage wherein a key is a file and values are lines of the file.
  - storage listeners - reactive approach
---

### [load balancer](discovery/README.md#load-balancer)
when there is more than one service instance it is a common approach to have a strategy to return some a service instance.

---
### [crdt for active active](crdt-service/README.md)
The basic idea to use conflict-free replicated data types is a flavor of eventual consistency that ensures 
conflicts can be merged automatically to produce a value that is guaranteed to be correct/consistent.

- pncounter
  - event based
  - state based
- lwwregister
---

### [saga](saga-services/README.md)
The Saga pattern is an architectural pattern which provides an alternative approach to big and long running ACID transactions.\
It takes a business process and breaks it up into small isolated steps – each of them with its own transaction./
The overall consistency is provided by manually reverting past actions.
there are 2 types of saga:
- event based
- orchestrator based
---

### [batch processing](batch-processing/README.md) 
The basic idea is to get a resource pool of workers and spread task between them.
- workers/resource pool
- resource manager
- pull task from metric
- complex command (orchestrator / dsl)
---

### [logs aggregator](log-aggregator-service/README.md)
The basic idea is to collect all logs from all microservices to one place. 
This implementation does it through a file system.

---

### consensus protocols
Active-passive replication for leader/followers system
- [Zookeeper Atomic Broadcast](zab/README.md)
- [Raft consensus protocol](raft/README.md)

