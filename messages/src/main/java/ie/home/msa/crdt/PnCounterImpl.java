package ie.home.msa.crdt;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static java.lang.Math.max;
import static java.lang.Math.toIntExact;


public class PnCounterImpl implements PnCounter {
    private final int idx;
    private long[] incArr;
    private long[] decArr;

    public synchronized long value() {
        long inc = LongStream.of(incArr).sum();
        long dec = LongStream.of(decArr).sum();
        return inc - dec;
    }

    public PnCounterImpl(int nodes, int idx) {
        if (nodes < 1 || idx < 0 || idx > nodes) {
            throw new CRDTException();
        }
        this.incArr = new long[nodes];
        this.decArr = new long[nodes];
        this.idx = idx;
    }

    @Override
    public void update(Effector effector) {
        effector.effect(incArr, decArr);
    }

    @Override
    public synchronized Effector generate(Op op) {
        switch (op) {
            case INCREMENT: {
                incArr[idx]++;
                return new Effector(Op.INCREMENT, idx);
            }
            case DECREMENT: {
                decArr[idx]++;
                return new Effector(Op.DECREMENT, idx);
            }
            default:
                return null;
        }
    }

    @Override
    public synchronized void merge(State state) {
        this.incArr = mergeMax(this.incArr, state.getIncArr());
        this.decArr = mergeMax(this.decArr, state.getDecArr());
    }

    @Override
    public State state() {
        return new State(incArr,decArr);
    }

    private long[] mergeMax(long[] left, long[] right) {
        return LongStream
                .range(0, left.length)
                .map(i -> max(left[toIntExact(i)], right[toIntExact(i)]))
                .toArray();
    }

    @Override
    public String toString() {
        return "PnCounterImpl{" +
                "idx=" + idx +
                ", incArr=" + Arrays.toString(incArr) +
                ", decArr=" + Arrays.toString(decArr) +
                '}';
    }
}
