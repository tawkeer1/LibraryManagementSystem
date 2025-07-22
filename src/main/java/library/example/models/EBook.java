package library.example.models;

import java.io.Serializable;

public class EBook extends Book implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String format;
    private final String downloadLink;
    private final boolean isDRMProtected;
    private final int maxConcurrentUsers;
    private int currentUsers = 0;
    private final Object lock = new Object(); // internal lock for thread-safety

    public EBook(String title, String author, String genre, int pages,
                 String format,  String downloadLink,
                 boolean isDRMProtected, int maxConcurrentUsers) {
        super(title, author, genre, pages); // from Book class
        this.format = format;
        this.downloadLink = downloadLink;
        this.isDRMProtected = isDRMProtected;
        this.maxConcurrentUsers = maxConcurrentUsers;
    }

    public boolean access() {
        synchronized (lock) {
            if (currentUsers < maxConcurrentUsers) {
                currentUsers++;
                System.out.println("Access granted to eBook: " + title);
                return true;
            } else {
                System.out.println("Access limit reached for eBook: " + title);
                return false;
            }
        }
    }

    public void release() {
        synchronized (lock) {
            if (currentUsers > 0) {
                currentUsers--;
                System.out.println("User released eBook: " + title);
            }
        }
    }

    public String getFormat() {
        return format;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public boolean isDRMProtected() {
        return isDRMProtected;
    }

    public int getCurrentUsers() {
        return currentUsers;
    }

    public int getMaxConcurrentUsers() {
        return maxConcurrentUsers;
    }

    @Override
    public String toString() {
        return "\n--------------------------" +
                "\nEBook Title     : " + title +
                "\nFormat          : " + format +
                "\nDRM Protected   : " + isDRMProtected +
                "\nUsers Accessing : " + currentUsers + "/" + maxConcurrentUsers +
                "\nDownload Link   : " + downloadLink +
                "\n--------------------------";
    }

}
