package ie.home.msa.crdt;

import java.io.Serializable;
import java.util.Arrays;

/**
 * simple pn counter - crdt - conflict free replication data
 * it consists of 2 vectors for inc and dec. vector is array with node length.
 */
public interface PnCounter {

    /**
     * operation ++ or --
     *
     * @param op type of operation @see {@link Op}
     * @return effector : class action for update ther nodes @see {@link Effector}
     */
    Effector generate(Op op);

    /**
     * update from other node
     *
     * @param effector class action for update
     */
    void update(Effector effector);

    /**
     * sync nodes
     */
    void merge(State state);
    /**
     * return state
     */
    State state();

    long value();


    class Effector implements Serializable {
        private Op op;
        private int idx;

        public void effect(long[] incArr, long[] decArr) {
            switch (op) {
                case DECREMENT:
                    decArr[idx]++;
                    break;
                case INCREMENT:
                    incArr[idx]++;
                    break;
            }
        }

        public Effector() {
        }

        public Effector(Op op, int idx) {
            this.op = op;
            this.idx = idx;
        }

        public Op getOp() {
            return op;
        }

        public void setOp(Op op) {
            this.op = op;
        }

        public long getIdx() {
            return idx;
        }

        public void setIdx(int idx) {
            this.idx = idx;
        }

    }

    class State implements Serializable {
        private long[] incArr;
        private long[] decArr;

        public State(long[] incArr, long[] decArr) {
            this.incArr = incArr;
            this.decArr = decArr;
        }

        public void setIncArr(long[] incArr) {
            this.incArr = incArr;
        }

        public void setDecArr(long[] decArr) {
            this.decArr = decArr;
        }

        public long[] getIncArr() {
            return incArr;
        }

        public long[] getDecArr() {
            return decArr;
        }

        @Override
        public String toString() {
            return "State{" +
                    "incArr=" + Arrays.toString(incArr) +
                    ", decArr=" + Arrays.toString(decArr) +
                    '}';
        }
    }

    enum Op {
        INCREMENT, DECREMENT;
    }
}
