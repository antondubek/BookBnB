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


    public static Boolean loginIsSuccessful(String password, String email) {
        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            return loginProcedure(password, email);

        } catch (Exception e) {
            System.out.println("ERR: " + e);
        }
        return false;
    }

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

    public static Boolean loginProcedure(String password, String email){
        try (PreparedStatement statementForLogin = con.prepareStatement(Query.LOGIN)) {
            statementForLogin.setString(1, email);
            ResultSet queryResults = statementForLogin.executeQuery();

            String[] namesOfFieldsInResponse = new String[]{"password"};
            ArrayList<String> data = getArrayListFromResultSet(queryResults,namesOfFieldsInResponse);

            con.close();
            if (data.size() == 1) {
                return password.equals(data.get(0));
            } else {
                return false;
            }
        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se);
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
        try (PreparedStatement statementToInsertBook = con.prepareStatement(Query.INSERT_NEW_BOOK);
             PreparedStatement statementToAddUserToBook = con.prepareStatement(Query.ADD_USER_TO_BOOK)){
            //Statement queryStatement = con.createStatement();

            if(!checkIfBookInDB(book)){
               statementToInsertBook.setString(1, book.getISBN());
               statementToInsertBook.setString(2, book.getTitle());
               statementToInsertBook.setString(3, book.getAuthor());
               statementToInsertBook.setString(4, book.getEdition());

               statementToInsertBook.executeUpdate();
            }

            statementToAddUserToBook.setString(1, email);
            statementToAddUserToBook.setString(2, book.getISBN());

            statementToAddUserToBook.executeUpdate();

            con.close();
            return true;
        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se);
        }
        return false;
    }

    public static Boolean checkIfBookInDB(Book book){
        try (PreparedStatement statementCheckIfBookInDB = con.prepareStatement(Query.CHECK_IF_BOOK_IN_DB)){
            statementCheckIfBookInDB.setString(1, book.getISBN());
            ResultSet queryResults = statementCheckIfBookInDB.executeQuery();

            String[] namesOfFieldsInResponse = new String[]{"ISBN"};
            ArrayList<String> data = getArrayListFromResultSet(queryResults,namesOfFieldsInResponse);

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

    public static String getQueryType(String email){
        String query = "";
        if (email.equals("all")){
            query = Query.FETCH_BOOKS_BASE;
        } else {
            query = Query.FETCH_USER_BOOKS;
        }
        return query;
    }


    public static ArrayList<Book> getBookFromResultSet(ResultSet queryResults, boolean booksAreForUser){
        ArrayList<Book> data = new ArrayList<>();
        try {
            while (queryResults.next()) {
                String ISBN = queryResults.getString("ISBN");
                String title = queryResults.getString("title");
                String author = queryResults.getString("author");

                Book nextBook = new Book(ISBN, author, title);

                if (queryResults.getString("book_version") != null) {
                    nextBook.setEdition(queryResults.getString("book_version"));
                }

                if (booksAreForUser && queryResults.getString("available") != null) {
                    nextBook.setAvailable(queryResults.getString("available"));
                }

                data.add(nextBook);
            }
        } catch (SQLException se){
            System.out.println("SQL ERR: " + se);
        }
        return data;
    }

    /**Parse query results and turn into object */

    private static ArrayList<Book> parseBooks(String email) { //TODO REFACTOR into smaller methods
        ArrayList<Book> data = new ArrayList<>();

        String query = getQueryType(email);
        boolean booksAreForUser = !email.equals("all");

        try (PreparedStatement statementToFetchBooks = con.prepareStatement(query)
        ){
            if (booksAreForUser){
                statementToFetchBooks.setString(1, email);
            }

            ResultSet queryResults = statementToFetchBooks.executeQuery();
            data = getBookFromResultSet(queryResults, booksAreForUser);

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
        try (PreparedStatement statementToSerachUserByMail = con.prepareStatement(Query.USER_SEARCH_BY_EMAIL)){
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
        try (PreparedStatement statementToInsertUser = con.prepareStatement(Query.INSERT_NEW_USER);
             PreparedStatement statementToInsertPassword = con.prepareStatement(Query.INSERT_NEW_USER_PASSWORD)){

            statementToInsertUser.setString(1,newUser.getName());
            statementToInsertUser.setString(2,newUser.getEmail());
            statementToInsertUser.setString(3,newUser.getCity());

            statementToInsertUser.executeUpdate();

            statementToInsertPassword.setString(1,newUser.getEmail());
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
