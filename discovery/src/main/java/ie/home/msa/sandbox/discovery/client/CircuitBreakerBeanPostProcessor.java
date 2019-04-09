package ie.home.msa.lab;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.*;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Service
public class CircuitBreakerBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Method[] methods = bean.getClass().getDeclaredMethods();
        for (Method method : methods) {
            CircuitBreaker cbAnn = method.getAnnotation(CircuitBreaker.class);
            if (Objects.nonNull(cbAnn)) {
                CircuitBreakerMethodStore.put(beanName + "." + method.getName(), cbAnn.value());
            }
        }
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Method[] dcl = bean.getClass().getDeclaredMethods();
        for (Method method : dcl) {
            CircuitBreaker annotation = method.getAnnotation(CircuitBreaker.class);
            if (annotation != null) {
                Enhancer enhancer = new Enhancer();
                enhancer.setSuperclass(bean.getClass());
                enhancer.setCallback(methodTimeProcessMeter(beanName));
                return enhancer.create();
            }
        }
        return bean;
    }

    private MethodInterceptor methodTimeProcessMeter(String beanName) {
        return (obj, method1, args, proxy) -> {
            String key = beanName + "." + method1.getName();
            if (CircuitBreakerMethodStore.contains(key)) {
                LocalDateTime startTime = LocalDateTime.now();
                Object res = proxy.invokeSuper(obj, args);
                LocalDateTime finishTime = LocalDateTime.now();
                CircuitBreakerMethodStore.update(key, duration(startTime, finishTime));
                return res;
            } else {
                return proxy.invokeSuper(obj, args);
            }
        };
    }

    private int duration(LocalDateTime startTime, LocalDateTime finishTime) {
        return Math.toIntExact(ChronoUnit.SECONDS.between(startTime, finishTime));
    }


}
