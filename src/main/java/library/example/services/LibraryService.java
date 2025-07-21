package library.example.services;

import library.example.models.*;
import library.example.utils.GenericAssetManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class LibraryService {
    private final List<User> users = Collections.synchronizedList(new ArrayList<>());
    private final GenericAssetManager<Book> bookManager = new GenericAssetManager<>();
    private final GenericAssetManager<EBook> ebookManager = new GenericAssetManager<>();
    private final Map<Integer, BookCopy> copyIdMap = new ConcurrentHashMap<>();

    // User management
    public void addUser(User user) {
        users.add(user);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    // Physical book methods
    public void addBook(Book book) {
        bookManager.add(book);
    }

    public List<Book> getAllBooks() {
        return bookManager.getAll();
    }

    public void addBookCopy(int bookIndex, BookCopy copy) {
        List<Book> books = bookManager.getAll();
        if (bookIndex >= 0 && bookIndex < books.size()) {
            Book book = books.get(bookIndex);
            book.addCopy(copy);
            copyIdMap.put(copy.getCopyId(), copy);
        }
    }

    public Optional<BookCopy> getAvailableCopyByTitle(String title) {
        return bookManager.getAll().stream()
                .filter(book -> book.getTitle().equalsIgnoreCase(title))
                .flatMap(book -> book.getCopies().stream())
                .filter(copy -> !copy.isTaken())
                .findFirst();
    }

    public List<BookCopy> getAllAvailableCopies() {
        return bookManager.getAll().stream()
                .flatMap(book -> book.getCopies().stream())
                .filter(copy -> !copy.isTaken())
                .collect(Collectors.toList());
    }

    public List<Book> searchBooksByTitle(String title) {
        return bookManager.getAll().stream()
                .filter(book -> book.getTitle().equalsIgnoreCase(title))
                .collect(Collectors.toList());
    }

    public List<Book> searchBooksByAuthor(String author) {
        return bookManager.getAll().stream()
                .filter(book -> book.getAuthorName().equalsIgnoreCase(author))
                .collect(Collectors.toList());
    }

    public BookCopy getCopyById(int copyId) {
        return copyIdMap.get(copyId);
    }

    public void printAllBooks() {
        bookManager.getAll().forEach(book -> {
            System.out.println(book + " (" +
                    book.getCopies().stream().filter(copy -> !copy.isTaken()).count() +
                    "/" + book.getCopies().size() + " available)");
        });
    }

    // EBook methods
    public void addEBook(EBook ebook) {
        ebookManager.add(ebook);
    }

    public List<EBook> getAllEBooks() {
        return ebookManager.getAll();
    }

    public boolean removeEBook(String title) {
        List<EBook> ebooks = ebookManager.getAll();
        for (EBook ebook : ebooks) {
            if (ebook.getTitle().equalsIgnoreCase(title)) {
                ebookManager.remove(ebook);
                return true;
            }
        }
        return false;
    }

    public Optional<EBook> getEBookByTitle(String title) {
        return ebookManager.getAll().stream()
                .filter(ebook -> ebook.getTitle().equalsIgnoreCase(title))
                .findFirst();
    }

    public List<EBook> getAllAvailableEBooks() {
        return ebookManager.getAll().stream()
                .filter(EBook::access)
                .collect(Collectors.toList());
    }

    public void printAllEBooks() {
        ebookManager.getAll().forEach(System.out::println);
    }
}
