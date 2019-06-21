#### intro:
The Saga pattern is an architectural pattern which provides an alternative approach to big and long running ACID transactions.\
It takes a business process and breaks it up into small isolated steps – each of them with its own transaction.\
The overall consistency is provided by manually reverting past actions.

there are 2 types of saga:
- event-based(distributed). Every microservice decides to push the message further
- orchestrator based(centralized). There is only one service being an orchestrator decides to push a message further

- refs: [whitepaper](https://www.cs.cornell.edu/andru/cs711/2002fa/reading/sagas.pdf)

#### notes:
There are three services involved to saga(creator-service,logger-service,validator-service).

General funcitonality is provided by the packages: 
- [logic](..\discovery\src\main\java\ie\home\msa\sandbox\saga)
- [messages](..\messages\src\main\java\ie\home\msa\saga)

The saga message involves all appropriate information for orchestration:
```
    Saga saga = new Saga()
                .addChapter(new Chapter("create", "creator-service", Status.READY, "test-data"))
                .addChapter(new Chapter("validate", "validator-service", Status.READY, "test-data"))
                .addChapter(new Chapter("log", "logger-service", Status.READY, "test-data"))
                .addChapter(new Chapter("diff_creator", "creator-service", Status.READY, "test-data"))
                .addChapter(new Chapter("validate", "validator-service", Status.READY, "test-data"))
                .addChapter(new Chapter("log", "logger-service", Status.READY, "test-data"))
        ;
```

Service example:
```java

@Slf4j
@EnableSaga
@SagaChapter(title = "create")
public class Creator {

    @ChapterAction
    public String createEntity(String name){
      log.info(" entity with name created {} ",name);
      return "entity["+name+"]";
    }

    @ChapterRollback
    public String removeEntity(String name){
      log.info(" entity with name removed {} ",name);
      return "entity["+name+"]";
    }
    
}


```