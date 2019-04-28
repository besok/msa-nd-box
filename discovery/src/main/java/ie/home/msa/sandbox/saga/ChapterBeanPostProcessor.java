package ie.home.msa.sandbox.saga;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class ChapterBeanPostProcessor implements BeanPostProcessor {
    private final ChapterInvoker chapterInvoker;
    private Map<String, ChapterInvoker.ChapterWrapper> beanMap;

    public ChapterBeanPostProcessor(ChapterInvoker chapterInvoker) {
        this.chapterInvoker = chapterInvoker;
        this.beanMap = new HashMap<>();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> aClass = bean.getClass();
        if (aClass.isAnnotationPresent(SagaChapter.class)) {
            SagaChapter ann = aClass.getAnnotation(SagaChapter.class);
            String title = ann.title();
            Method process = null, rollback = null;
            Method[] declaredMethods = aClass.getDeclaredMethods();
            for (Method method : declaredMethods) {
                ChapterAction pr = method.getAnnotation(ChapterAction.class);
                ChapterRollback rlb = method.getAnnotation(ChapterRollback.class);
                if(Objects.nonNull(pr)){
                    process = method;
                }
                if(Objects.nonNull(rlb)){
                    rollback = method;
                }
            }
            beanMap.put(beanName,new ChapterInvoker.ChapterWrapper(title,bean,process,rollback));
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        ChapterInvoker.ChapterWrapper cw = beanMap.get(beanName);
        if(Objects.nonNull(cw)){
            chapterInvoker.put(cw.getTitle(),bean,cw.getProcessMethod(),cw.getRollbackMethod());
        }
        return bean;
    }
}
