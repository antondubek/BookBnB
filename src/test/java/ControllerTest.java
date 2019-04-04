import org.json.JSONObject;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.junit.Test;
import server.Controller;
import server.User;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.util.ArrayList;
import java.util.Arrays;

public class ControllerTest {

    @Test
    public void testGetPasswordFromJson(){
        JSONObject jsonObj1 = new JSONObject();
        jsonObj1.put("password", "4672937");

        JSONObject jsonObj2 = new JSONObject();
        jsonObj2.put("pasrd", "password");

        assertEquals(Controller.getPasswordFromJson(jsonObj1), "4672937");
        assertEquals(Controller.getPasswordFromJson(jsonObj2), "");
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

        User testingUser1 = Controller.getUserFromJSON(jsonObj1);
        assertEquals(testingUser1.getEmail(), "email1231@mail.ru");
        assertEquals(testingUser1.getName(), "name1");
        assertEquals(testingUser1.getCity(), "London");

        assertNull(Controller.getUserFromJSON(jsonObj2));
    }

    @Test
    public void testGetFollowFields(){
        String[] followFields1 = {"test@amakepeace.com","riadibadulla@gmail.com"};
        JSONObject jsonObj1 = new JSONObject();
        jsonObj1.put("email", "test@amakepeace.com");
        jsonObj1.put("friendEmail", "riadibadulla@gmail.com");
        assertEquals(Controller.getFollowFields(jsonObj1), followFields1);

    }
}
