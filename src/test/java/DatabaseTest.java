import org.mockito.Mockito;
import server.Database;
import server.Book;
import server.Query;
import org.junit.Test;
import java.sql.*;
import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
/**
 * Testing class. Designed to test DataBase with JUnit tests.
 */
public class DatabaseTest {

    @Test
    public void testLoginDetailsAreRight(){
        Connection jdbcConnection = Mockito.mock(Connection.class);
        Database.setConnection(jdbcConnection);
        //ResultSet resultSet = Mockito.mock(ResultSet.class);
        try {
            doNothing().when(jdbcConnection).close();
        } catch (SQLException se){
            System.out.println("");
        }
        assertTrue(Database.loginDetailsAreRight("password", new ArrayList<String>(Arrays.asList("password"))));
        assertFalse(Database.loginDetailsAreRight("passwor", new ArrayList<String>(Arrays.asList("password"))));
        assertFalse(Database.loginDetailsAreRight("password", new ArrayList<String>(Arrays.asList("password", ""))));
        assertFalse(Database.loginDetailsAreRight("password", new ArrayList<String>(Arrays.asList(""))));
        assertFalse(Database.loginDetailsAreRight("password", null));
        assertFalse(Database.loginDetailsAreRight(null, new ArrayList<String>(Arrays.asList(""))));
        Database.setConnection(null);
    }

    public ArrayList<String> testGetArrayListFromResultSetHelper1(ResultSet resultSet){
        ArrayList<String> predictedResponse = new ArrayList<String>(Arrays.asList("123","ABC","GDK"));

        try {
            Mockito.when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);
            Mockito.when(resultSet.getString("password")).thenReturn("123").thenReturn("ABC").thenReturn("GDK");
        } catch (SQLException se){
            System.out.println(se);
        }
        return predictedResponse;
    }

    public ArrayList<String> testGetArrayListFromResultSetHelper2(ResultSet resultSet){
        ArrayList<String> predictedResponse = new ArrayList<String>(Arrays.asList("1234567890","book1","0123456", "book2"));

        try {
            Mockito.when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
            Mockito.when(resultSet.getString("ISBN")).thenReturn("1234567890").thenReturn("0123456");
            Mockito.when(resultSet.getString("Title")).thenReturn("book1").thenReturn("book2");
        } catch (SQLException se){
            System.out.println(se);
        }
        return predictedResponse;
    }

    @Test
    public void testGetArrayListFromResultSet(){
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        //Test1
        ArrayList<String> predictedResponse1 = testGetArrayListFromResultSetHelper1(resultSet);
        String[] namesOfFieldsInResponse1 = new String[]{"password"};
        assertArrayEquals(Database.getArrayListFromResultSet(resultSet, namesOfFieldsInResponse1).toArray(), predictedResponse1.toArray());
        //Test2
        ArrayList<String> predictedResponse2 = testGetArrayListFromResultSetHelper2(resultSet);
        String[] namesOfFieldsInResponse2 = new String[]{"ISBN","Title"};
        assertArrayEquals(Database.getArrayListFromResultSet(resultSet, namesOfFieldsInResponse2).toArray(), predictedResponse2.toArray());
    }



   /* @Test
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
    public void insertBookWithoutSomeDetails() {
        Book book1 = new Book("", "Title", "Author");
        Book book2 = new Book("0000000000001", "", "Author");
        Book book3 = new Book("0000000000001", "Title", "");
        Book book4 = new Book("", "", "");
        assertFalse(Database.insertNewBook(book1, "test@amakepeace.com"));
        assertFalse(Database.insertNewBook(book2, "test@amakepeace.com"));
        assertFalse(Database.insertNewBook(book3, "test@amakepeace.com"));
        assertFalse(Database.insertNewBook(book4, "test@amakepeace.com"));
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

    }*/
}

