package ie.home.msa.lab.batch;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ServiceInitializer {


    @PostConstruct
    public void init() throws IOException, InterruptedException {
        List<Callable<Integer>> collect = IntStream.range(0, 1).mapToObj(this::run).collect(Collectors.toList());
        Executors.newFixedThreadPool(1).invokeAll(collect);

    }

    private Callable<Integer> run(int el) {
        return () -> {
            String url = "C:\\projects\\msa-nd-box\\greeting-service\\target\\greeting-service-1.0.jar";
            ProcessBuilder pb = new ProcessBuilder("java", "-server", "-jar", url);
            Process p = null;
            try {
                p = pb.start();
                p.getOutputStream().close();
                BufferedReader is = new BufferedReader(new InputStreamReader(p.getInputStream()));
                int i =0;
                boolean f = true;
                String line;
                while ((line = is.readLine()) != null) {
                    System.out.println(el + " >> " + line);
                    i++;
                    if(i > 39 && f){
                        p.destroy();
                        f = false;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0;
        };
    }

}
