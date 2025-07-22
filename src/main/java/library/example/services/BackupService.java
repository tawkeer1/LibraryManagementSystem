package library.example.services;

import library.example.models.*;
import java.io.*;
import java.util.List;

public class BackupService {
    private static final String BOOKS_FILE = "backup_books.ser";
    private static final String USERS_FILE = "backup_users.ser";

    public static void saveBooks(List<Book> books) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(BOOKS_FILE))) {
            out.writeObject(books);
        } catch (IOException e) {
            System.err.println("Failed to save books: " + e.getMessage());
        }
    }

    public static void saveUsers(List<User> users) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            out.writeObject(users);
        } catch (IOException e) {
            System.err.println("Failed to save users: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Book> loadBooks() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(BOOKS_FILE))) {
            return (List<Book>) in.readObject();
        } catch (Exception e) {
            System.out.println("No saved books found or failed to load.");
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static List<User> loadUsers() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(USERS_FILE))) {
            return (List<User>) in.readObject();
        } catch (Exception e) {
            System.out.println("No saved users found or failed to load.");
            return null;
        }
    }
}
