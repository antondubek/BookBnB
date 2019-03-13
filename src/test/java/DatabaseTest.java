import server.Database;
import server.Book;
import server.Query;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Testing class. Designed to test DataBase with JUnit tests.
 */
public class DatabaseTest {

    @Test
    public void TestLogin() {
        assertTrue(Database.loginIsSuccessful("W6ph5Mm5Pz8GgiULbPgzG37mj9g=", "test@amakepeace.com"));
        assertFalse(Database.loginIsSuccessful("5baa61e4c9b93f3f0682250b6cf8331b7ee68fd8", "test@amakepeace.com"));
        assertFalse(Database.loginIsSuccessful("123", "testEmail@mail.ru"));
    }

    @Test
    public void insertNewBook() {
        Book book = new Book("0000000000001", "Title", "Author");
        assertTrue(Database.insertNewBook(book, "test@amakepeace.com"));
        assertFalse(Database.insertNewBook(book, "test1@amakepeace.com"));
    }


    @Test
    public void fetchAllBooks(){
         assertNotEquals(Database.fetchAllBooks("all").size(),0);
         assertEquals(Database.fetchAllBooks("").size(),0);
         assertEquals(Database.fetchAllBooks("riad@baku.az").size(),0);
         assertNotEquals(Database.fetchAllBooks("test@amakepeace.com").size(),0);
    }


    @Test
    public void setQueryTypetest() {
        assertEquals(Database.getQueryType("all"), Query.FETCH_BOOKS_BASE);
        assertEquals(Database.getQueryType("das"), Query.FETCH_USER_BOOKS);
    }

    @Test
    public void findTheUser() {
        assertEquals(Database.findUser("test@amakepeace.com").size(), 1);
        assertEquals(Database.findUser("test@amakepeace.com").get(0).getCity(), "Newcastle");
        assertEquals(Database.findUser("test@amakepeace.com").get(0).getName(), "test anthony");
        assertNotEquals(Database.findUser("test@amakepeace.com").get(0).getName(), "anthony");

    }

}

