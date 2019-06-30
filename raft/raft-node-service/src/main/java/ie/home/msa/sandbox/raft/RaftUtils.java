package ie.home.msa.sandbox.raft;

import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.function.Function.*;
import static java.util.stream.Collectors.*;

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
                .orElseThrow(RuntimeException::new);
    }


    static int quorumSize(int sizeEnsemble) {
        return sizeEnsemble / 2 + 1;
    }

    static boolean isQ(int currentSize, int qs) {
        return currentSize >= qs;
    }

    static Optional<Integer> findMaxInQs(int[] idxes) {
        int countdown = idxes.length;
        int qs = quorumSize(idxes.length);
        int mxFilter = Integer.MAX_VALUE;
        while (countdown > 0) {
            Map<Integer, Long> countMap = distinctAndCount(idxes);
            Map.Entry<Integer, Long> mx = findMx(countMap, mxFilter).get();
            int v = Math.toIntExact(mx.getValue());
            if (isQ(v, qs) || isQ(v + moreThen(countMap, mx), qs)) {
                return Optional.of(mx.getKey());
            } else {
                mxFilter = mx.getKey();
                countdown--;
            }
        }
        return Optional.empty();
    }

    private static Optional<Map.Entry<Integer, Long>> findMx(Map<Integer, Long> countNum, int finalMxFilter) {
        return countNum.entrySet().stream().filter(e -> e.getKey() < finalMxFilter)
                .max((l, r) -> {
                    if (l.getValue() - r.getValue() == 0) {
                        return l.getKey() - r.getKey();
                    }
                    return Math.toIntExact(l.getValue() - r.getValue());
                });
    }

    private static int moreThen(Map<Integer, Long> countNum, Map.Entry<Integer, Long> mx) {
        return Math.toIntExact(countNum.keySet().stream().filter(e -> e > mx.getKey()).count());
    }

    private static Map<Integer, Long> distinctAndCount(int[] idxes) {
        return IntStream.of(idxes).boxed().collect(groupingBy(identity(), counting()));
    }

}
