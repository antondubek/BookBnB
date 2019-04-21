import org.json.JSONObject;
import org.junit.Test;
import server.*;

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
        String expectedJSON = "{\"ISBN\":\"1234567890\",\"title\":\"Song of Ice and Fire\",\"author\":\"George Martin\",\"edition\":null,\"available\":null,\"copyID\":null,\"isLoaned\":false,\"loanLength\":null,\"isbn\":\"1234567890\"}";
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

    @Test
    public void testGetJSONBorrowedBooks(){
        BorrowedBook book1 = new BorrowedBook("123456","Title1","Author1","active", "riadibadulla@gmail.com");
        book1.setStartDate("01.02.2019");
        book1.setEndDate("02.09.2019");
        book1.setRequestNumber("12");
        book1.setCopyID("4");
        ArrayList<BorrowedBook> listOfBooks = new ArrayList<BorrowedBook>();
        listOfBooks.add(book1);
        String JSONStringExpected = "[{\"copyID\":\"4\",\"requestNumber\":\"12\",\"ISBN\":\"123456\",\"endDate\":\"02.09.2019\",\"author\":\"Author1\",\"name\":\"riadibadulla@gmail.com\",\"title\":\"Title1\",\"startDate\":\"01.02.2019\",\"status\":\"active\"}]";
        String JSONActual = ControllerHelper.getJSONBorrowedBooks(listOfBooks).toString();
        assertEquals(JSONActual,JSONStringExpected);
    }

    @Test
    public void testGetJSONLenders(){
        Lender lender = new Lender("12","Name1","City1","12","1");
        ArrayList<Lender> lenders = new ArrayList<Lender>();
        lenders.add(lender);

        String ExpectedJSONArray = "[{\"name\":\"Name1\",\"city\":\"City1\",\"loanLength\":\"12\",\"copyID\":\"1\",\"id\":\"12\"}]";
        String actualJSOON = ControllerHelper.getJSONLenders(lenders).toString();
        assertEquals(actualJSOON, ExpectedJSONArray);
    }

    @Test
    public void testGetRequestNumberDateStatusFromJSON(){
        JSONObject data = new JSONObject();
        data.put("status","active");
        data.put("requestNumber","12");
        data.put("startDate","01.02.2019");

        ArrayList<String> actualList = ControllerHelper.getRequestNumberDateStatusFromJSON(data);
        String actualStatus = actualList.get(0);
        String actualRequestNumber = actualList.get(1);
        String actualStartDate = actualList.get(2);

        assertEquals(actualStartDate,"01.02.2019");
        assertEquals(actualRequestNumber,"12");
        assertEquals(actualStatus,"active");
    }

    @Test
    public void testGetBorrowRequestFields(){
        JSONObject data = new JSONObject();
        data.put("lenderID", 2);
        data.put("copyID",12);
        Lender actualLender = ControllerHelper.getBorrowRequestFields(data);
        assertEquals(actualLender.getcopyID(),"12");
        assertEquals(actualLender.getID(),"2");
        assertEquals(actualLender.getCity(),"");
    }

    @Test
    public void testProcessRequestStatus(){
        ArrayList<String> requestNumberDate = new ArrayList<String>();
        assertFalse(ControllerHelper.processRequestStatus("approd",requestNumberDate));

    }
}
