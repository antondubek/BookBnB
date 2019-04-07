import org.json.JSONObject;
import org.junit.Test;
import server.Book;
import server.Controller;
import server.ControllerHelper;
import server.User;

import static org.junit.Assert.*;
import java.util.ArrayList;

public class ControllerTest {

    @Test
    public void testGetPasswordFromJson(){
        JSONObject jsonObj1 = new JSONObject();
        jsonObj1.put("password", "4672937");

        JSONObject jsonObj2 = new JSONObject();
        jsonObj2.put("pasrd", "password");

        assertEquals(ControllerHelper.getPasswordFromJson(jsonObj1), "4672937");
        assertEquals(ControllerHelper.getPasswordFromJson(jsonObj2), "");
    }

    @Test
    public void testGetEmailFromJson(){
        JSONObject jsonObj1 = new JSONObject();
        jsonObj1.put("email", "riadibadulla@gmail.com");

        JSONObject jsonObj2 = new JSONObject();
        jsonObj2.put("em", "sdfj@ada.co,");

        assertEquals(ControllerHelper.getEmailFromJson(jsonObj1), "riadibadulla@gmail.com");
        assertEquals(ControllerHelper.getEmailFromJson(jsonObj2), "");
    }

    @Test
    public void testGetUserFromJSON(){
        JSONObject jsonObj1 = new JSONObject();
        jsonObj1.put("name", "name1");
        jsonObj1.put("email", "email1231@mail.ru");
        jsonObj1.put("city", "London");

        JSONObject jsonObj2 = new JSONObject();
        jsonObj1.put("nam", "name1");
        jsonObj1.put("email", "email1231@mail.ru");
        jsonObj1.put("city", "London");

        User testingUser1 = ControllerHelper.getUserFromJSON(jsonObj1);
        assertEquals(testingUser1.getEmail(), "email1231@mail.ru");
        assertEquals(testingUser1.getName(), "name1");
        assertEquals(testingUser1.getCity(), "London");

        assertNull(ControllerHelper.getUserFromJSON(jsonObj2));
    }

    @Test
    public void testGetJSONBooks(){
        ArrayList<Book> books = new ArrayList<Book>();
        books.add(new Book("1234567890","Song of Ice and Fire", "George Martin"));
        ArrayList<String> JSONBooks = ControllerHelper.getJSONBooks(books);
        String expectedJSON = "{\"ISBN\":\"1234567890\",\"title\":\"Song of Ice and Fire\",\"author\":\"George Martin\",\"edition\":null,\"available\":null,\"copyID\":null,\"isbn\":\"1234567890\"}";
        assertEquals(JSONBooks.get(0), expectedJSON);
    }

    @Test
    public void testGetFollowFields(){
        String[] followFields1 = {"test@amakepeace.com","riadibadulla@gmail.com"};
        JSONObject jsonObj1 = new JSONObject();
        jsonObj1.put("email", "test@amakepeace.com");
        jsonObj1.put("friendEmail", "riadibadulla@gmail.com");
        assertEquals(ControllerHelper.getFollowFields(jsonObj1), followFields1);

    }

    @Test
    public void testGetJSONFollows(){
        ArrayList<User> emailsOfFollows = new ArrayList<User>();
        User emailOfFollow = new User("","evaristo@castro.it","");
        emailsOfFollows.add(emailOfFollow);

        ArrayList<String> JSONFollows = new ArrayList<String>();
        String json1 = "{\"name\":\"\",\"email\":\"evaristo@castro.it\",\"city\":\"\"}";
        JSONFollows.add(json1);

        assertEquals(ControllerHelper.getJSONFollows(emailsOfFollows), JSONFollows);

    }

    @Test
    public void testGetUserFromArrayList(){
        User user1 = new User("Riad", "rio@gmail.com", "baku");
        User user2 = new User("Evaristo", "eva@gmail.com", "castro");
        ArrayList<User> users = new ArrayList<User>();
        users.add(user1);

        User userActual = ControllerHelper.getUserFromArrayList(users);

        assertEquals(userActual.getEmail(),"rio@gmail.com");
        assertEquals(userActual.getCity(),"baku");
        assertEquals(userActual.getName(),"Riad");

        users.add(user2);
        userActual = ControllerHelper.getUserFromArrayList(users);
        assertEquals(userActual.getEmail(),"");
        assertEquals(userActual.getCity(),"");
        assertEquals(userActual.getName(),"");

    }

    @Test
    public void testCreateJSONFromUser(){
        User user1 = new User("Riad", "rio@gmail.com", "baku");
        String json1 = "{\"name\":\"Riad\",\"email\":\"rio@gmail.com\",\"city\":\"baku\"}";
        assertEquals(ControllerHelper.createJSONFromUser(user1),json1);
        //TODO: test catch block as well
    }

    @Test
    public void testGetBookFromJSON(){
        JSONObject data = new JSONObject();
        data.put("ISBN","12345");
        data.put("title", "Dance with dragons");
        data.put("author", "George Martin");
        data.put("edition", "");

        Book actualBook = ControllerHelper.getBookFromJSON(data);

        String expectedISBN = "12345";
        String expectedTitle = "Dance with dragons";
        String expectedAuthor = "George Martin";
        String actualISBN = actualBook.getISBN();
        String actualTitle = actualBook.getTitle();
        String actualAuthor = actualBook.getAuthor();
        String actualEdition = actualBook.getEdition();

        assertEquals(actualAuthor,expectedAuthor);
        assertEquals(actualTitle, expectedTitle);
        assertEquals(actualISBN, expectedISBN);
        assertNull(actualEdition);

    }

    @Test
    public void testGetEmailToFetchFollowers(){
        String json = "{\"email\":\"riadibadulla@gmail.com\"}\n";
        assertEquals(ControllerHelper.getEmailToFetchFollowers(json), "riadibadulla@gmail.com");

        String json1 = "{\"emai\":\"riadibadulla@gmail.com\"}\n";
        assertEquals(ControllerHelper.getEmailToFetchFollowers(json1), "");
    }

    @Test
    public void testGetAvailabilityFromJSON(){
        JSONObject data = new JSONObject();
        data.put("email", "rio@gmail.com");
        data.put("available", "true");
        assertTrue(ControllerHelper.getAvailabilityFromJSON(data));
        data.put("available", "false");
        assertFalse(ControllerHelper.getAvailabilityFromJSON(data));
    }
}
