import org.junit.Test;
import org.junit.Before;
import server.Book;

import static org.junit.Assert.*;


public class BookTest {
    private Book testBook;
    private String ISBN = "1112223334445";
    private String title = "Test";
    private String author = "Testy McTest";
    private String edition = "1st";
    private Boolean available;
    private String copyID = "9998887776665";
    private Boolean isLoaned = true;
    private String loanLength = "7";


    @Before
    public void Setup(){
        testBook = new Book(ISBN, title, author);
    }

    @Test
    public void testConstructor(){
        assertEquals(testBook.getISBN(), ISBN);
        assertEquals(testBook.getTitle(), title);
        assertEquals(testBook.getAuthor(), author);
        assertFalse(testBook.getIsLoaned());
    }

    @Test
    public void testSettersAndGetters() {
        testBook.setEdition(edition);
        testBook.setAvailable("1");
        testBook.setCopyID(copyID);
        testBook.setIsLoaned(isLoaned);
        testBook.setLoanLength(loanLength);

        assertEquals(testBook.getEdition(), edition);
        assertEquals(testBook.getAvailable(), true);
        assertEquals(testBook.getCopyID(), copyID);
        assertEquals(testBook.getIsLoaned(), isLoaned);
        assertEquals(testBook.getLoanLength(), loanLength);
    }
}