package ie.home.msa.crdt;

public interface PnCounter {
    void update(Effector effector);
    Effector generate(Op op);
    void merge(long[] incArr, long[] decArr);
    long value();

    interface Effector {
        void effect(long[] incArr,long[] decArr);
    }


    static Effector incEffector(int idx) {
        return (iA,dA) -> iA[idx]++;
    }
    static Effector decEffector(int idx) {
        return (iA,dA) -> dA[idx]--;
    }

    enum Op {
        INCREMENT, DECREMENT;
    }
}
