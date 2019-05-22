package ie.home.msa.lab.zab;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ZabUtils {

    static String[] filter(String address,String[] addresses){
        return Stream.of(addresses)
                .filter(a -> !a.equals(address))
                .toArray(String[]::new);
    }

    static int find(String address, String[] addresses) {
        return IntStream
                .range(0, address.length())
                .filter(i -> addresses[i].equals(address))
                .findAny()
                .orElseThrow(ElectionException::new);
    }

    static boolean isLeader(int incomeId, String address, String[] addresses) {
        if (incomeId >= addresses.length) {
            return false;
        }
        return addresses[incomeId].equals(address);
    }

    public static int threadSleep(int current, int threshold) {
        int nextTm = Math.min(current * 2, threshold);
        try {
            Thread.sleep(nextTm);
        } catch (InterruptedException ignored) {}
        return nextTm;
    }
}
