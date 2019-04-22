import org.junit.Test;
import org.junit.Before;
import server.Lender;

import static org.junit.Assert.*;

public class LenderTest {
    private Lender testLender;
    private String ID = "1";
    private String name = "Garnizzleman";
    private String city = "St. Andrews";
    private String loanLength = "7";
    private String copyID = "1";

    @Before
    public void Setup(){
        testLender = new Lender(ID, name, city, loanLength, copyID);
    }

    @Test
    public void testConstructor(){
        assertEquals(testLender.getID(), ID);
        assertEquals(testLender.getName(), name);
        assertEquals(testLender.getCity(), city);
        assertEquals(testLender.getLoanLength(), loanLength);
        assertEquals(testLender.getcopyID(), copyID);
    }

    @Test
    public void testToString() {
        String currentToString = "name: " + this.name + " loanLength: " + this.loanLength + " city: " + this.city;
        assertEquals(testLender.toString(), currentToString);
    }

    @Test
    public void testGettersAndSetters() {
        testLender.setID("2");
        testLender.setName("Ibo Banderas");
        testLender.setCity("London");
        testLender.setLoanLength("14");
        testLender.setCopyID("2");

        assertEquals(testLender.getID(), "2");
        assertEquals(testLender.getName(), "Ibo Banderas");
        assertEquals(testLender.getCity(), "London");
        assertEquals(testLender.getLoanLength(), "14");
        assertEquals(testLender.getcopyID(), "2");
    }
}
