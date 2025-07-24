package library.example.models;
import library.example.services.BackupService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Book implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String title;
    private String authorName;
    private int pages;
    private String genre;
    //list of copies of this book. Have same name but diff. ids
    private List<BookCopy> copies ;

    public Book(String title, String authorName, String genre, int pages) {
        this.title = title;
        this.authorName = authorName;
        this.genre = genre;
        this.pages = pages;
        this.copies = new ArrayList<>();
    }

    public void addCopy(BookCopy copy) {
        copies.add(copy);
    }

    public synchronized BookCopy borrowAvailableCopy() {
        for (BookCopy copy : copies) {
            if (!copy.isTaken()) {
                copy.setTaken(true);
                return copy;
            }
        }
        return null; // no available copy
    }

    public synchronized void returnCopy(BookCopy copy) {
        copy.setTaken(false);
    }

    public int availableCopies() {
        int count = 0;
        for (BookCopy copy : copies) {
            if (!copy.isTaken()) count++;
        }
        return count;
    }
    //return list of all available copies of book
    public List<BookCopy> getCopies() {
        return copies;
    }

    public List<BookCopy> getMergedCopies() {
        // Create a fresh list with all in-memory copies first
        List<BookCopy> merged = new ArrayList<>(this.copies);

        // Load from disk
        List<BookCopy> fromDisk = BackupService.loadBookCopiesByTitle(this.title);

        // Avoid duplicates: add only copies not already present
        for (BookCopy copy : fromDisk) {
            boolean alreadyExists = merged.stream()
                    .anyMatch(c -> c.getCopyId().equals(copy.getCopyId()));
            if (!alreadyExists) {
                merged.add(copy);
            }
        }

        return merged;
    }


//    public int totalCopies() {
//        return copies.size();
//    }

    public void setTitle(String name){
        title = name;
    }

    public String getTitle(){
        return title;
    }

    public void setAuthorName(String name){
        authorName = name;
    }

    public String getAuthorName(){
        return authorName;
    }
    //how many pages book has
    public void SetPages(int pages){
        this.pages = pages;
    }
    public int getPages(){
        return pages;
    }
    public void setGenre(String bookGenre){
        genre = bookGenre;
    }

    public String getGenre(){
        return genre;
    }

    @Override
    public String toString() {
        List<BookCopy> merged = getMergedCopies();
        long availableCount = merged.stream().filter(copy -> !copy.isTaken()).count();

        return "\n------------------------" +
                "\nTitle       : " + title +
                "\nAuthor      : " + authorName +
                "\nGenre       : " + genre +
                "\nPages       : " + pages +
                "\nTotal Copies: " + merged.size() +
                "\nAvailable   : " + availableCount +
                "\n------------------------";
    }

}
