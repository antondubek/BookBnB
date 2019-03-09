package hello;

import java.sql.*;
import java.util.ArrayList;

/**
 * Class which is responsible for the database connections
 * @author 180029410
 */
public class Database {

    private static Connection con = null;
    //private static String url = "jdbc:mysql://dag8.host.cs.st-andrews.ac.uk/";
    private static String url = "jdbc:mysql://localhost:3307/";
    private static String db = "dag8_RickDB";
    private static String driver = "com.mysql.cj.jdbc.Driver";
    private static String user = "ri31";
    private static String pass = "33.1Z4HLNfnbuy";
    //private static Statement currentQuery;

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
     * Insert a book into the database, associate it with a particular user
     */
    public static Boolean insertNewBook(Book book, String email){
        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            return processInsertBook(book, email);
        } catch (Exception e) {
            System.out.println("ERR: " + e);
        }
        return false;
    }

    private static Boolean processInsertBook(Book book, String email) {
        try {
            Statement queryStatement = con.createStatement();

           if(!checkIfBookInDB(book, queryStatement)){
               String query1 = String.format(Query.insertNewBook, book.ISBN, book.title, book.author, book.edition);
               queryStatement.executeUpdate(query1);
           }

            String query2 = String.format(Query.addUserToBook, email, book.ISBN);
            queryStatement.executeUpdate(query2);

            con.close();
            return true;
        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se);
        }
        return false;
    }

    private static Boolean checkIfBookInDB(Book book, Statement queryStatement){
        try {
            String query = String.format(Query.checkIfBookInDB, book.ISBN);
            ResultSet queryResults = queryStatement.executeQuery(query);

            ArrayList<String> data = new ArrayList<>();
            while (queryResults.next()) {
                String ISBN = queryResults.getString("ISBN");
                data.add(ISBN);
            }

            return data.size() == 1;
        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se);
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

    private static ArrayList<Book> parseBooks(String email) { //TODO REFACTOR into smaller methods
        ArrayList<Book> data = new ArrayList<>();
        try {
            Statement queryStatement = con.createStatement();
            String query;

            if(email.equals("all")) {
                query = Query.fetchBooksBase;
            } else {
                query = String.format(Query.fetchUserBooks, email);
            }

            ResultSet queryResults = queryStatement.executeQuery(query);
            while (queryResults.next()) {
                String ISBN = queryResults.getString("ISBN");
                String title = queryResults.getString("title");
                String author = queryResults.getString("author");

                Book nextBook = new Book(ISBN, author, title);

                if(queryResults.getString("book_version") != null) {
                    nextBook.setEdition(queryResults.getString("book_version"));
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


    public static Boolean insertNewUser(User newUser, String password) {
        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            return processInsertionOfNewUser(newUser, password);
        } catch (Exception e) {
            System.out.println("ERR: " + e);
        }
        return false;
    }

    private static Boolean processInsertionOfNewUser(User newUser, String password){
        try {
            Statement queryStatement = con.createStatement();

            String query1 = String.format(Query.insertNewUser, newUser.name, newUser.email, newUser.city);
            queryStatement.executeUpdate(query1);

            String query2 = String.format(Query.insertNewUserPassword, newUser.email, password);
            queryStatement.executeUpdate(query2);

            con.close();
            return true;
        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se);
        }
        return false;
    }

}
