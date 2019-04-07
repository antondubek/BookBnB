package server;

public class BorrowedBook extends Book {

    private String status;
    private String lenderName;
    private String startDate;
    private String endDate;

    BorrowedBook(String ISBN, String title, String author, String status, String lenderName) {
        super(ISBN, title, author);
        this.status = status;
        System.out.println("Status equals: " + this.status);
        this.lenderName = lenderName;
        System.out.println("Lender equals: " + this.lenderName);
    }

    //overloaded constructor
    BorrowedBook(String ISBN, String title, String author, String status, String lenderName, String startDate, String endDate) {
        this(ISBN, title, author, status, lenderName);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "status " + status + "lenderName " + lenderName + "start date " + startDate + "end date" + endDate;
    }


    //getters
    public String getStatus() {
        return status;
    }

    public String getLenderName() {
        return lenderName;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }
}
