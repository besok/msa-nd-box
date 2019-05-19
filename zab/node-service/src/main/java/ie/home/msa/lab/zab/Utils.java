package ie.home.msa.lab.zab;

import java.util.stream.IntStream;

public class Utils {
    static int find(String address, String[]addresses ){
        return IntStream.range(0,address.length()).filter(i -> addresses[i].equals(address)).findAny()
                .orElseThrow(ElectionException::new);
    }
    static boolean isLeader(int incomeId,String address,String[] addresses){
        if(incomeId >= addresses.length){
            return false;
        }
        return addresses[incomeId].equals(address);
    }
}
