package ie.home.msa.sandbox.creator;

import ie.home.msa.sandbox.saga.ChapterAction;
import ie.home.msa.sandbox.saga.ChapterRollback;
import ie.home.msa.sandbox.saga.SagaChapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
