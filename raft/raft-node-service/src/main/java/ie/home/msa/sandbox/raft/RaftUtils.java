package ie.home.msa.sandbox.raft;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class RaftUtils {

    static String[] filter(String address, String[] addresses) {
        return Stream.of(addresses)
                .filter(a -> !a.equals(address))
                .toArray(String[]::new);
    }

    static int find(String address, String[] adressList) {
        return IntStream
                .range(0, address.length())
                .filter(i -> adressList[i].equals(address))
                .findAny()
                .orElseThrow(RaftException::new);
    }


    static int quorumSize(int sizeEnsemble) {
        return sizeEnsemble / 2 + 1 ;
    }

    static boolean isQ(int currentSize, int qs) {
        return currentSize >= qs;
    }

}
