package library.example.services;

import library.example.exceptions.BookNotFoundException;
import library.example.models.*;
import library.example.utils.GenericAssetManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class LibraryService {
    private final List<User> users;
    private final GenericAssetManager<Book> bookManager;
    private final GenericAssetManager<EBook> ebookManager;
    private final Map<String, BookCopy> copyIdMap;

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
        if (user.getName() == null || user.getName().trim().isEmpty() ||
                user.getEmail() == null || user.getEmail().trim().isEmpty() ||
                user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            System.out.println("User fields cannot be empty.");
            return;
        }

        boolean exists = users.stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(user.getEmail()));

        if (exists) {
            System.out.println("User with email already exists.");
        } else {
            users.add(user);
            System.out.println("User added successfully.");
        }
    }


    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    // Physical book methods
    public void addBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null.");
        }

        // Validate required fields
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Book title cannot be empty.");
        }
        if (book.getAuthorName() == null || book.getAuthorName().trim().isEmpty()) {
            throw new IllegalArgumentException("Book author cannot be empty.");
        }
        if (book.getGenre() == null || book.getGenre().trim().isEmpty()) {
            throw new IllegalArgumentException("Book genre cannot be empty.");
        }
        if (book.getPages() <= 0) {
            throw new IllegalArgumentException("Book must have a positive number of pages.");
        }

        //check for duplicates by title and author
        boolean duplicate = bookManager.getAll().stream()
                .anyMatch(b -> b.getTitle().equalsIgnoreCase(book.getTitle())
                        && b.getAuthorName().equalsIgnoreCase(book.getAuthorName()));

        if (duplicate) {
            throw new IllegalArgumentException("A book with the same title and author already exists.");
        }

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
        List<Book> books = bookManager.getAll();
        if(books.isEmpty()){
            System.out.println("No available book");
        }
        return books.stream()
                .filter(book -> book.getTitle().equalsIgnoreCase(title))
                .flatMap(book -> book.getCopies().stream())
                .filter(copy -> !copy.isTaken())
                .findFirst();
    }

    public List<BookCopy> getAllAvailableCopies() {
        List<Book> books = bookManager.getAll();
        List<BookCopy> bookCopiesFromDisk = BackupService.loadBookCopies();


        List<BookCopy> availableDiskCopies = bookCopiesFromDisk.stream()
                .filter(copy -> !copy.isTaken())
                .collect(Collectors.toList());

        List<BookCopy> availableInMemoryCopies = books.stream()
                .flatMap(book -> book.getCopies().stream())
                .filter(copy -> !copy.isTaken())
                .collect(Collectors.toList());

        // Combine both
        List<BookCopy> allAvailableCopies = new ArrayList<>();
        allAvailableCopies.addAll(availableDiskCopies);
        allAvailableCopies.addAll(availableInMemoryCopies);
        allAvailableCopies.forEach(System.out::println);
        return allAvailableCopies;
    }


    //get all copies we have with the title
    public List<BookCopy> getAllAvailableCopiesByTitle(String title) {
        List<Book> books = bookManager.getAll();

        List<BookCopy> availableCopies = books.stream()
                .filter(book -> book.getTitle().equalsIgnoreCase(title))
                .flatMap(book -> book.getMergedCopies().stream())
                .filter(copy -> !copy.isTaken())
                .collect(Collectors.toList());

        if (availableCopies.isEmpty()) {
            System.out.println("No available copies for title: " + title);
        }

        return availableCopies;
    }



    public List<Book> searchBooksByTitle(String title) throws BookNotFoundException {
        List<Book> books = bookManager.getAll();
        if(books.isEmpty()){
            throw new BookNotFoundException("No Book available currently");
        }

        return books.stream()
                .filter(book -> book.getTitle().equalsIgnoreCase(title))
                .collect(Collectors.toList());
    }

    public List<Book> searchBooksByAuthor(String author) {
        List<Book> books = bookManager.getAll();
        if(books.isEmpty()){
            System.out.println("No available book");
        }

        return books.stream()
                .filter(book -> book.getAuthorName().equalsIgnoreCase(author))
                .collect(Collectors.toList());
    }

    public BookCopy getCopyById(String copyId) {
        if(copyIdMap.containsKey(copyId)) {
            return copyIdMap.get(copyId);
        }
        else {
            System.out.println("No copy found");
        }
        return null;
    }

    public void printAllBooks() {
        List<Book> books = bookManager.getAll();
        if (books.isEmpty()) {
            System.out.println("No Book available");
            return;
        }

        books.forEach(book -> {
            List<BookCopy> mergedCopies = book.getMergedCopies();
            long available = mergedCopies.stream().filter(copy -> !copy.isTaken()).count();
            System.out.println(book + " (" + available + "/" + mergedCopies.size() + " available)");
        });
    }


    public void addEBook(EBook ebook) {
        if (ebook == null) {
            throw new IllegalArgumentException("EBook cannot be null.");
        }

        if (ebook.getTitle() == null || ebook.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("EBook title cannot be empty.");
        }

        if (ebook.getAuthorName() == null || ebook.getAuthorName().trim().isEmpty()) {
            throw new IllegalArgumentException("EBook author name cannot be empty.");
        }

        if (ebook.getGenre() == null || ebook.getGenre().trim().isEmpty()) {
            throw new IllegalArgumentException("EBook genre cannot be empty.");
        }

        boolean duplicate = ebookManager.getAll().stream()
                .anyMatch(e -> e.getTitle().equalsIgnoreCase(ebook.getTitle()) &&
                        e.getAuthorName().equalsIgnoreCase(ebook.getAuthorName()));

        if (duplicate) {
            throw new IllegalArgumentException("An EBook with the same title and author already exists.");
        }

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

        Optional<EBook> ebookFound = ebookManager.getAll().stream()
                .filter(ebook -> ebook.getTitle().equalsIgnoreCase(title))
                .findFirst();
        if(ebookFound.isEmpty()){
            System.out.println("Ebook not found");
        }
        return ebookFound;
    }

    public List<EBook> getAllAvailableEBooks() {
        List<EBook> ebooks = ebookManager.getAll();
        if(ebooks.isEmpty()){
            System.out.println("No Ebook found");
        }
        return ebooks.stream()
                .filter(EBook::access)
                .collect(Collectors.toList());
    }

    public void printAllEBooks() {
        List<EBook> ebooks = ebookManager.getAll();
        if(ebooks.isEmpty()){
            System.out.println("No Ebook found");
        }
        ebooks.forEach(System.out::println);
    }

    // Backup triggers (can be used manually or by a background thread)
    public void backupToDisk() {
        BackupService.saveBooks(getAllBooks());
        BackupService.saveUsers(getAllUsers());
        BackupService.saveBookCopies(getAllAvailableCopies());
        System.out.println("[Backup] Data saved successfully.");
    }

    public void reloadUsersFromDisk() {
        List<User> loaded = BackupService.loadUsers();
        if (loaded != null && !loaded.isEmpty()) {
            users.clear();
            users.addAll(loaded);
            System.out.println("Users loaded successfully.");
            loaded.forEach(System.out::println);
        } else {
            System.out.println("No user data found or failed to load.");
        }
    }

    public void reloadBooksFromDisk() {
        List<Book> loadedBooks = BackupService.loadBooks();
        if (loadedBooks != null && !loadedBooks.isEmpty()) {
            bookManager.clear();
            for (Book book : loadedBooks) {
                bookManager.add(book);
                for (BookCopy copy : book.getCopies()) {
                    copyIdMap.put(copy.getCopyId(), copy);
                }
            }
            System.out.println("Books loaded successfully.");
            loadedBooks.forEach(System.out::println);
        } else {
            System.out.println("No book data found or failed to load.");
        }
    }
}
