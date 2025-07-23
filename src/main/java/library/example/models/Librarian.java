package library.example.models;

import library.example.services.LibraryService;

import java.io.Serializable;

public class Librarian extends User implements Serializable {
    private static final long serialVersionUID = 1L;
    public Librarian(int userId, String name, String email, String password, String phone) {
        super(userId, name, email, password, phone);
    }

    @Override
    public String getRole() {
        return "Librarian";
    }
    public void addNewBook(LibraryService library, Book book) {
        try{
            library.addBook(book);
            System.out.println("Librarian added new book: " + book.getTitle());
        }catch(IllegalArgumentException e){
            System.out.println("Note: " + e.getMessage());
        }

    }

    @Override
    public String getPassword(){
        return password;
    }

    public void addBookCopyToBook(LibraryService library, int bookIndex, BookCopy copy) {
        library.addBookCopy(bookIndex, copy);
        System.out.println("Added copy ID: " + copy.getCopyId() + " to book at index " + bookIndex);
    }

    public void removeEBook(LibraryService library, String title) {
        if (library.removeEBook(title)) {
            System.out.println("Removed eBook: " + title);
        } else {
            System.out.println("eBook not found: " + title);
        }
    }

    public void addEBook(LibraryService library, EBook ebook) {
        library.addEBook(ebook);
        System.out.println("Librarian added eBook: " + ebook.getTitle());
    }


    public void removeBookCopy(Book book, BookCopy copy) {
        synchronized (book) {
            if (copy.isTaken()) {
                System.out.println("Cannot remove borrowed copy: " + copy.getTitle());
            } else if (book.getCopies().remove(copy)) {
                System.out.println("Removed book copy ID: " + copy.getCopyId() + " from: " + book.getTitle());
            } else {
                System.out.println("Book copy not found in " + book.getTitle());
            }
        }
    }

    @Override
    public void borrowBook(BookCopy copy) {
        if (copy == null) {
            System.out.println("Invalid book copy.");
            return;
        }

        synchronized (copy) {
            if (!copy.isTaken()) {
                borrowedBooks.add(copy);
                copy.setTaken(true);
                System.out.println("Librarian " + name + " borrowed: " + copy.getTitle());
            } else {
                System.out.println("Book already taken: " + copy.getTitle());
            }
        }
    }

    @Override
    public void returnBook(BookCopy copy) {
        if (copy == null) {
            System.out.println("Invalid book copy.");
            return;
        }

        synchronized (copy) {
            if (borrowedBooks.remove(copy)) {
                copy.setTaken(false);
                System.out.println("Librarian " + name + " returned: " + copy.getTitle());
            } else {
                System.out.println("Book not in librarian's borrowed list.");
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
}
