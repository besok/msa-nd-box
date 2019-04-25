package ie.home.msa.sandbox.saga;

import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Service
public class ChapterInvoker {
    private Map<String,ChapterWrapper> chapters;

    public ChapterInvoker() {
        this.chapters = new HashMap<>();
    }

    public void put(String title, Object bean, Method process,Method rollback){
        chapters.put(title,new ChapterWrapper(title,bean,process,rollback));
    }

    public ChapterWrapper invoke(String title){
        return chapters.get(title);
    }

    public static class ChapterWrapper{
        private String title;
        private Object bean;
        private Method processMethod;
        private Method rollbackMethod;


        public ChapterWrapper() {
        }

        public ChapterWrapper(String title, Object bean, Method processMethod, Method rollbackMethod) {
            this.title = title;
            this.bean = bean;
            this.processMethod = processMethod;
            this.rollbackMethod = rollbackMethod;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Object getBean() {
            return bean;
        }

        public void setBean(Object bean) {
            this.bean = bean;
        }

        public Method getProcessMethod() {
            return processMethod;
        }

        public void setProcessMethod(Method processMethod) {
            this.processMethod = processMethod;
        }

        public Method getRollbackMethod() {
            return rollbackMethod;
        }

        public void setRollbackMethod(Method rollbackMethod) {
            this.rollbackMethod = rollbackMethod;
        }
    }

}
