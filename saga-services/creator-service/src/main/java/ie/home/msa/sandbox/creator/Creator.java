package ie.home.msa.sandbox.creator;

import ie.home.msa.sandbox.saga.Process;
import ie.home.msa.sandbox.saga.Rollback;
import ie.home.msa.sandbox.saga.SagaChapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SagaChapter(title = "create")
public class Creator {

    @Process
    public String createEntity(String name){
      log.info(" entity with name created {} ",name);
      return "entity["+name+"]";
    }

    @Rollback
    public String removeEntity(String name){
      log.info(" entity with name removed {} ",name);
      return "entity["+name+"]";
    }



}
