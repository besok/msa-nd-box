package ie.home.msa.sandbox.creator;

import ie.home.msa.sandbox.saga.ChapterAction;
import ie.home.msa.sandbox.saga.ChapterRollback;
import ie.home.msa.sandbox.saga.SagaChapter;
import lombok.extern.slf4j.Slf4j;

@SagaChapter(title = "diff_creator")
@Slf4j
public class DifficultCreator {

    @ChapterAction
    public int length(String name){
        log.info(" entity with name created {} ",name);
        return name.length();
    }

    @ChapterRollback
    public String removeEntity(String name){
        log.info(" rollback empty . it does not need. ",name);
        return name;
    }

}
