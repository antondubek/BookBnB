package hello;

import java.sql.*;
import java.util.ArrayList;

/**
 * Class which is responsible for the database connections
 * @author 180029410
 * @modified by 180019489
 */
public class Database {

    private static Connection con = null;
    private static String url = "jdbc:mysql://dag8.host.cs.st-andrews.ac.uk/";
    //private static String url = "jdbc:mysql://localhost:3307/";
    private static String db = "dag8_RickDB";
    private static String driver = "com.mysql.cj.jdbc.Driver";
    private static String user = "ri31";
    private static String pass = "33.1Z4HLNfnbuy";
    //private static Statement currentQuery;

    public static Boolean login(String password, String email) { //TODO REFACTOR - break down into smaller methods
        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);

            try (PreparedStatement prepared = con.prepareStatement(Query.login)) {
                //Statement queryStatement = con.createStatement();
                prepared.setString(1, email);
                //String query = String.format(Query.login, email);
                //ResultSet queryResults = queryStatement.executeQuery(query);
                ResultSet queryResults = prepared.executeQuery();

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
        try (PreparedStatement statementToInsertBook = con.prepareStatement(Query.insertNewBook);
             PreparedStatement statementToAddUserToBook = con.prepareStatement(Query.addUserToBook)){
            //Statement queryStatement = con.createStatement();


            if(!checkIfBookInDB(book)){
               statementToInsertBook.setString(1, book.ISBN);
               statementToInsertBook.setString(2, book.title);
               statementToInsertBook.setString(3, book.author);
               statementToInsertBook.setString(4, book.edition);

               statementToInsertBook.executeUpdate();
            }

            statementToAddUserToBook.setString(1, email);
            statementToAddUserToBook.setString(2, book.ISBN);

            statementToAddUserToBook.executeUpdate();

            con.close();
            return true;
        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se);
        }
        return false;
    }

    private static Boolean checkIfBookInDB(Book book){
        try (PreparedStatement statementCheckIfBookInDB = con.prepareStatement(Query.checkIfBookInDB)){
            statementCheckIfBookInDB.setString(1, book.ISBN);
            //String query = String.format(Query.checkIfBookInDB, book.ISBN);
            ResultSet queryResults = statementCheckIfBookInDB.executeQuery();

            ArrayList<String> data = new ArrayList<>();
            while (queryResults.next()) {
                String ISBN = queryResults.getString("ISBN");
                data.add(ISBN);
                System.out.println("checking if book exists");
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

        String query = "";

        try (PreparedStatement statementToFetchAllBooks = con.prepareStatement(Query.fetchBooksBase);
             PreparedStatement statementToFetchUserBooks = con.prepareStatement(Query.fetchUserBooks)
        ){
            statementToFetchUserBooks.setString(1, email);
            ResultSet queryResults;

            if (email.equals("all")){
                queryResults = statementToFetchAllBooks.executeQuery();
            } else {
                queryResults = statementToFetchUserBooks.executeQuery();
            }

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
        try (PreparedStatement statementToSerachUserByMail = con.prepareStatement(Query.userSearchByEmail)){
            statementToSerachUserByMail.setString(1,email);
            ResultSet queryResults = statementToSerachUserByMail.executeQuery();
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
        try (PreparedStatement statementToInsertUser = con.prepareStatement(Query.insertNewUser);
             PreparedStatement statementToInsertPassword = con.prepareStatement(Query.insertNewUserPassword)){

            statementToInsertUser.setString(1,newUser.name);
            statementToInsertUser.setString(2,newUser.email);
            statementToInsertUser.setString(3,newUser.city);

            statementToInsertUser.executeUpdate();

            statementToInsertPassword.setString(1,newUser.email);
            statementToInsertPassword.setString(2,password);

            statementToInsertPassword.executeUpdate();

            con.close();
            return true;
        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se);
        }
        return false;
    }

}
