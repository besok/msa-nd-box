package ie.home.msa.zab;

import java.io.Serializable;

public class Zid implements Serializable,Comparable<Zid> {
    private int epoch;
    private int counter;

    public Zid() {
        epoch = 0;
        counter = 0;
    }

    public Zid(int epoch, int counter) {
        this.epoch = checkNegative(epoch);
        this.counter = checkNegative(counter);
    }

    public int getEpoch() {
        return epoch;
    }

    public void setEpoch(int epoch) {
        this.epoch = checkNegative(epoch);
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = checkNegative(counter);
    }

    public void incEpoch(){
        epoch++;
    }
    public void incCounter(){
        counter++;
    }


    @Override
    public String toString() {
        return "Zid{" +
                "epoch=" + epoch +
                ", counter=" + counter +
                '}';
    }

    @Override
    public int compareTo(Zid o) {
        int nextEpox = o.getEpoch();
        int diffEpox = epoch - nextEpox;
        if(diffEpox !=0){
            return diffEpox;
        }
        int nextCounter = o.getCounter();
        return counter - nextCounter;
    }
    private int checkNegative(int el){
        if(el < 0) {
            throw new IllegalArgumentException("must be positive ");
        }
        return el;
    }
}
