package library.example;

import library.example.admin.AdminReportGenerator;
import library.example.exceptions.BookNotFoundException;
import library.example.exceptions.InvalidInputException;
import library.example.models.*;
import library.example.services.LibraryService;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
            Thread.sleep(1000 + new Random().nextInt(1500));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        student.returnBook(copy);
    }
}

public class Main {

         // simulation method case10 in studentMenu
        private static void simulateStudentActivity(LibraryService library) {
        System.out.println("\n=== Simulating random borrow/return by students... ===");

        List<BookCopy> availableCopies = library.getAllAvailableCopies();
        if (availableCopies.size() < 3) {
            System.out.println("At least 3 available book copies are needed for a realistic simulation.");
            return;
        }

        List<Student> students = List.of(
                new Student(101, "Alice", "alice@example.com", "password@123", "9991110001"),
                new Student(102, "Bob", "bob@example.com", "password@123", "9991110002"),
                new Student(103, "Charlie", "charlie@example.com", "password@123", "9991110003"),
                new Student(104, "John", "john@example.com", "password@123", "9991110004")
        );

        Random rand = new Random();
        ExecutorService executor = Executors.newFixedThreadPool(students.size() * 2); // Borrow + Return threads

        for (Student stu : students) {
            try {
                Thread.sleep(rand.nextInt(2000)); // Delay between each student's activity
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            BookCopy selectedCopy;
            synchronized (availableCopies) {
                List<BookCopy> stillAvailable = availableCopies.stream()
                        .filter(bc -> !bc.isTaken())
                        .toList();
                if (stillAvailable.isEmpty()) {
                    System.out.println(stu.getName() + " could not borrow (no books available)");
                    continue;
                }
                selectedCopy = stillAvailable.get(rand.nextInt(stillAvailable.size()));
            }

            executor.submit(new BorrowBookTask(stu, selectedCopy));
            executor.submit(new ReturnBookTask(stu, selectedCopy));
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("\n=== Simulation complete ===\n");
    }

    // handle prompts' validation
    private static String promptNonEmpty(Scanner sc, String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = sc.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Input cannot be empty. Please try again.");
            } else if(input.length() < 3){
                System.out.println("Input should be at least 3 characters");
            }
        } while (input.isEmpty() || input.length() < 3);
        return input;
    }

