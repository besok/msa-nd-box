package ie.home.msa.crdt;

import java.io.Serializable;

public interface LWWRegister {
    String value();
    Checker assign(String value);
    void merge(Checker checker);

    class Checker implements Serializable {
        private long id;
        private String value;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public Checker(long id, String value) {
            this.id = id;
            this.value = value;
        }

        public boolean newWin(long localId){
            return id > localId;
        }

        @Override
        public String toString() {
            return "Checker{" +
                    "id=" + id +
                    ", value='" + value + '\'' +
                    '}';
        }
    }


}
