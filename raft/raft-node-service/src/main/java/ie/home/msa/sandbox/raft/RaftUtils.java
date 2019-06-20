package ie.home.msa.sandbox.raft;

import ie.home.msa.messages.Message;
import ie.home.msa.messages.ZElectionMessage;
import ie.home.msa.zab.ZNotification;
import ie.home.msa.zab.ZVote;

import java.util.Map;
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



    static int quorumSize(int sizeEnsemble){
        return sizeEnsemble % 2 == 0 ? sizeEnsemble / 2 + 1 : sizeEnsemble / 2;
    }

}
