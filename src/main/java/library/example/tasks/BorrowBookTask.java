package library.example.tasks;

import library.example.models.BookCopy;
import library.example.models.Student;

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
