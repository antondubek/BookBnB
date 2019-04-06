import org.json.JSONObject;
import org.junit.Test;
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
    public void testGetEmailToFetchFollowers(){
        String json = "{\"email\":\"riadibadulla@gmail.com\"}\n";
        assertEquals(ControllerHelper.getEmailToFetchFollowers(json), "riadibadulla@gmail.com");

        String json1 = "{\"emai\":\"riadibadulla@gmail.com\"}\n";
        assertEquals(ControllerHelper.getEmailToFetchFollowers(json1), "");
    }
}
