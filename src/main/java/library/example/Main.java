package library.example;

import library.example.admin.AdminReportGenerator;
import library.example.models.*;
import library.example.services.LibraryService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

class BorrowBookTask implements Runnable {
    private final Student student;
    private final BookCopy copy;

    public BorrowBookTask(Student student, BookCopy copy) {
        this.student = student;
        this.copy = copy;
    }

    @Override
    public void run() {
        student.borrowBook(copy);
    }
}

class ReturnBookTask implements Runnable {
    private final Student student;
    private final BookCopy copy;

    public ReturnBookTask(Student student, BookCopy copy) {
        this.student = student;
        this.copy = copy;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000); // simulate some reading time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        student.returnBook(copy);
    }
}
public class Main {
    private static int idCounter = 1000;
    private static int generateId() {
        return idCounter++;
    }

    public static void main(String[] args) {
        LibraryService library = new LibraryService();

        // Start background auto-backup every 5 minutes
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(600000);
                    library.backupToDisk();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\nWelcome to the Library System");
            System.out.print("Choose an option: [L]ibrarian Login, [S]tudent Login, [U] View Users, [A] Add User, [D] Delete User, [Q] Quit: ");
            String role = sc.nextLine().trim().toUpperCase();

            switch (role) {
                case "L" -> {
                    System.out.print("Enter Librarian email: ");
                    String email = sc.nextLine().trim().toLowerCase();
                    Optional<User> userOpt = library.findUserByEmail(email);

                    Librarian librarian;
                    if (userOpt.isPresent() && userOpt.get() instanceof Librarian) {
                        librarian = (Librarian) userOpt.get();
                    } else {
                        System.out.print("Enter name: ");
                        String name = sc.nextLine();
                        System.out.print("Enter phone number: ");
                        String phone = sc.nextLine();
                        librarian = new Librarian(generateId(), name, email, phone);
                        library.addUser(librarian);
                        System.out.println("New librarian added.");
                    }
                    showLibrarianMenu(librarian, library, sc);
                }

                case "S" -> {
                    System.out.print("Enter Student email: ");
                    String email = sc.nextLine().trim().toLowerCase();
                    Optional<User> userOpt = library.findUserByEmail(email);

                    Student student;
                    if (userOpt.isPresent() && userOpt.get() instanceof Student) {
                        student = (Student) userOpt.get();
                    } else {
                        System.out.print("Enter name: ");
                        String name = sc.nextLine();
                        System.out.print("Enter phone number: ");
                        String phone = sc.nextLine();
                        student = new Student(generateId(), name, email, phone);
                        library.addUser(student);
                        System.out.println("New student added.");
                    }
                    showStudentMenu(student, library, sc);
                }

                case "U" -> {
                    System.out.println("All Users:");
                    library.getAllUsers().forEach(user ->
                            System.out.println(user.getRole() + ": " + user.getName() + " (" + user.getEmail() + ")"));
                }

                case "A" -> {
                    System.out.print("Add (L)ibrarian or (S)tudent? ");
                    String type = sc.nextLine().trim().toUpperCase();
                    System.out.print("Enter name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter email: ");
                    String email = sc.nextLine().trim().toLowerCase();
                    System.out.print("Enter phone: ");
                    String phone = sc.nextLine().trim().toLowerCase();

                    if (library.findUserByEmail(email).isPresent()) {
                        System.out.println("User already exists with this email.");
                        break;
                    }

                    int id = generateId();
                    if (type.equals("L")) {
                        Librarian librarian = new Librarian(id, name, email, phone);
                        library.addUser(librarian);
                        System.out.println("Librarian added: " + name);
                    } else if (type.equals("S")) {
                        Student student = new Student(id, name, email, "8888888888");
                        library.addUser(student);
                        System.out.println("Student added: " + name);
                    } else {
                        System.out.println("Invalid user type.");
                    }
                }

                case "D" -> {
                    System.out.print("Enter user email to delete: ");
                    String email = sc.nextLine().trim().toLowerCase();
                    boolean deleted = library.deleteUserByEmail(email);
                    if (deleted) {
                        System.out.println("User deleted successfully.");
                    } else {
                        System.out.println("No user found with that email.");
                    }
                }

                case "Q" -> {
                    System.out.println("Exiting the Library System...");
                    return;
                }

                default -> System.out.println("Invalid input.");
            }
        }
    }



    private static void showLibrarianMenu(Librarian librarian, LibraryService library, Scanner sc) {
        while (true) {
            System.out.println("\nLibrarian Menu:");
            System.out.println("1. Add Book");
            System.out.println("2. Add Book Copy");
            System.out.println("3. Add EBook");
            System.out.println("4. Remove EBook");
            System.out.println("5. View All Books");
            System.out.println("6. View All EBooks");
            System.out.println("7. Sort Books by Title");
            System.out.println("8. Sort Books by Author");
            System.out.println("9. Search Books by Title");
            System.out.println("10. Search Books by Author");
            System.out.println("11. Generate report");
            System.out.println("12. backup to disk");
            System.out.println("13. Load users");
            System.out.println("14. Load books");
            System.out.println("0. Logout");
            System.out.print("Enter choice: ");
            int choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter title: ");
                    String title = sc.nextLine();
                    System.out.print("Enter author: ");
                    String author = sc.nextLine();
                    System.out.print("Enter genre: ");
                    String genre = sc.nextLine();
                    System.out.print("Enter pages: ");
                    int pages = Integer.parseInt(sc.nextLine());
                    Book book = new Book(title, author, genre, pages);
                    librarian.addNewBook(library, book);
                }
                case 2 -> {
                    System.out.print("Enter title of the book to add a copy to: ");
                    String title = sc.nextLine();
                    List<Book> matchedBooks = library.searchBooksByTitle(title);

                    if (matchedBooks.isEmpty()) {
                        System.out.println("No book found with title: " + title);
                    } else if (matchedBooks.size() == 1) {
                        Book book = matchedBooks.get(0);
                        System.out.print("Enter new copy ID: ");
                        int copyId = Integer.parseInt(sc.nextLine());
                        BookCopy copy = new BookCopy(copyId, book.getTitle(), book.getAuthorName(), book.getGenre(), book.getPages(), false);
                        int index = library.getAllBooks().indexOf(book);
                        librarian.addBookCopyToBook(library, index, copy);
                    } else {
                        System.out.println("Multiple books found with that title:");
                        for (int i = 0; i < matchedBooks.size(); i++) {
                            System.out.println((i + 1) + ". " + matchedBooks.get(i).getTitle() + " by " + matchedBooks.get(i).getAuthorName());
                        }
                        System.out.print("Enter choice: ");
                        int bookChoice = Integer.parseInt(sc.nextLine()) - 1;
                        if (bookChoice >= 0 && choice < matchedBooks.size()) {
                            Book book = matchedBooks.get(bookChoice);
                            System.out.print("Enter new copy ID: ");
                            int copyId = Integer.parseInt(sc.nextLine());
                            BookCopy copy = new BookCopy(copyId, book.getTitle(), book.getAuthorName(), book.getGenre(), book.getPages(), false);
                            int index = library.getAllBooks().indexOf(book);
                            librarian.addBookCopyToBook(library, index, copy);
                        } else {
                            System.out.println("Invalid selection.");
                        }
                    }
                }

                case 3 -> {
                    System.out.print("Enter title: ");
                    String title = sc.nextLine();
                    System.out.print("Enter author: ");
                    String author = sc.nextLine();
                    System.out.print("Enter genre: ");
                    String genre = sc.nextLine();
                    System.out.print("Enter pages: ");
                    int pages = Integer.parseInt(sc.nextLine());
                    System.out.print("Enter format: ");
                    String format = sc.nextLine();
                    System.out.print("DRM Protected (true/false): ");
                    boolean drm = Boolean.parseBoolean(sc.nextLine());
                    System.out.print("Max concurrent users: ");
                    int maxUsers = Integer.parseInt(sc.nextLine());
                    System.out.print("Download link: ");
                    String link = sc.nextLine();
                    EBook ebook = new EBook(title, author, genre, pages, format, link, drm, maxUsers);
                    librarian.addEBook(library, ebook);
                }
                case 4 -> {
                    System.out.print("Enter title of eBook to remove: ");
                    String title = sc.nextLine();
                    librarian.removeEBook(library, title);
                }
                case 5 -> library.printAllBooks();
                case 6 -> library.printAllEBooks();
                case 7 -> library.getAllBooks().stream()
                        .sorted((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()))
                        .forEach(System.out::println);
                case 8 -> library.getAllBooks().stream()
                        .sorted((a, b) -> a.getAuthorName().compareToIgnoreCase(b.getAuthorName()))
                        .forEach(System.out::println);

                case 9 -> {
                    System.out.print("Enter title to search: ");
                    String title = sc.nextLine();
                    List<Book> results = library.searchBooksByTitle(title);
                    if (results.isEmpty()) {
                        System.out.println("No books found with title: " + title);
                    } else {
                        results.forEach(System.out::println);
                    }
                }
                case 10 -> {
                    System.out.print("Enter author to search: ");
                    String author = sc.nextLine();
                    List<Book> results = library.searchBooksByAuthor(author);
                    if (results.isEmpty()) {
                        System.out.println("No books found by author: " + author);
                    } else {
                        results.forEach(System.out::println);
                    }
                }
                case 11 -> {
                    List<User> allUsers = library.getAllUsers();
                    AdminReportGenerator.generateReport("Library Users Report", allUsers);
                }

                case 12 -> library.backupToDisk();

                case 13 -> library.reloadUsersFromDisk();
                case 14 -> library.reloadBooksFromDisk();

                case 0 -> {
                    System.out.println("Logged out.");
                    return;
                }

                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void showStudentMenu(Student student, LibraryService library, Scanner sc) {
        while (true) {
            System.out.println("\nStudent Menu:");
            System.out.println("1. Borrow Book");
            System.out.println("2. Return Book");
            System.out.println("3. Access EBook");
            System.out.println("4. Release EBook");
            System.out.println("5. View Available Books");
            System.out.println("6. View Available EBooks");
            System.out.println("7. Search Books by Title");
            System.out.println("8. Search Books by Author");
            System.out.println("9. Generate report");
            System.out.println("10. Simulate borrowing/returning of books");
            System.out.println("0. Logout");
            System.out.print("Enter choice: ");
            int choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1 -> {
                    library.printAllBooks();
                    System.out.print("Enter title of book to borrow: ");
                    String title = sc.nextLine();
                    BookCopy copy = library.getAvailableCopyByTitle(title).orElse(null);
                    if (copy != null) {
                        student.borrowBook(copy); // No thread
                    } else {
                        System.out.println("No available copy.");
                    }
                }
                case 2 -> {
                    System.out.print("Enter copy ID to return: ");
                    int copyId = Integer.parseInt(sc.nextLine());
                    BookCopy toReturn = library.getCopyById(copyId);
                    if (toReturn != null) {
                        student.returnBook(toReturn); // No thread
                    } else {
                        System.out.println("Invalid copy ID.");
                    }
                }
                case 3 -> {
                    library.printAllEBooks();
                    System.out.print("Enter eBook title to access: ");
                    String title = sc.nextLine();
                    EBook ebook = library.getEBookByTitle(title).orElse(null);
                    student.accessEBook(ebook);
                }
                case 4 -> {
                    System.out.print("Enter eBook title to release: ");
                    String title = sc.nextLine();
                    EBook ebook = library.getEBookByTitle(title).orElse(null);
                    student.releaseEBook(ebook);
                }
                case 5 -> library.printAllBooks();
                case 6 -> library.getAllAvailableEBooks().forEach(System.out::println);
                case 7 -> {
                    System.out.print("Enter title to search: ");
                    String title = sc.nextLine();
                    List<Book> results = library.searchBooksByTitle(title);
                    if (results.isEmpty()) {
                        System.out.println("No books found with title: " + title);
                    } else {
                        results.forEach(System.out::println);
                    }
                }
                case 8 -> {
                    System.out.print("Enter author to search: ");
                    String author = sc.nextLine();
                    List<Book> results = library.searchBooksByAuthor(author);
                    if (results.isEmpty()) {
                        System.out.println("No books found by author: " + author);
                    } else {
                        results.forEach(System.out::println);
                    }
                }
                case 9 -> {
                    System.out.println("Generating report for all students:");
                    List<Student> students = library.getAllUsers().stream()
                            .filter(u -> u instanceof Student)
                            .map(u -> (Student) u)
                            .toList();
                    AdminReportGenerator.generateReport("Student Report", students);
                }
                case 10 -> {
                    System.out.println("\n=== Simulating borrowing/returning with multiple students... ===");

                    List<BookCopy> available = library.getAllAvailableCopies();
                    if (available.size() < 3) {
                        System.out.println("At least 3 available book copies are required for simulation.");
                        break;
                    }

                    // Create students
                    Student s1 = new Student(101, "Alice", "alice@example.com", "9991110001");
                    Student s2 = new Student(102, "Bob", "bob@example.com", "9991110002");
                    Student s3 = new Student(103, "Charlie", "charlie@example.com", "9991110003");

                    library.addUser(s1);
                    library.addUser(s2);
                    library.addUser(s3);

                    BookCopy c1 = available.get(0);
                    BookCopy c2 = available.get(1);
                    BookCopy c3 = available.get(2);

                    // Borrow threads
                    Thread t1 = new Thread(() -> {
                        s1.borrowBook(c1);
                        System.out.println(s1.getName() + " borrowed: " + c1.getTitle());
                    });

                    Thread t2 = new Thread(() -> {
                        s2.borrowBook(c2);
                        System.out.println(s2.getName() + " borrowed: " + c2.getTitle());
                    });

                    Thread t3 = new Thread(() -> {
                        s3.borrowBook(c3);
                        System.out.println(s3.getName() + " borrowed: " + c3.getTitle());
                    });

                    // Return threads (delayed)
                    Thread r1 = new Thread(() -> {
                        try {
                            Thread.sleep(2000);
                            s1.returnBook(c1);
                            System.out.println(s1.getName() + " returned: " + c1.getTitle());
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    });

                    Thread r2 = new Thread(() -> {
                        try {
                            Thread.sleep(2500);
                            s2.returnBook(c2);
                            System.out.println(s2.getName() + " returned: " + c2.getTitle());
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    });

                    Thread r3 = new Thread(() -> {
                        try {
                            Thread.sleep(3000);
                            s3.returnBook(c3);
                            System.out.println(s3.getName() + " returned: " + c3.getTitle());
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    });

                    t1.start(); t2.start(); t3.start();
                    r1.start(); r2.start(); r3.start();

                    // Wait for all threads to complete
                    try {
                        t1.join(); t2.join(); t3.join();
                        r1.join(); r2.join(); r3.join();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    System.out.println("\n=== Simulation complete ===\n");
                }





                case 0 -> {
                    System.out.println("Logged out.");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

}
