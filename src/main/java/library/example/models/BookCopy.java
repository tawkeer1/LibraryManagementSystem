package library.example.models;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class BookCopy {
    private final String copyId;
    private String title;
    private String authorName;
    private String genre;
    private int pages;
    private boolean taken;

    public BookCopy(String copyId, String bookName, String authorName, String genre, int pages, boolean taken) {
        this.copyId = copyId;
        this.title = bookName;
        this.authorName = authorName;
        this.genre = genre;
        this.pages = pages;
        this.taken = taken;
    }

    public String getCopyId() {
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BookCopy that = (BookCopy) obj;
        return this.copyId == that.copyId; // or any unique identifier
    }

    @Override
    public int hashCode() {
        return Objects.hash(copyId); // or same field used in equals
    }

}
