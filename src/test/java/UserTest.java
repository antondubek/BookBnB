import org.junit.Test;
import org.junit.Before;
import server.User;

import static org.junit.Assert.*;


public class UserTest {
    private User testUser;
    private String name = "Garnizzleman";
    private String email = "garnizzleman@garnizzleman.com";
    private String city = "St. Andrews";

    @Before
    public void Setup(){
        testUser = new User(name, email, city);
    }

    @Test
    public void testConstructor(){
        assertEquals(testUser.getName(), name);
        assertEquals(testUser.getEmail(), email);
        assertEquals(testUser.getCity(), city);
    }

    @Test
    public void testGettersAndSetters() {
        testUser.setName("Pizza Man");
        testUser.setEmail("pizza@man.com");
        testUser.setCity("Castro");

        assertEquals(testUser.getName(), "Pizza Man");
        assertEquals(testUser.getEmail(), "pizza@man.com");
        assertEquals(testUser.getCity(), "Castro");
    }
}
