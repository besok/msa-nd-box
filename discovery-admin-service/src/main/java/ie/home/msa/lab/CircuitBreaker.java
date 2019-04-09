package ie.home.msa.lab;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CircuitBreaker {
    /**
     * seconds
     */
    int value() default 0;
}
