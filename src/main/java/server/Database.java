package server;

import com.sun.org.apache.xpath.internal.operations.Bool;

import javax.swing.text.StyledEditorKit;
import java.sql.*;
import java.util.ArrayList;

/**
 * Class which is responsible for the database connections
 * @author 180029410 & 180019489
 */
public class Database {

    private static Connection con = null;
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

    /**
     * Login method. Opens the connection, and calls helping method loginDetailsAreRight.
     * @param password password of the user, who logs in
     * @param email email of the user, who logs in
     * @return true if login is successful
     */
    public static Boolean loginIsSuccessful(String password, String email) {
        if (openTheConnection()){
            return loginDetailsAreRight(password, getDataFromDataBaseForLogin(email));
        }
        return false;

    }

    private static ArrayList<String> getDataFromDataBaseForLogin(String email){
        try (PreparedStatement statementForLogin = con.prepareStatement(Query.LOGIN)) {
            statementForLogin.setString(1, email);
            ResultSet queryResults = statementForLogin.executeQuery();
            String[] namesOfFieldsInResponse = new String[]{"password"};
            ArrayList<String> data = getArrayListFromResultSet(queryResults,namesOfFieldsInResponse);
            return data;
        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se);
        }
        return null;
    }

    /**
     * Helps loginIsSuccessful to do the login part, checks if the password of the user is right or not.
     * @param password
     * @return
     */
    public static Boolean loginDetailsAreRight(String password, ArrayList<String> data){
        try {
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

    public static Boolean bookMissesDetails(Book book){
        return book.getAuthor().equals("") || book.getISBN().equals("") || book.getTitle().equals("");
    }

    /**
     * Insert a new book, opens a connection. Actual insertion is done in another method.
     * @param book to be added to the database
     * @param email user which adds the book
     * @return false if connection is failed
     */
    public static Boolean insertNewBook(Book book, String email){
        if (bookMissesDetails(book)){
            return false;
        }
        if (openTheConnection()){
            try {
                Boolean bookInserted = processInsertBook(book);
                Boolean userAddedToBook = addUserToBook(email, book);
                con.close();
                return bookInserted && userAddedToBook;
            } catch (SQLException se) {
            System.out.println("SQL ERR: " + se);
            }
        }
        return false;
    }

    /**
     * Inserts a book to the database
     * @param book to be added to the database
     * @return true if the book is added
     */
    private static Boolean processInsertBook(Book book) {
        try (PreparedStatement statementToInsertBook = con.prepareStatement(Query.INSERT_NEW_BOOK)){

            if(!checkIfBookInDB(book)){
               statementToInsertBook.setString(1, book.getISBN());
               statementToInsertBook.setString(2, book.getTitle());
               statementToInsertBook.setString(3, book.getAuthor());
               statementToInsertBook.setString(4, book.getEdition());

               statementToInsertBook.executeUpdate();
            }
            return true;
        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se);
        }
        return false;
    }

    private static Boolean addUserToBook(String email, Book book){
        try (PreparedStatement statementToAddUserToBook = con.prepareStatement(Query.ADD_USER_TO_BOOK)){
            statementToAddUserToBook.setString(1, email);
            statementToAddUserToBook.setString(2, book.getISBN());

            statementToAddUserToBook.executeUpdate();
            return true;
        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se);
        }
        return false;
    }

    private static ArrayList<String> getAllBooksByISBN(String ISBN){
        try (PreparedStatement statementCheckIfBookInDB = con.prepareStatement(Query.CHECK_IF_BOOK_IN_DB)){
            statementCheckIfBookInDB.setString(1, ISBN);
            ResultSet queryResults = statementCheckIfBookInDB.executeQuery();
            String[] namesOfFieldsInResponse = new String[]{"ISBN"};
            return getArrayListFromResultSet(queryResults,namesOfFieldsInResponse);
        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se);
        }
        return null;
    }

    /**
     * Checks if the book already exists in database.
     * @param book the book which is search in database
     * @return true if the book exists in database
     */
    public static Boolean checkIfBookInDB(Book book){
        ArrayList<String> data = getAllBooksByISBN(book.ISBN);
        return data.size() == 1;
    }

    /**
     * gets right query type, depending on the request.
     * @param email email of the user whose books should be shown, or "all" for all books to be shown
     * @return String of the SQL Query.
     */
    public static String getQueryType(String email){
        String query = "";
        if (email.equals("all")){
            query = Query.FETCH_BOOKS_BASE;
        } else {
            query = Query.FETCH_USER_BOOKS;
        }
        return query;
    }

    /**
     * Gets book objects from the ResultSet.
     * @param queryResults Query results for getting books from the database
     * @param booksAreForUser True if books are for 1 user, false if all books should be returned
     * @return ArrayList of books
     */
    public static ArrayList<Book> getBookFromResultSet(ResultSet queryResults, boolean booksAreForUser){
        ArrayList<Book> books = new ArrayList<>();
        String[] namesOfFieldsInResponse;
        if (booksAreForUser){
            namesOfFieldsInResponse = new String[]{"ISBN", "title", "author", "book_version", "available"};
        } else {
            namesOfFieldsInResponse = new String[]{"ISBN", "title", "author", "book_version"};
        }

        ArrayList<String> data = getArrayListFromResultSet(queryResults, namesOfFieldsInResponse);

        for (int i = 0; i <= data.size()-namesOfFieldsInResponse.length; i+=namesOfFieldsInResponse.length){
            //System.out.println(data.get(i)+" "+data.get(i+1)+" "+data.get(i+2)+" "+data.get(i+3)+  " " +data.get(i+4));
            Book nextBook = new Book(data.get(i), data.get(i+1), data.get(i+2));
            if (data.get(i+3) != null) {
                nextBook.setEdition(data.get(i+3));
            }

            if (booksAreForUser && data.get(i+4) != null) {
                nextBook.setAvailable(data.get(i+4));
            }
            books.add(nextBook);
        }
        System.out.println("Query is finished");
        return books;
    }

    /**
     * Fetch all books from the database. Connection is opened.
     * @param email email of the user whose books should be shown, or "all" for all books to be shown
     * @return List of books
     */
    public static ArrayList<Book> fetchAllBooks(String email) {
        openTheConnection();
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
     * Find the user, by his email.
     * @param email email of the user to be searched
     * @return ArrayListOfUsers
     */
    public static ArrayList<User> findUser(String email) {
        ArrayList<User> data = new ArrayList<>();
        openTheConnection();
        data = getDetailsofTheUser(email);
        return data;
    }

    /**
     * Get's details of the user by executing the query and searching for the user by his email.
     * @param email email of the user to be searched
     * @return ArrayListOfUsers
     */
    public static ArrayList<User> getDetailsofTheUser(String email){
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

    /**
     * Registers a new user.
     * @param newUser username
     * @param password password (is hashed)
     * @return false if the connection is failed and the user is not registered
     */
    public static Boolean insertNewUser(User newUser, String password) {
        if (openTheConnection()){
            return processInsertionOfNewUser(newUser, password);
        }
        return false;
    }

    /**
     * Adds the user to the database.
     * @param newUser username
     * @param password password of the new user
     * @return true if the user is added to the database
     */
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
