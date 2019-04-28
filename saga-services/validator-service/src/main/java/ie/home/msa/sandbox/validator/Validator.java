package ie.home.msa.sandbox.validator;

import ie.home.msa.sandbox.saga.ChapterAction;
import ie.home.msa.sandbox.saga.ChapterRollback;
import ie.home.msa.sandbox.saga.SagaChapter;
import lombok.extern.slf4j.Slf4j;

@SagaChapter(title = "validate")
@Slf4j
public class Validator {

    @ChapterAction
    public boolean validate(String validateName) {
        log.info("validation for {} is successful",validateName);
        return true;
    }

    @ChapterRollback
    public void validateRollback(String validateName){
        log.info("validation rollback for {} is not necessary", validateName);
    }
}
