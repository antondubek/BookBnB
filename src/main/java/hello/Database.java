package hello;

import java.sql.*;
import java.util.ArrayList;

/**
 * Class which is responsible for the database connections
 * @author 180029410
 */
public class Database {

    private static Connection con = null;
    private static String url = "jdbc:mysql://dag8.host.cs.st-andrews.ac.uk/";
    //private static String url = "jdbc:mysql://localhost:3307/";
    private static String db = "dag8_RickDB";
    private static String driver = "com.mysql.cj.jdbc.Driver";
    private static String user = "ri31";
    private static String pass = "33.1Z4HLNfnbuy";


    public static Boolean login(String password, String email) { //TODO REFACTOR - break down into smaller methods
        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);

            try {
                Statement queryStatement = con.createStatement();

                String query = String.format(Query.login, email);
                ResultSet queryResults = queryStatement.executeQuery(query);

                ArrayList<String> data = new ArrayList<>();
                while (queryResults.next()) {
                    String real_password = queryResults.getString("password");
                    data.add(real_password);
                }

                System.out.println(data.size());
                con.close();
                if (data.size() == 1) {
                    return password.equals(data.get(0));
                } else {
                    return false;
                }
            } catch (SQLException se) {
                System.out.println("SQL ERR: " + se);
            }
        } catch (Exception e) {
            System.out.println("ERR: " + e);
        }
        return false;
    }

    /**
     * Use this method and the next one to search for all books
     */
    public static ArrayList<Book> fetchAllBooks(String email) {
        ArrayList<Book> data = new ArrayList<>();
        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            data = parseBooks(email);
        } catch (Exception e) {
            System.out.println("ERR: " + e);
        }
        return data;
    }

    /**Parse query results and turn into object */

    private static ArrayList<Book> parseBooks(String email) {
        ArrayList<Book> data = new ArrayList<>();
        try {
            Statement queryStatement = con.createStatement();
            String query;

            if(email.equals("all")) {
                query = Query.fetchBooksBase;
            } else {
                query = String.format(Query.fetchUserBooks, email);
                System.out.println(query);
            }

            ResultSet queryResults = queryStatement.executeQuery(query);
            System.out.println("query successful");
            while (queryResults.next()) {
                String ISBN = queryResults.getString("ISBN");
                String title = queryResults.getString("title");
                String author = queryResults.getString("author");

                Book nextBook = new Book(ISBN, author, title);

                if(queryResults.getString("book_version") != null) {
                    nextBook.setVersion(queryResults.getString("book_version"));
                }

                if(!email.equals("all") && queryResults.getString("available") != null) {
                    nextBook.setAvailable(queryResults.getString("available"));
                }


                data.add(nextBook);
            }
            con.close();
        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se); //
        }
        return data;
    }


    /**
     * Use this method and the next one to search for a specific user
     */
    public static ArrayList<User> findUser(String email) {
        ArrayList<User> data = new ArrayList<>();
        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            data = getName(email);
        } catch (Exception e) {
            System.out.println("ERR: " + e);
        }
        return data;
    }

    public static ArrayList<User> getName(String email){
        ArrayList<User> data = new ArrayList<>();
        try {
            Statement queryStatement = con.createStatement();
            String query = String.format(Query.userSearchByEmail, email);
            ResultSet queryResults = queryStatement.executeQuery(query);
            while (queryResults.next()) {
                String name = queryResults.getString("name");
                String userEmail = queryResults.getString("email");
                String city = queryResults.getString("city");

                User nextUser = new User(name, userEmail, city);

                data.add(nextUser);
            }
            con.close();
        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se); //
        }
        return data;
    }

    /**
     * Use this method and the next one to fetch all users in the DB
     */
    public static String userObjects() {
        ArrayList<User> data = new ArrayList<>();
        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            data = parseQueryObjects();
        } catch (Exception e) {
            System.out.println("ERR: " + e);
        }
        return data.toString();
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

    //TODO Refactor into two methods
    public static Boolean insertNewUser(User new_user, String password) {
        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            try {
                Statement st = con.createStatement();

                String query1 = String.format(Query.insertNewUser, new_user.name, new_user.email, new_user.city);
                st.executeUpdate(query1);

                String query2 = String.format(Query.insertNewUserPassword, new_user.email, password);
                st.executeUpdate(query2);

                con.close();
                return true;
            } catch (SQLException se) {
                System.out.println("SQL ERR: " + se);
            }
        } catch (Exception e) {
            System.out.println("ERR: " + e);
        }
        return false;
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

}
