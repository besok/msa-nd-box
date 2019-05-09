package ie.home.msa.messages;

import java.io.Serializable;
import java.util.List;

public class LogServiceMessage extends ServiceMessage<LogServiceMessage.Logs> {

    public static class Logs implements Serializable {
        private List<String> logList;

        public Logs(List<String> logList) {
            this.logList = logList;
        }

        public Logs() {
        }

        public List<String> getLogList() {
            return logList;
        }

        public void setLogList(List<String> logList) {
            this.logList = logList;
        }

        public static Logs of(List<String> logList){
            return new Logs(logList);
        }
    }
}
