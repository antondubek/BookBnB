package hello;

import java.sql.*;
/**
 * Class which is responsible for the database connections
 * @author 180029410
 */
public class DataBase {

    private static Connection con = null;
    private static String url = "jdbc:mysql://dag8.host.cs.st-andrews.ac.uk/";
    private static String db = "dag8_RickDB";
    private static String driver = "com.mysql.cj.jdbc.Driver";
    private static String user = "ri31";
    private static String pass = "33.1Z4HLNfnbuy";

    /**
     * Get all the data from the table.
     * @param tableName the name of the table
     */
    public static String getData(String tableName) {
        String returnData = "";
        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            
            try {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM " + tableName);
                while (rs.next()) {
                    String name = rs.getString("name");
                    returnData += name + "\n";
                }

                //ADD User
                //st.executeUpdate("INSERT INTO dag8_RickDB.test (name) Values ('Praneeth'), ('John'); ");


                //DELETE
                //st.executeUpdate("DELETE FROM dag8_RickDB.test WHERE name = 'John'");

                //UPDATE
                //st.executeUpdate("UPDATE dag8_RickDB.test SET name = 'Edwin' WHERE name = 'Edvin'");
                con.close();
            } catch (SQLException se) {
                 System.out.println("SQL ERR: " + se); //
            }
        } catch (Exception e) {
            System.out.println("ERR: " + e);
        }
        return returnData;
    }
}