package ie.home.msa.messages;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileCountTask implements Task<Long> {

    private String dirOrFile;
    private Long result;


    public FileCountTask(String directory) {
        this.dirOrFile = directory;
        this.result = 0L;
    }

    public String getDirOrFile() {
        return dirOrFile;
    }

    public void setDirOrFile(String dirOrFile) {
        this.dirOrFile = dirOrFile;
    }

    @Override
    public Long getResult() {
        return result;
    }

    public void setResult(Long result) {
        this.result = result;
    }

    @Override
    public List<Task<Long>> split() {
        Path path = Paths.get(dirOrFile);
        if (isDirectory(path)) {
            try( Stream<Path> list = Files.list(path)) {
                return list
                        .map(Path::toAbsolutePath)
                        .map( Path::toString)
                        .map(FileCountTask::new)
                        .collect(Collectors.toList());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>(Collections.singletonList(this));
    }

    private boolean isDirectory(Path path) {
        return path.toFile().isDirectory();
    }

    @Override
    public Task<Long> process() {
        Path path = Paths.get(dirOrFile);
        if(isDirectory(path)){
            return null;
        }
        try(Stream<String> lines = Files.lines(path)) {
            this.setResult(lines.mapToLong(String::length).sum());
            return this;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public synchronized boolean accumulate(Long data) {
        this.result += data;
        return true;
    }
}
