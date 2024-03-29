
import org.mockito.Mock;
import org.mockito.Mockito;
import server.*;
import org.junit.Test;

import java.sql.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Testing class. Designed to test DataBase with JUnit tests.
 */
public class DatabaseLogicTest {

    @Test
    public void testLoginDetailsAreRight(){
        Connection jdbcConnection = Mockito.mock(Connection.class);
        DatabaseLogic.setConnection(jdbcConnection);
        //ResultSet resultSet = Mockito.mock(ResultSet.class);
        try {
            doNothing().when(jdbcConnection).close();
        } catch (SQLException se){
            System.out.println("");
        }
        assertTrue(UserDatabaseLogic.loginDetailsAreRight("password", new ArrayList<String>(Arrays.asList("password"))));
        assertFalse(UserDatabaseLogic.loginDetailsAreRight("passwor", new ArrayList<String>(Arrays.asList("password"))));
        assertFalse(UserDatabaseLogic.loginDetailsAreRight("password", new ArrayList<String>(Arrays.asList("password", ""))));
        assertFalse(UserDatabaseLogic.loginDetailsAreRight("password", new ArrayList<String>(Arrays.asList(""))));
        assertFalse(UserDatabaseLogic.loginDetailsAreRight("password", null));
        assertFalse(UserDatabaseLogic.loginDetailsAreRight(null, new ArrayList<String>(Arrays.asList(""))));
        DatabaseLogic.setConnection(null);
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
        assertArrayEquals(DatabaseLogic.getArrayListFromResultSet(resultSet, namesOfFieldsInResponse1).toArray(), predictedResponse1.toArray());
        //Test2
        ArrayList<String> predictedResponse2 = testGetArrayListFromResultSetHelper2(resultSet);
        String[] namesOfFieldsInResponse2 = new String[]{"ISBN","Title"};
        assertArrayEquals(DatabaseLogic.getArrayListFromResultSet(resultSet, namesOfFieldsInResponse2).toArray(), predictedResponse2.toArray());
    }

    @Test
    public void testBookMissesDetails() {
        Book book1 = new Book("", "Title", "Author");
        Book book2 = new Book("0000000000001", "", "Author");
        Book book3 = new Book("0000000000001", "Title", "");
        Book book4 = new Book("", "", "");
        assertTrue(BookDatabaseLogic.bookMissesDetails(book1));
        assertTrue(BookDatabaseLogic.bookMissesDetails(book2));
        assertTrue(BookDatabaseLogic.bookMissesDetails(book3));
        assertTrue(BookDatabaseLogic.bookMissesDetails(book4));
    }

    @Test
    public void setQueryTypeTest() {
        assertEquals(BookDatabaseLogic.getQueryType("all"), Query.FETCH_BOOKS_BASE);
        assertEquals(BookDatabaseLogic.getQueryType("das"), Query.FETCH_USER_BOOKS);
    }

    @Test
    public void testGetUsersFromResultSet() {
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        try {
            Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
            Mockito.when(resultSet.getString("name")).thenReturn("Riad");
            Mockito.when(resultSet.getString("email")).thenReturn("riadibadulla@gmail.com");
            Mockito.when(resultSet.getString("city")).thenReturn("Baku");
        } catch (SQLException se){
            System.out.println(se);
        }
        ArrayList<User> usersExpected = new ArrayList<>();
        User user = new User("Riad","riadibadulla@gmail.com","Baku");
        usersExpected.add(user);
        ArrayList<User> usersActual = UserDatabaseLogic.getUsersFromResultSet(resultSet);
        assertEquals(usersActual.get(0).getName(), usersExpected.get(0).getName());
        assertEquals(usersActual.get(0).getCity(), usersExpected.get(0).getCity());
        assertEquals(usersActual.get(0).getEmail(), usersExpected.get(0).getEmail());

    }


}

