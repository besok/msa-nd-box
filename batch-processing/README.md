#### intro:
The basic idea is to get a resource pool of workers and spread task between them. 
This version of local resource manager and workers

```
                                    global resource manager
                                               /
                                              /  
                                             /   
                                 local resource manager ...
                                    /
                                   /
                                  /
                              worker ...     
```

- initiate a pool of workers.
- initiate a queue of task/subtask
- if a worker is free give a new task to him from the queue
- if there are not  any tasks in queue terminate free workers
- collect results

---

#### notes:
 - workerInitializer start a new worker by invoking a jar in a new thread.
 - workerDestroyer close and terminate a worker by sending a http request to close point
 - every worker has a new metric indicating this worker is busy or not
  - if there are no tasks in the task queue and the worker is free then terminate it
 - resource manager service is a mediator(it is a service discovery admin for workers and it has common service discovery)  
 - it uses a common interface of task, which has possibility to split itself and accumulate a result