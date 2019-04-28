package ie.home.msa.saga;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Saga implements Serializable {
    private List<Chapter> chapters;
    private int currentChapter;
    private Status status;

    @Override
    public String toString() {
        return "Saga{" +
                "chapters=" + Arrays.toString(chapters.toArray()) +
                ", currentChapter=" + currentChapter +
                ", status=" + status +
                '}';
    }

    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }

    public int getCurrentChapter() {
        return currentChapter;
    }

    public void setCurrentChapter(int currentChapter) {
        this.currentChapter = currentChapter;
    }

    public Saga() {
        status = Status.READY;
        chapters = new ArrayList<>();
        currentChapter = 0;
    }

    public Saga addChapter(Chapter chapter){
        chapters.add(chapter);
        return this;
    }

    public void updateCurrentChapter(Chapter chapter){
        if(currentChapter > -1 && currentChapter < size()){
            chapters.set(currentChapter,chapter);
        }
    }
    public List<Chapter> getChapters() {
        return chapters;
    }

    public Chapter currentChapter(){
        int count = chapters.size() - 1;
        if(currentChapter > count){
            return chapters.get(count);
        }
        return chapters.get(currentChapter);
    }

    public int inc(){
        return ++currentChapter;
    }
    public int dec(){
        return --currentChapter;
    }

    public int size(){
        return chapters.size();
    }
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }


}
