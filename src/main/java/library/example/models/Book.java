package library.example.models;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Book implements Serializable {
    private static final long serialVersionUID = 1L;
    private final transient Object lock = new Object();
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

    public Object getLock() {
        return lock;
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
    public List<BookCopy> getCopies(){
        return copies;
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
        return "\n------------------------" +
                "\nTitle       : " + title +
                "\nAuthor      : " + authorName +
                "\nGenre       : " + genre +
                "\nPages       : " + pages +
                "\nTotal Copies: " + copies.size() +
                "\nAvailable   : " + availableCopies() +
                "\n------------------------";
    }

}
