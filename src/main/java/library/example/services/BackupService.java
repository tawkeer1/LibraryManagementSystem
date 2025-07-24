package library.example.services;

import library.example.models.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class BackupService {
    private static final String BOOKS_CSV = "backup_books.csv";
    private static final String USERS_CSV = "backup_users.csv";
    private static final String BOOK_COPIES_CSV = "backup_bookcopies.csv";


    // ---------------- BOOK BACKUP ----------------

    public static void saveBooks(List<Book> newBooks) {
        Set<String> existingKeys = new HashSet<>();

        // Load existing books to detect duplicates
        File file = new File(BOOKS_CSV);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = parseCSVLine(line);
                    if (parts.length >= 2) {
                        String key = parts[0].trim().toLowerCase() + "|" + parts[1].trim().toLowerCase(); // title|author
                        existingKeys.add(key);
                    }
                }
            } catch (IOException e) {
                System.err.println("Failed to read existing books: " + e.getMessage());
            }
        }

        // Append only new books
        try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
            for (Book book : newBooks) {
                String key = book.getTitle().trim().toLowerCase() + "|" + book.getAuthorName().trim().toLowerCase();
                if (!existingKeys.contains(key)) {
                    writer.println(escape(book.getTitle()) + "," + escape(book.getAuthorName()) + "," +
                            escape(book.getGenre()) + "," + escape(String.valueOf(book.getPages())));
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to append books: " + e.getMessage());
        }
    }


    public static List<Book> loadBooks() {
        List<Book> books = new ArrayList<>();
        File file = new File(BOOKS_CSV);
        if (!file.exists()) return books;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue; // skip blank lines

                String[] parts = parseCSVLine(line);
                if (parts.length < 4) {
                    System.err.println("Skipping malformed book entry: " + line);
                    continue;
                }

                try {
                    String title = parts[0].replace("\\,", ",").trim();
                    String author = parts[1].replace("\\,", ",").trim();
                    String genre = parts[2].replace("\\,", ",").trim();
                    int pages = Integer.parseInt(parts[3].trim());

                    books.add(new Book(title, author, genre, pages));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid page number in: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load books: " + e.getMessage());
        }

        return books;
    }

    public static void saveBookCopies(List<BookCopy> copies) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(BOOK_COPIES_CSV))) {
            for (BookCopy copy : copies) {
                writer.println(
                        escape(copy.getCopyId()) + "," +
                                escape(copy.getTitle()) + "," +
                                escape(copy.getAuthorName()) + "," +
                                escape(copy.getGenre()) + "," +
                                copy.getPages() + "," +
                                copy.isTaken()
                );
            }
        } catch (IOException e) {
            System.err.println("Failed to save book copies: " + e.getMessage());
        }
    }

    public static List<BookCopy> loadBookCopies() {
        List<BookCopy> copies = new ArrayList<>();
        File file = new File(BOOK_COPIES_CSV);
        if (!file.exists()) return copies;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = parseCSVLine(line);
                if (parts.length >= 6) {
                    try {
                        String copyId = parts[0].trim();
                        String title = parts[1].replace("\\,", ",").trim();
                        String author = parts[2].replace("\\,", ",").trim();
                        String genre = parts[3].replace("\\,", ",").trim();
                        int pages = Integer.parseInt(parts[4].trim());
                        boolean taken = Boolean.parseBoolean(parts[5].trim());

                        BookCopy copy = new BookCopy(copyId, title, author, genre, pages, taken);
                        copies.add(copy);
                    } catch (Exception e) {
                        System.err.println("Invalid book copy entry: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load book copies: " + e.getMessage());
        }

        return copies;
    }

    public static List<BookCopy> loadBookCopiesByTitle(String targetTitle) {
        List<BookCopy> copies = new ArrayList<>();
        File file = new File(BOOK_COPIES_CSV);
        if (!file.exists()) return copies;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = parseCSVLine(line);
                if (parts.length >= 6) {
                    try {
                        String copyId = parts[0].trim();
                        String title = parts[1].replace("\\,", ",").trim();
                        String author = parts[2].replace("\\,", ",").trim();
                        String genre = parts[3].replace("\\,", ",").trim();
                        int pages = Integer.parseInt(parts[4].trim());
                        boolean taken = Boolean.parseBoolean(parts[5].trim());

                        if (title.equalsIgnoreCase(targetTitle)) {
                            BookCopy copy = new BookCopy(copyId, title, author, genre, pages, taken);
                            copies.add(copy);
                        }
                    } catch (Exception e) {
                        System.err.println("Invalid book copy entry: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load book copies: " + e.getMessage());
        }

        return copies;
    }



    // ---------------- USER BACKUP ----------------

    public static void saveUsers(List<User> newUsers) {
        Set<Integer> existingUserIds = new HashSet<>();

        File file = new File(USERS_CSV);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = parseCSVLine(line);
                    if (parts.length >= 2) {
                        try {
                            existingUserIds.add(Integer.parseInt(parts[1])); // userId at index 1
                        } catch (NumberFormatException ignored) {}
                    }
                }
            } catch (IOException e) {
                System.err.println("Failed to read existing users: " + e.getMessage());
            }
        }

        // Append only new users
        try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
            for (User user : newUsers) {
                if (!existingUserIds.contains(user.getUserId())) {
                    if (user instanceof Student) {
                        writer.println("Student," + user.getUserId() + "," + escape(user.getName()) + "," +
                                escape(user.getEmail()) + "," + user.getPassword()  + "," + escape(user.getPhone()));
                    } else if (user instanceof Librarian) {
                        writer.println("Librarian," + user.getUserId() + "," + escape(user.getName()) + "," +
                                escape(user.getEmail()) + "," + user.getPassword() + "," + escape(user.getPhone()));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to append users: " + e.getMessage());
        }
    }

    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        File file = new File(USERS_CSV);
        if (!file.exists()) return users;

        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_CSV))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = parseCSVLine(line);
                if (parts.length >= 6) {
                    String role = parts[0];
                    int userId = Integer.parseInt(parts[1]);
                    String name = parts[2];
                    String email = parts[3];
                    String password = parts[4];
                    String phone = parts[5];

                    if ("Student".equalsIgnoreCase(role)) {
                        users.add(new Student(userId, name, email, password, phone));
                    } else if ("Librarian".equalsIgnoreCase(role)) {
                        users.add(new Librarian(userId, name, email, password, phone));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load users: " + e.getMessage());
        }
        return users;
    }


   // CSV Helpers

    private static String escape(String input) {
        return input == null ? "" : input.replace(",", "\\,");
    }

    private static String[] parseCSVLine(String line) {
        return line.split("(?<!\\\\),");
    }

}
