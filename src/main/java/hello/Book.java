package hello;

public class Book {
    public String ISBN;
    public String title;
    public String author;
    public String version;

    public Book(String ISBN, String title, String author){
        this.ISBN = ISBN;
        this.title = title;
        this.author = author;
    }

    /**
     * Not all Book's will have a version recorded, so a separate setter was created to edit the value of this
     * attribute.
     * @param version String containing the version number
     */
    public void setVersion(String version) {
        this.version = version;
    }

}
