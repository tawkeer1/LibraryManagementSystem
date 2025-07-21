package library.example.models;

public class Student extends User {
    private final int borrowLimit = 3;

    public Student(int userId, String name, String email, String phone) {
        super(userId, name, email, phone);
    }

    @Override
    public String getRole() {
        return "Student";
    }
    @Override
    public void borrowBook(BookCopy copy) {
        synchronized (copy.getLock()) {
//            if (copy.isTaken()) {
//                System.out.println("Book already taken: " + copy.getTitle());
//                return;
//            }
// BUG: Missing condition to check borrow limit
//            borrowedBooks.add(copy);
//            copy.setTaken(true);

            if (borrowedBooks.size() >= borrowLimit) {
                System.out.println("Borrow limit reached for student: " + name);
                return;
            }

            if (!copy.isTaken()) {
                borrowedBooks.add(copy);
                copy.setTaken(true);
                System.out.println(name + " borrowed: " + copy.getTitle());
            } else {
                System.out.println("Book already taken: " + copy.getTitle());
            }
        }
    }

    @Override
    public void returnBook(BookCopy copy) {
        synchronized (copy) {
            if (borrowedBooks.remove(copy)) {
                copy.setTaken(false);
                System.out.println(name + " returned: " + copy.getTitle());
            } else {
                System.out.println("Book not found in student's borrowed list.");
            }
        }
    }
}
