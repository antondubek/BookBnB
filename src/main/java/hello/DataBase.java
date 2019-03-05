package hello;

import java.sql.*;
import java.util.ArrayList;

/**
 * Class which is responsible for the database connections
 * @author 180029410
 */
public class DataBase {

    private static Connection con = null;
    //private static String url = "jdbc:mysql://dag8.host.cs.st-andrews.ac.uk/";
    private static String url = "jdbc:mysql://localhost:3307/";
    private static String db = "dag8_RickDB";
    private static String driver = "com.mysql.cj.jdbc.Driver";
    private static String user = "ri31";
    private static String pass = "33.1Z4HLNfnbuy";


    public static String userList() {
        StringBuilder data = new StringBuilder();
        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            data = parseQuery();
        } catch (Exception e) {
            System.out.println("ERR: " + e);
        }
        return data.toString(); //TODO for testing purposes, this could be problematic, as it can return an empty string builder
    }

    /**Parse query results one line at a time */
    private static StringBuilder parseQuery() {
        StringBuilder data = new StringBuilder();
        try {
            Statement queryStatement = con.createStatement();
            ResultSet queryResults = queryStatement.executeQuery(Query.allUserInformation);
            while (queryResults.next()) {
                String name = queryResults.getString("name");
                String email = queryResults.getString("email");
                String city = queryResults.getString("city");
                data.append(name + "with email " + email + "in city " + city +", ");
            }
            con.close();
        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se); //
        }
        return data;
    }

    public static String findUser(String email) {
        ArrayList<User> data = new ArrayList<>();
        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            data = getName(email);
        } catch (Exception e) {
            System.out.println("ERR: " + e);
        }
        return data.toString(); //TODO  this turn an arraylist of User obejcts into a string, needs to return JSON
    }

    public static ArrayList<User> getName(String name){
        ArrayList<User> data = new ArrayList<>();
        try {
            Statement queryStatement = con.createStatement();
            String query = String.format(Query.userSearchByName, name);
            ResultSet queryResults = queryStatement.executeQuery(query);
            while (queryResults.next()) {
                String username = queryResults.getString("name");
                String email = queryResults.getString("email");
                String city = queryResults.getString("city");

                User nextUser = new User(username, email, city);

                data.add(nextUser);
            }
            con.close();
        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se); //
        }
        return data;
    }

    public static String userObjects() {
        ArrayList<User> data = new ArrayList<>();
        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            data = parseQueryObjects();
        } catch (Exception e) {
            System.out.println("ERR: " + e);
        }
        return data.toString(); //TODO  this turn an arraylist of User obejcts into a string, needs to return JSON
    }

    /**Parse query results and turn into object */

    private static ArrayList<User> parseQueryObjects() {
        ArrayList<User> data = new ArrayList<>();
        try {
            Statement queryStatement = con.createStatement();
            ResultSet queryResults = queryStatement.executeQuery(Query.allUserInformation);
            while (queryResults.next()) {
                String name = queryResults.getString("name");
                String email = queryResults.getString("email");
                String city = queryResults.getString("city");

                User nextUser = new User(name, email, city);

                data.add(nextUser);
            }
            con.close();
        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se); //
        }
        return data;
    }


//RIAD's code listed below
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
                con.close();
            } catch (SQLException se) {
                 System.out.println("SQL ERR: " + se); //
            }
        } catch (Exception e) {
            System.out.println("ERR: " + e);
        }
        return returnData;
    }

    /**
     * AddTheNewUser
     */
    public static void addNewUser(String name){
        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            try {
                Statement st = con.createStatement();
                st.executeUpdate("INSERT INTO dag8_RickDB.test (name) Values ('" + name + "')");
                con.close();
            } catch (SQLException se) {
                 System.out.println("SQL ERR: " + se); //
            }
        } catch (Exception e) {
            System.out.println("ERR: " + e);
        }
    }

    /**
     * DeleteTheUser
     */
    public static void deleteTheUser(String name){
        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            try {
                Statement st = con.createStatement();
                st.executeUpdate("DELETE FROM dag8_RickDB.test WHERE name = '" + name + "'");
                con.close();
            } catch (SQLException se) {
                 System.out.println("SQL ERR: " + se); //
            }
        } catch (Exception e) {
            System.out.println("ERR: " + e);
        }
    }

    //UPDATE
    //st.executeUpdate("UPDATE dag8_RickDB.test SET name = 'Edwin' WHERE name = 'Edvin'");

}
