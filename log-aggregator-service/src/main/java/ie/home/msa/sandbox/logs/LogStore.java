package ie.home.msa.sandbox.logs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


@Service
@Slf4j
public class LogStore {

    private Path logDirectory;

    public LogStore() {
        this.logDirectory = logDirectory();
    }



    public List<String> getLastByAddress(String service, String address) {
        return findLogDir(service, address)
                .flatMap(this::findMaxFolder)
                .map(this::findLinesFromFile)
                .orElseGet(ArrayList::new);
    }
    public List<String> getAllByAddress(String service, String address) {
        return findLogDir(service, address)
                .map(this::findLinesFromAllFiles)
                .orElseGet(ArrayList::new);
    }

    private List<String> findLinesFromAllFiles(Path p){
        try(Stream<Path> files = Files.list(p)){
            return files.flatMap(this::findLinesFromFileToStream).collect(Collectors.toList());
        }
        catch (Exception ignored){}

        return new ArrayList<>();
    }

    private List<String> findLinesFromFile(Path p) {
        try {
            return Files.readAllLines(p);
        } catch (IOException ignored) {}
        return new ArrayList<>();
    }
    private Stream<String> findLinesFromFileToStream(Path p) {
        return findLinesFromFile(p).stream();
    }

    private Optional<Path> findMaxFolder(Path p) {
        try (Stream<Path> list = Files.list(p)) {
            return list.max(Comparator.comparing(inP -> Long.parseLong(inP.getFileName().toString())));
        } catch (Exception ignored) {}
        return Optional.empty();
    }

    private Optional<Path> findLogDir(String service, String address) {
        Path serviceDir = logDirectory.resolve(service);
        if (Files.notExists(serviceDir)) {
            return Optional.empty();
        }

        Path index = serviceDir.resolve("index");
        try {
            List<String> addrList = Files.readAllLines(index);
            for (int i = 0; i < addrList.size(); i++) {
                if (addrList.get(i).equals(address)) {
                    return Optional.of(serviceDir.resolve(String.valueOf(i)));
                }
            }
        } catch (IOException e) {
            log.info("exception ", e);
        }
        return Optional.empty();
    }

    public void setLogs(String service, String address, List<String> logs) {
        try {
            setNewLogs(getAddressLogDir(getServiceLogDir(service), address), logs);
        } catch (IOException e) {
            log.error(" exception ", e);
        }
    }


    private Path logDirectory() {
        logDirectory = Paths.get(new ClassPathResource("service_log_storage").getPath());
        if (Files.notExists(logDirectory)) {
            try {
                Files.createDirectory(logDirectory);
            } catch (IOException e) {
                log.error(" exception ", e);
            }
        }
        return logDirectory;
    }

    private void setNewLogs(Path dir, List<String> logs) throws IOException {
        long id = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        Path newLogFile = dir.resolve(String.valueOf(id));
        Files.deleteIfExists(newLogFile);
        Files.createFile(newLogFile);
        Files.write(newLogFile, logs);
    }

    private Path getServiceLogDir(String service) throws IOException {
        Path serviceDir = logDirectory.resolve(service);
        if (Files.notExists(serviceDir)) {
            Files.createDirectory(serviceDir);
            Files.createFile(serviceDir.resolve("index"));
            log.info(" directory created {}", service);
        }
        return serviceDir;
    }

    private Path getAddressLogDir(Path serviceDir, String address) throws IOException {
        Path index = serviceDir.resolve("index");
        List<String> addrList = Files.readAllLines(index);
        int size = addrList.size();
        for (int i = 0; i < size; i++) {
            if (addrList.get(i).equals(address)) {
                log.info(" directory founded {}", address);
                return serviceDir.resolve(String.valueOf(i));
            }
        }
        addrList.add(address);
        rewriteIndex(index, addrList);

        return Files.createDirectory(serviceDir.resolve(String.valueOf(size)));
    }

    private void rewriteIndex(Path index, List<String> addrList) throws IOException {
        Files.deleteIfExists(index);
        Files.createFile(index);
        Files.write(index, addrList);
    }

}
