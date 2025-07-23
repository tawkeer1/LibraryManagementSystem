import library.example.models.Book;
import library.example.models.BookCopy;
import library.example.models.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StudentTest {
    private Student student;
    private Book book;

    @BeforeEach
    void setUp() {
        student = new Student(1, "Test Student", "test@student.com","pass@123", "1234567890");
        book = new Book("Java", "Author", "Tech", 300);
        // add 3 copies to simulate real scenario
        for (int i = 1; i <= 3; i++) {
            book.addCopy(new BookCopy(i, "Java", "Author", "Tech", 300, false));
        }
    }

    @Test
    void testBorrowBookWithinLimit() {
        BookCopy copy = book.getCopies().get(0);
        student.borrowBook(copy);
        assertEquals(1, student.getBorrowedBooks().size());
        assertTrue(copy.isTaken());
    }

    @Test
    void testBorrowBeyondLimit() {
        for (int i = 0; i < 3; i++) {
            BookCopy copy = new BookCopy(i + 10, "Java", "Author", "Tech", 300, false);
            student.borrowBook(copy);
        }

        BookCopy extra = new BookCopy(99, "Java", "Author", "Tech", 300, false);
        student.borrowBook(extra); // should be ignored due to limit

        assertEquals(3, student.getBorrowedBooks().size());
        assertFalse(extra.isTaken(), "Extra copy should not be borrowed");
    }

    @Test
    void testReturnBookSuccessfully() {
        BookCopy copy = book.getCopies().get(0);
        student.borrowBook(copy);
        assertTrue(copy.isTaken());

        student.returnBook(copy);
        assertFalse(copy.isTaken());
        assertEquals(0, student.getBorrowedBooks().size());
    }

    @Test
    void testReturnBookNotBorrowed() {
        BookCopy fakeCopy = new BookCopy(999, "Ghost Book", "Ghost", "Mystery", 100, false);
        student.returnBook(fakeCopy);
        assertFalse(fakeCopy.isTaken());
        assertEquals(0, student.getBorrowedBooks().size());
    }
}
