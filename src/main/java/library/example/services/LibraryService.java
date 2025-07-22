package library.example.services;

import library.example.models.*;
import library.example.utils.GenericAssetManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class LibraryService {
    private final List<User> users;
    private final GenericAssetManager<Book> bookManager;
    private final GenericAssetManager<EBook> ebookManager;
    private final Map<Integer, BookCopy> copyIdMap;

    public LibraryService() {
        // Load users from backup or start fresh
        this.users = Collections.synchronizedList(
                Optional.ofNullable(BackupService.loadUsers()).orElse(new ArrayList<>())
        );

        // Load books (physical) from backup or empty list
        List<Book> loadedBooks = Optional.ofNullable(BackupService.loadBooks()).orElse(new ArrayList<>());
        this.bookManager = new GenericAssetManager<>();
        loadedBooks.forEach(bookManager::add);

        // Create copy map from loaded books
        this.copyIdMap = new ConcurrentHashMap<>();
        for (Book book : loadedBooks) {
            for (BookCopy copy : book.getCopies()) {
                copyIdMap.put(copy.getCopyId(), copy);
            }
        }
        this.ebookManager = new GenericAssetManager<>();
    }

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

    public Optional<User> findUserByEmail(String email) {
        synchronized (users) {
            return users.stream()
                    .filter(user -> user.getEmail().equalsIgnoreCase(email))
                    .findFirst();
        }
    }

    public boolean deleteUserByEmail(String email) {
        synchronized (users) {
            return users.removeIf(user -> user.getEmail().equalsIgnoreCase(email));
        }
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

    // Backup triggers (can be used manually or by a background thread)
    public void backupToDisk() {
        BackupService.saveBooks(getAllBooks());
        BackupService.saveUsers(getAllUsers());
        System.out.println("[Backup] Data saved successfully.");
    }

    public void reloadUsersFromDisk() {
        List<User> loaded = BackupService.loadUsers();
        if (loaded != null) {
            users.clear();
            users.addAll(loaded);
            System.out.println("Users loaded successfully.");
        } else {
            System.out.println("No user data found or failed to load.");
        }
    }

    public void reloadBooksFromDisk() {
        List<Book> loadedBooks = BackupService.loadBooks();
        if (loadedBooks != null) {
            bookManager.clear();
            for (Book book : loadedBooks) {
                bookManager.add(book);
                for (BookCopy copy : book.getCopies()) {
                    copyIdMap.put(copy.getCopyId(), copy);
                }
            }
            System.out.println("Books loaded successfully.");
        } else {
            System.out.println("No book data found or failed to load.");
        }
    }
}
