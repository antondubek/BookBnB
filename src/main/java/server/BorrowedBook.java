package server;

public class BorrowedBook extends Book {

    private String status;
    private String personOfInterest;  //This field represents the name of the person either lending or borrowing the book, depending on how it is used
    private String startDate;
    private String endDate;
    private String requestNumber;

    public BorrowedBook(String ISBN, String title, String author, String status, String personOfInterest) {
        super(ISBN, title, author);
        this.status = status;
        this.personOfInterest = personOfInterest;
    }

    @Override
    public String toString() {
        return "status " + status + "personOfInterest " + personOfInterest + "start date " + startDate + "end date" + endDate;
    }


    //getters
    public String getStatus() {
        return status;
    }

    public String getPersonOfInterest() {
        return personOfInterest;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getRequestNumber() { return requestNumber; }

    //Setters
    public void setStatus(String status) {
        this.status = status;
    }

    public void setPersonOfInterest(String personOfInterest) {
        this.personOfInterest = personOfInterest;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setRequestNumber(String requestNumber){ this.requestNumber = requestNumber; }
}
