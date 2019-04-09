package ie.home.msa.sandbox.discovery.client;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CircuitBreaker {
    /**
     * seconds
     */
    int value() default 0;
}
