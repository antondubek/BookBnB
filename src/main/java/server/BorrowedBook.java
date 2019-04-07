package server;

public class BorrowedBook extends Book {

    private String status;
    private String lenderName;
    private String startDate;
    private String endDate;

    BorrowedBook(String ISBN, String title, String author, String status, String lenderName) {
        super(ISBN, title, author);
        this.status = status;
        this.lenderName = lenderName;
    }

    //overloaded constructor //TODO delete this
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

    //Setters
    public void setStatus(String status) {
        this.status = status;
    }

    public void setLenderName(String lenderName) {
        this.lenderName = lenderName;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
