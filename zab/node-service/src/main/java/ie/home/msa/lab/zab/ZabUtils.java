package ie.home.msa.lab.zab;

import ie.home.msa.messages.ElectionMessage;
import ie.home.msa.messages.Message;
import ie.home.msa.zab.ZNotification;
import ie.home.msa.zab.ZVote;

import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class ZabUtils {

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
                .orElseThrow(ElectionException::new);
    }

    static boolean isLeader(int incomeId, String address, String[] addresses) {
        return incomeId < addresses.length && addresses[incomeId].equals(address);
    }

    static boolean checkQuorum(ZVote vote, Map<Integer, ElectionMessage> map, int sizeEnsemble) {
        int qS = sizeEnsemble % 2 == 0 ? sizeEnsemble / 2 + 1 : sizeEnsemble / 2;
        long size = map.values().stream()
                .map(Message::getBody)
                .map(ZNotification::getVote)
                .filter(vote::equals)
                .count();
        return size >= qS;
    }
    static int setTimeout(int current, int threshold) {
        return Math.min(current * 2, threshold);
    }

    static int round(ElectionMessage message){
        return message.getBody().getRound();
    }
    static ZVote vote(ElectionMessage message){
        return message.getBody().getVote();
    }
    static int voteId(ElectionMessage message){
        return message.getBody().getVote().getId();
    }
    static int id(ElectionMessage message){
        return message.getBody().getId();
    }
}
