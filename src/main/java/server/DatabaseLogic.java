package server;

import com.sun.org.apache.xpath.internal.operations.Bool;

import javax.swing.text.StyledEditorKit;
import java.sql.*;
import java.util.ArrayList;

/**
 * Class which is responsible for the database connections
 * @author 180029410 & 180019489
 */
public class DatabaseLogic {

    protected static Connection con = null;
    private static String url = "jdbc:mysql://dag8.host.cs.st-andrews.ac.uk/";
    //private static String url = "jdbc:mysql://localhost:3307/";
    private static String db = "dag8_RickDB";
    private static String driver = "com.mysql.cj.jdbc.Driver";
    private static String user = "ri31";
    private static String pass = "33.1Z4HLNfnbuy";


    public static Boolean openTheConnection(){
        try{
            Class.forName(driver);
            con = DriverManager.getConnection(url + db, user, pass);
            return true;
        } catch (Exception e) {
            System.out.println("ERR: " + e);
        }
        return false;
    }

    public static void setConnection(Connection connection){
        con = connection;
    }
    /**
     * Gets ArrayList of String from the ResultSet. Useful, if read String sequence from database.
     * @param queryResults Results of the query
     * @param namesOfFields Names of the variables from database, which needs to be read
     * @return ArrayList of all Strings from the query
     */
    public static ArrayList<String> getArrayListFromResultSet(ResultSet queryResults,String[] namesOfFields){
        ArrayList<String> data = new ArrayList<>();
        try {
            while (queryResults.next()) {
                for (String fieldName : namesOfFields){
                    String element = queryResults.getString(fieldName);
                    data.add(element);
                }
            }
        } catch (SQLException se){
            System.out.println("SQL ERR: " + se);
        }
        return data;
    }

}
