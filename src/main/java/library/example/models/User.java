package library.example.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;
    protected final int userId;
    protected String name;
    protected String email;
    protected String phone;
    protected final List<BookCopy> borrowedBooks;

    public User(int userId, String name, String email, String phone) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.borrowedBooks = Collections.synchronizedList(new ArrayList<>());
    }

    public abstract String getRole();
    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<BookCopy> getBorrowedBooks() {
        return borrowedBooks;
    }

    public void borrowBook(BookCopy copy) {
        if (copy == null) {
            System.out.println("Invalid book copy provided.");
            return;
        }

        synchronized (copy.getLock()) {
            if (!copy.isTaken()) {
                borrowedBooks.add(copy);
                copy.setTaken(true);
                System.out.println(name + " borrowed: " + copy.getTitle());
            } else {
                System.out.println("Book already taken: " + copy.getTitle());
            }
        }
    }

    public void returnBook(BookCopy copy) {
        if (copy == null) {
            System.out.println("Invalid book copy provided.");
            return;
        }

        synchronized (copy.getLock()) {
            if (borrowedBooks.remove(copy)) {
                copy.setTaken(false);
                System.out.println(name + " returned: " + copy.getTitle());
            } else {
                System.out.println("Book not found in user's borrowed list.");
            }
        }
    }


    public void accessEBook(EBook ebook) {
        if (ebook == null) {
            System.out.println("Invalid eBook.");
            return;
        }

        if (ebook.access()) {
            System.out.println(name + " is accessing eBook: " + ebook.getTitle());
        } else {
            System.out.println("Could not access eBook: " + ebook.getTitle());
        }
    }

    public void releaseEBook(EBook ebook) {
        if (ebook == null) {
            System.out.println("Invalid eBook.");
            return;
        }

        ebook.release();
        System.out.println(name + " released eBook: " + ebook.getTitle());
    }


    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", borrowedBooks=" + borrowedBooks.size() +
                '}';
    }
}
