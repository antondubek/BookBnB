import org.junit.Test;
import org.junit.Before;
import server.BorrowedBook;

import static org.junit.Assert.*;


public class BorrowedBookTest {
    public BorrowedBook testBorrowedBook;
    public String ISBN = "1112223334445";
    private String title = "Test";
    private String status = "pending";
    private String personOfInterest = "1";
    private String author = "Testy McTest";
    private String edition = "1st";
    private Boolean available;
    private String copyID = "9998887776665";
    private Boolean isLoaned = true;
    private String loanLength = "7";


    @Before
    public void Setup(){
        testBorrowedBook = new BorrowedBook(ISBN, title, author, status, personOfInterest);
    }

    @Test
    public void testConstruction(){
        assertEquals(testBorrowedBook.getISBN(), ISBN);
        assertEquals(testBorrowedBook.getTitle(), title);
        assertEquals(testBorrowedBook.getAuthor(), author);
        assertFalse(testBorrowedBook.getIsLoaned());
        assertEquals(testBorrowedBook.getStatus(), status);
        assertEquals(testBorrowedBook.getPersonOfInterest(), personOfInterest);
    }

    @Test
    public void testSetters() {
        testBorrowedBook.setEdition(edition);
        testBorrowedBook.setAvailable("1");
        testBorrowedBook.setCopyID(copyID);
        testBorrowedBook.setIsLoaned(isLoaned);
        testBorrowedBook.setLoanLength(loanLength);

        testBorrowedBook.setStatus("approved");
        testBorrowedBook.setStartDate("23-04-2019");
        testBorrowedBook.setEndDate("30-04-2019");
        testBorrowedBook.setPersonOfInterest("2");
        testBorrowedBook.setRequestNumber("5");

        assertEquals(testBorrowedBook.getEdition(), edition);
        assertTrue(testBorrowedBook.getAvailable());
        assertEquals(testBorrowedBook.getCopyID(), copyID);
        assertEquals(testBorrowedBook.getIsLoaned(), isLoaned);
        assertEquals(testBorrowedBook.getLoanLength(), loanLength);

        assertEquals(testBorrowedBook.getStatus(), "approved");
        assertEquals(testBorrowedBook.getStartDate(), "23-04-2019");
        assertEquals(testBorrowedBook.getEndDate(), "30-04-2019");
        assertEquals(testBorrowedBook.getPersonOfInterest(), "2");
        assertEquals(testBorrowedBook.getRequestNumber(), "5");





    }
}
