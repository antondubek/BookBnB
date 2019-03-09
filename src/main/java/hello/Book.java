package hello;

public class Book {
    public String ISBN;
    public String title;
    public String author;
    public String edition;
    public Boolean available;

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

    public void setAvailable(String available) { this.available = Boolean.parseBoolean(available); }

}
