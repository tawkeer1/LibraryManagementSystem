package library.example;

import library.example.models.*;
import library.example.services.LibraryService;

import java.util.List;
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
    public static void main(String[] args) {
        LibraryService library = new LibraryService();
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\nWelcome to the Library System");
            System.out.print("Are you a Librarian or Student? (L/S), or Q to Quit: ");
            String role = sc.nextLine().trim().toUpperCase();

            if (role.equals("L")) {
                Librarian librarian = new Librarian(1, "Librarian1", "librarian@example.com", "9999999999");
                showLibrarianMenu(librarian, library, sc); // returns to here after logout
            } else if (role.equals("S")) {
                Student student = new Student(2, "Student1", "student@example.com", "8888888888");
                showStudentMenu(student, library, sc); // returns to here after logout
            } else if (role.equals("Q")) {
                System.out.println("Exiting the Library System. Goodbye!");
                break; // ends the while loop and exits
            } else {
                System.out.println("Invalid input. Please enter 'L' for Librarian, 'S' for Student, or 'Q' to Quit.");
            }
        }

        sc.close();
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
            System.out.println("0. Logout");
            System.out.print("Enter choice: ");
            int choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1 -> {
                    library.printAllBooks();
                    System.out.print("Enter title of book to borrow: ");
                    String title = sc.nextLine();
                    BookCopy copy = library.getAvailableCopyByTitle(title).orElse(null);
                    //Borrowing logic using threads
                    if (copy != null) {
                        Thread borrowThread = new Thread(new BorrowBookTask(student, copy));
                        borrowThread.start();
                    } else {
                        System.out.println("No available copy.");
                    }
                }
                case 2 -> {
                    System.out.print("Enter copy ID to return: ");
                    int copyId = Integer.parseInt(sc.nextLine());
                    BookCopy toReturn = library.getCopyById(copyId);
                    if (toReturn != null) {
                        Thread returnThread = new Thread(new ReturnBookTask(student, toReturn));
                        returnThread.start();
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
                case 5 -> library.getAllAvailableCopies().forEach(System.out::println);
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
                case 0 -> {
                    System.out.println("Logged out.");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

}