    private static String promptPhone(Scanner sc, String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = sc.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Input cannot be empty. Please try again.");
            } else if (input.length() < 10 || !input.matches("\\d+")) {
                System.out.println("Phone number must be at least 10 digits and only contain numbers.");
            }
        } while (input.isEmpty() || input.length() < 10 || !input.matches("\\d+"));
        return input;
    }

    private static String promptName(Scanner sc, String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = sc.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Input cannot be empty. Please try again.");
            } else if (input.length() < 3) {
                System.out.println("Name must be at least 3 characters");
            }
        } while (input.isEmpty() || input.length() < 3);
        return input;
    }


    private static String promptValidEmail(Scanner sc, String prompt) {
        String email;
        do {
            email = promptNonEmpty(sc, prompt).toLowerCase();
            if (!email.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
                System.out.println("Invalid email format. Please try again.");
                email = null;
            }
        } while (email == null);
        return email;
    }

    public static int promptPositiveInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();

            try {
                int number = Integer.parseInt(input);
                if (number < 0) {
                    throw new InvalidInputException("Number must be positive.");
                }
                return number;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            } catch (InvalidInputException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static int promptIntInRange(Scanner sc, String message, int min, int max) {
        int choice;
        while (true) {
            try {
                System.out.print(message);
                choice = Integer.parseInt(sc.nextLine());
                if (choice >= min && choice <= max) return choice;
                System.out.println("Please enter a number between " + min + " and " + max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }


    public static boolean promptBoolean(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt + " (yes/no): ");
            String input = sc.nextLine().trim().toLowerCase();

            if (input.equals("yes") || input.equals("y") || input.equals("true")) {
                return true;
            } else if (input.equals("no") || input.equals("n") || input.equals("false")) {
                return false;
            } else {
                System.out.println("Invalid input. Please enter yes or no.");
            }
        }
    }


    private static String promptPassword(Scanner sc, String prompt) {
        String password;
        do {
            System.out.print(prompt);
            password = sc.nextLine().trim();

            if (password.isEmpty()) {
                System.out.println("Password cannot be empty. Please try again.");
            } else if (password.length() < 6) {
                System.out.println("Password should be at least 6 characters long.");
            }

        } while (password.isEmpty() || password.length() < 6);

        return password;
    }


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
                    Thread.sleep(300000);
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
                    String email = promptValidEmail(sc, "Enter librarian email: ");
                    Optional<User> userOpt = library.findUserByEmail(email);

                    Librarian librarian;
                    if (userOpt.isPresent() && userOpt.get() instanceof Librarian) {
                        librarian = (Librarian) userOpt.get();

                        while (true) {
                            String enteredPassword = promptPassword(sc, "Enter your password: ");
                            if (librarian.getPassword().equals(enteredPassword)) {
                                break;
                            }
                            System.out.println("Incorrect password. Please try again.");
                        }
                    } else {
                        String name = promptName(sc, "Enter librarian name: ");
                        String phone = promptPhone(sc, "Enter phone: ");
                        String password = promptPassword(sc, "Create password: ");
                        librarian = new Librarian(generateId(), name, email, password, phone);
                        library.addUser(librarian);
                    }

                    showLibrarianMenu(librarian, library, sc);
                }



                case "S" -> {
                    String email = promptValidEmail(sc, "Enter student email: ");
                    Optional<User> userOpt = library.findUserByEmail(email);

                    Student student;
                    if (userOpt.isPresent() && userOpt.get() instanceof Student) {
                        student = (Student) userOpt.get();

                        while (true) {
                            String enteredPassword = promptPassword(sc, "Enter your password: ");
                            if (student.getPassword().equals(enteredPassword)) {
                                break;
                            }
                            System.out.println("Incorrect password. Please try again.");
                        }
                    } else {
                        String name = promptName(sc, "Enter student name: ");
                        String phone = promptPhone(sc, "Enter phone: ");
                        String password = promptPassword(sc, "Create password: ");
                        student = new Student(generateId(), name, email, password, phone);
                        library.addUser(student);
                    }

                    showStudentMenu(student, library, sc);
                }



                case "U" -> {
                    System.out.println("All Users:");
                    library.getAllUsers().forEach(user ->
                            System.out.println(user.getRole() + ": " + user.getName() + " (" + user.getEmail() + ")"));
                }

                case "A" -> {
                    String type = promptNonEmpty(sc, "Add (L/l) for librarian or (S/s) for student ");
                    String name = promptName(sc, "Enter name: ");
                    String email = promptValidEmail(sc, "Enter email: ");
                    String phone = promptPhone(sc, "Enter phone: ");
                    String password = promptPassword(sc,"Enter password: ");

                    if (library.findUserByEmail(email).isPresent()) {
                        System.out.println("User already exists with this email.");
                        break;
                    }

                    int id = generateId();
                    if (type.equals("L") || type.equals("l")) {
                        Librarian librarian = new Librarian(id, name, email, password, phone);
                        library.addUser(librarian);
                    } else if (type.equals("S") || type.equals("s")) {
                        Student student = new Student(id, name, email, password, phone);
                        library.addUser(student);
                    } else {
                        System.out.println("Invalid user type.");
                    }
                }

                case "D" -> {
                    String email = promptValidEmail(sc, "Enter email of user: ");
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
            System.out.println("12. Backup to disk");
            System.out.println("13. Load users");
            System.out.println("14. Load books");
            System.out.println("15. Remove Book by Title");
            System.out.println("16. Borrow a Book");
            System.out.println("17. Return a Book");
            System.out.println("18. Print All Copies");
            System.out.println("0. Logout");

            int choice = -1;
            while (true) {
                System.out.print("Enter choice: ");
                String input = sc.nextLine().trim();

                try {
                    choice = Integer.parseInt(input);
                    break; // valid number, exit loop
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number corresponding to a menu option.");
                }
            }

            switch (choice) {
                case 1 -> {
                    String title = promptNonEmpty(sc, "Enter title: ");
                    String author = promptNonEmpty(sc, "Enter author: ");
                    String genre = promptNonEmpty(sc, "Enter genre: ");
                    int pages = promptPositiveInt(sc, "Enter pages: ");
                    Book book = new Book(title, author, genre, pages);
                    librarian.addNewBook(library, book);
                }

                case 2 -> {
                    String title = promptNonEmpty(sc, "Enter title of the book to add a copy to: ");
                    try {
                        List<Book> matchedBooks = library.searchBooksByTitle(title); // may throw BookNotFoundException

                        if (matchedBooks.isEmpty()) {
                            System.out.println("No book found with title: " + title);
                        } else if (matchedBooks.size() == 1) {
                            Book book = matchedBooks.get(0);
                            String copyId = UUID.randomUUID().toString();
                            BookCopy copy = new BookCopy(copyId, book.getTitle(), book.getAuthorName(), book.getGenre(), book.getPages(), false);
                            int index = library.getAllBooks().indexOf(book);
                            librarian.addBookCopyToBook(library, index, copy);
                        } else {
                            System.out.println("Multiple books found with that title:");
                            for (int i = 0; i < matchedBooks.size(); i++) {
                                System.out.println((i + 1) + ". " + matchedBooks.get(i).getTitle() + " by " + matchedBooks.get(i).getAuthorName());
                            }
                            int bookChoice = promptPositiveInt(sc, "Enter choice: ") - 1;
                            if (bookChoice >= 0 && bookChoice < matchedBooks.size()) {
                                Book book = matchedBooks.get(bookChoice);
                                String copyId = UUID.randomUUID().toString();
                                BookCopy copy = new BookCopy(copyId, book.getTitle(), book.getAuthorName(), book.getGenre(), book.getPages(), false);
                                int index = library.getAllBooks().indexOf(book);
                                librarian.addBookCopyToBook(library, index, copy);
                            } else {
                                System.out.println("Invalid selection.");
                            }
                        }
                    } catch (BookNotFoundException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }



                case 3 -> {
                    String title = promptNonEmpty(sc, "Enter title: ");
                    String author = promptNonEmpty(sc, "Enter author: ");
                    String genre = promptNonEmpty(sc, "Enter genre: ");
                    int pages = promptPositiveInt(sc, "Enter pages: ");
                    String format = promptNonEmpty(sc, "Enter format: ");
                    boolean drm = promptBoolean(sc, "DRM Protected ");
                    int maxUsers = promptPositiveInt(sc, "Max concurrent users: ");
                    String link = promptNonEmpty(sc, "Download link: ");

                    EBook ebook = new EBook(title, author, genre, pages, format, link, drm, maxUsers);
                    librarian.addEBook(library, ebook);
                }


                case 4 -> {
                    String title = promptNonEmpty(sc, "Enter title of eBook to remove: ");
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
                    String title = promptNonEmpty(sc, "Enter title to search: ");
                    try{
                        List<Book> results = library.searchBooksByTitle(title);
                        results.forEach(System.out::println);
                    }catch(BookNotFoundException e){
                        System.out.println(e.getMessage());
                    }
                }


                case 10 -> {
                    String author = promptNonEmpty(sc, "Enter author to search: ");
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

                case 15 -> {
                    try{
                        String title = promptNonEmpty(sc, "Enter title of book to remove: ");
                        List<Book> books = library.searchBooksByTitle(title);
                        if (!books.isEmpty()) {
                            Book bookToRemove = books.get(0);
                            library.getAllBooks().remove(bookToRemove);
                            System.out.println("Book removed.");
                        } else {
                            System.out.println("Book not found.");
                        }
                    }catch (BookNotFoundException e){
                        System.out.println(e.getMessage());
                    }

                }


                case 16 -> {
                    String title = promptNonEmpty(sc, "Enter book title to borrow: ");
                    List<BookCopy> availableCopies = library.getAllAvailableCopiesByTitle(title);

                    if (availableCopies.isEmpty()) {
                        System.out.println("No available copies for: " + title);
                    } else {
                        System.out.println("Available copies:");
                        for (int i = 0; i < availableCopies.size(); i++) {
                            BookCopy copy = availableCopies.get(i);
                            System.out.printf("%d. ID: %s | Title: %s%n", i + 1, copy.getCopyId(), copy.getTitle());
                        }

                        int c = promptIntInRange(sc, "Select copy to borrow (number): ", 1, availableCopies.size());
                        BookCopy selected = availableCopies.get(c - 1);

                        librarian.borrowBook(selected);
                        System.out.println("Borrowed copy with ID: " + selected.getCopyId());
                    }
                }

                case 17 -> {
                    List<BookCopy> borrowed = librarian.getBorrowedBooks();

                    if (borrowed.isEmpty()) {
                        System.out.println("You have not borrowed any books.");
                    } else {
                        System.out.println("Your borrowed books:");
                        for (int i = 0; i < borrowed.size(); i++) {
                            BookCopy copy = borrowed.get(i);
                            System.out.printf("%d. ID: %s | Title: %s%n", i + 1, copy.getCopyId(), copy.getTitle());
                        }

                        int c = promptIntInRange(sc, "Select copy to return (number): ", 1, borrowed.size());
                        BookCopy toReturn = borrowed.get(c - 1);

                        librarian.returnBook(toReturn);
                        System.out.println("Returned: " + toReturn.getTitle() + " (ID: " + toReturn.getCopyId() + ")");
                    }
                }
                case 18 -> library.getAllAvailableCopies();
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

            int choice = promptPositiveInt(sc, "Enter choice: ");
            switch (choice) {
                case 1 -> {
                    library.printAllBooks();
                    String title = promptNonEmpty(sc, "Enter title of book to borrow: ");
                    List<BookCopy> availableCopies = library.getAllAvailableCopiesByTitle(title);

                    if (availableCopies.isEmpty()) {
                        System.out.println("No available copies for this title.");
                    } else {
                        System.out.println("Available copies:");
                        for (int i = 0; i < availableCopies.size(); i++) {
                            BookCopy copy = availableCopies.get(i);
                            System.out.printf("%d. ID: %s | Title: %s%n", i + 1, copy.getCopyId(), copy.getTitle());
                        }

                        int serialNo;
                        while (true) {
                            serialNo = promptPositiveInt(sc, "Enter the serial number of the copy to borrow: ");
                            if (serialNo >= 1 && serialNo <= availableCopies.size()) {
                                break;
                            } else {
                                System.out.println("Invalid serial number. Please choose between 1 and " + availableCopies.size());
                            }
                        }

                        BookCopy selected = availableCopies.get(serialNo - 1);
                        student.borrowBook(selected);
                        System.out.println("Borrowed copy ID: " + selected.getCopyId());
                    }
                }



                case 2 -> {
                    List<BookCopy> borrowed = student.getBorrowedBooks();

                    if (borrowed.isEmpty()) {
                        System.out.println("You haven't borrowed any books.");
                    } else {
                        System.out.println("Your borrowed books:");
                        for (int i = 0; i < borrowed.size(); i++) {
                            BookCopy copy = borrowed.get(i);
                            System.out.printf("%d. ID: %s | Title: %s%n", i + 1, copy.getCopyId(), copy.getTitle());
                        }

                        int c = promptIntInRange(sc, "Select copy to return (number): ", 1, borrowed.size());
                        BookCopy toReturn = borrowed.get(c - 1);

                        student.returnBook(toReturn);
                        System.out.println("Returned: " + toReturn.getTitle() + " (ID: " + toReturn.getCopyId() + ")");
                    }
                }


                case 3 -> {
                    library.printAllEBooks();
                    String title = promptNonEmpty(sc, "Enter eBook title to access: ");
                    EBook ebook = library.getEBookByTitle(title).orElse(null);
                    student.accessEBook(ebook);
                }
                case 4 -> {
                    String title = promptNonEmpty(sc, "Enter eBook title to release: ");
                    EBook ebook = library.getEBookByTitle(title).orElse(null);
                    student.releaseEBook(ebook);
                }
                case 5 -> library.printAllBooks();
                case 6 -> library.getAllAvailableEBooks().forEach(System.out::println);
                case 7 -> {
                    try{
                        String title = promptNonEmpty(sc, "Enter title to search: ");
                        List<Book> results = library.searchBooksByTitle(title);
                        if (results.isEmpty()) {
                            System.out.println("No books found with title: " + title);
                        } else {
                            results.forEach(System.out::println);
                        }
                    }catch (BookNotFoundException e){
                        System.out.println(e.getMessage());
                    }

                }
                case 8 -> {
                    String author = promptNonEmpty(sc, "Enter author to search: ");
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
                case 10 -> simulateStudentActivity(library);
                case 0 -> {
                    System.out.println("Logged out.");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

}

