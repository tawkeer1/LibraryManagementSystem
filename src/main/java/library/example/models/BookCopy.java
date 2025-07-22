package library.example.models;

import java.io.Serial;
import java.io.Serializable;

public class BookCopy implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Object lock = new Object();
    private final int copyId;
    private String title;
    private String authorName;
    private String genre;
    private int pages;
    private boolean taken;

    public BookCopy(int copyId, String bookName, String authorName, String genre, int pages, boolean taken) {
        this.copyId = copyId;
        this.title = bookName;
        this.authorName = authorName;
        this.genre = genre;
        this.pages = pages;
        this.taken = taken;
    }

    public Object getLock() {
        return lock;
    }

    public int getCopyId() {
        return copyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String name) {
        this.authorName = name;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public void toggleTaken() {
        this.taken = !this.taken;
    }

    public synchronized void setTaken(boolean taken) {
        this.taken = taken;
    }

    public synchronized boolean isTaken() {
        return this.taken;
    }

    @Override
    public String toString() {
        return "BookCopy{" +
                "copyId=" + copyId +
                ", bookName='" + title + '\'' +
                ", authorName='" + authorName + '\'' +
                ", genre='" + genre + '\'' +
                ", pages=" + pages +
                ", taken=" + taken +
                '}';
    }
}
