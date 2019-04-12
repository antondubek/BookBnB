package server;

/**
 * Book class, contains all details about the book.
 */
public class Book {
    public String ISBN;
    private String title;
    private String author;
    private String edition;     //Optional field
    private Boolean available;  //if the user sets the book to available, so people can borrow it
    private String copyID;      //Optional field. used when sending books associated with a particular user
    private Boolean isLoaned;
    private String loanLength;  //Optional field, used to

    //Getters

    public String getISBN(){
        return ISBN;
    }

    public String getTitle(){
        return title;
    }

    public String getAuthor(){
        return author;
    }

    public String getEdition(){
        return edition;
    }

    public Boolean getAvailable(){
        return available;
    }

    public String getCopyID() { return copyID; }
    public String getLoanLength(){return loanLength;}

    public boolean getIsLoaned(){ return isLoaned; }

    /**
     * Constructor for book.
     * @param ISBN ISBN of the book.
     * @param title name of the book.
     * @param author author of the book.
     */
    public Book(String ISBN, String title, String author){
        this.ISBN = ISBN;
        this.title = title;
        this.author = author;
        this.isLoaned = false;
    }

    @Override
    public String toString() {
        return "ISBN "+ ISBN + " title " + title + " author " + author + " edition "+ edition +" ava " + available;
    }

    /**
     * Not all Book's will have a edition recorded, so a separate setter was created to edit the value of this
     * attribute.
     * @param edition String containing the edition number
     */
    public void setEdition(String edition) {
        this.edition = edition;
    }

    public void setAvailable(String available) { this.available = available.equals("1"); }

    public void setCopyID(String copyID) { this.copyID = copyID; }

    public void setIsLoaned(Boolean isLoaned) { this.isLoaned = isLoaned; }

    public void setLoanLength(String loanLength){this.loanLength = loanLength;}

}
