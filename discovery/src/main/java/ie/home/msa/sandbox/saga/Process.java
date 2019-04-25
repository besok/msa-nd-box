package ie.home.msa.sandbox.saga;

import org.springframework.stereotype.Service;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Process {}
