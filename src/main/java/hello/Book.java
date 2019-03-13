package hello;

/**
 * Book class, contains all details about the book.
 */
public class Book {
    public String ISBN;
    private String title;
    private String author;
    private String edition;     //Optional field
    private Boolean available;  //if the user sets the book to available, so people can borrow it

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
}
