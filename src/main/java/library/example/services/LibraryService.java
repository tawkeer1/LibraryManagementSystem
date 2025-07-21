package library.example.services;

import library.example.models.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class LibraryService {
    private final List<Book> physicalBooks;
    private final List<EBook> digitalBooks;
    private final Map<Integer, BookCopy> copyIdMap; // Fast lookup for BookCopy by ID

    public LibraryService() {
        this.physicalBooks = Collections.synchronizedList(new ArrayList<>());
        this.digitalBooks = Collections.synchronizedList(new ArrayList<>());
        this.copyIdMap = new ConcurrentHashMap<>();
    }

    //physical book methods
    public void addBook(Book book) {
        physicalBooks.add(book);
    }
    //all available physical books
    public List<Book> getAllBooks(){
        return physicalBooks;
    }
    //add copy of a given book
    public void addBookCopy(int bookIndex, BookCopy copy) {
        synchronized (physicalBooks) {
            Book book = physicalBooks.get(bookIndex);
            book.addCopy(copy);
            copyIdMap.put(copy.getCopyId(), copy);
        }
    }

    //search books by their title
    public List<Book> searchBooksByTitle(String title) {
        return physicalBooks.stream()
                .filter(book -> book.getTitle().equalsIgnoreCase(title))
                .collect(Collectors.toList());
    }

    //search books by author name
    public List<Book> searchBooksByAuthor(String author) {
        return physicalBooks.stream()
                .filter(book -> book.getAuthorName().equalsIgnoreCase(author))
                .collect(Collectors.toList());
    }

    //get all copies by title
    public Optional<BookCopy> getAvailableCopyByTitle(String title) {
        return physicalBooks.stream()
                .filter(book -> book.getTitle().equalsIgnoreCase(title))
                .flatMap(book -> book.getCopies().stream())
                .filter(copy -> !copy.isTaken())
                .findFirst();
    }

    public List<BookCopy> getAllAvailableCopies() {
        return physicalBooks.stream()
                .flatMap(book -> book.getCopies().stream())
                .filter(copy -> !copy.isTaken())
                .collect(Collectors.toList());
    }

    public void addEBook(EBook ebook) {
        digitalBooks.add(ebook);
    }
    //return all available ebooks books
    public List<EBook> getAllEBooks(){
        return digitalBooks;
    }

    public boolean removeEBook(String title) {
        return digitalBooks.removeIf(ebook -> ebook.getTitle().equalsIgnoreCase(title));
    }

    public Optional<EBook> getEBookByTitle(String title) {
        return digitalBooks.stream()
                .filter(ebook -> ebook.getTitle().equalsIgnoreCase(title))
                .findFirst();
    }

    public List<EBook> getAllAvailableEBooks() {
        return digitalBooks.stream()
                .filter(EBook::access)
                .collect(Collectors.toList());
    }


    public void printAllBooks() {
        physicalBooks.forEach(book -> {
            System.out.println(book + " (" +
                    book.getCopies().stream().filter(copy -> !copy.isTaken()).count() +
                    "/" + book.getCopies().size() + " available)");
        });
    }

    public void printAllEBooks() {
        digitalBooks.forEach(System.out::println);
    }

    public BookCopy getCopyById(int copyId) {
        return copyIdMap.get(copyId);
    }
}
