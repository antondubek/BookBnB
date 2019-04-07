package server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BookDatabaseLogic extends DatabaseLogic {

    /**
     * Insert a new book, opens a connection. Actual insertion is done in another method.
     * @param book to be added to the database
     * @param email user which adds the book
     * @return false if connection is failed
     */
    public static Boolean insertNewBook(Book book, String email){
        System.out.println(book);
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

    public static Boolean bookMissesDetails(Book book){
        return book.getAuthor().equals("") || book.getISBN().equals("") || book.getTitle().equals("");
    }

    /**
     * Inserts a book to the database
     * @param book to be added to the database
     * @return true if the book is added
     */
    public static Boolean processInsertBook(Book book) {
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



    public static Boolean addUserToBook(String email, Book book){
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



    /**
     * Checks if the book already exists in database.
     * @param book the book which is search in database
     * @return true if the book exists in database
     */
    public static Boolean checkIfBookInDB(Book book){
        ArrayList<String> data = getAllBooksByISBN(book.ISBN);
        return data.size() == 1;
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
     * Fetch all books from the database. Connection is opened.
     * @param email email of the user whose books should be shown, or "all" for all books to be shown
     * @return List of books
     */
    public static ArrayList<Book> fetchAllBooks(String email) {
        openTheConnection();
        ArrayList<Book> data = new ArrayList<>();

        String query = getQueryType(email);
        boolean booksAreForUser = !email.equals("all");

        try (PreparedStatement statementToFetchBooks = con.prepareStatement(query)){
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
            namesOfFieldsInResponse = new String[]{"ISBN", "title", "author", "book_version", "available", "copy_id"};
        } else {
            namesOfFieldsInResponse = new String[]{"ISBN", "title", "author", "book_version"};
        }

        ArrayList<String> data = getArrayListFromResultSet(queryResults, namesOfFieldsInResponse);

        for (int i = 0; i <= data.size()-namesOfFieldsInResponse.length; i+=namesOfFieldsInResponse.length){
            Book nextBook = new Book(data.get(i), data.get(i+1), data.get(i+2));
            if (data.get(i+3) != null) {
                nextBook.setEdition(data.get(i+3));
            }

            if (booksAreForUser && data.get(i+4) != null) {
                nextBook.setAvailable(data.get(i+4));
            }

            if (booksAreForUser) {
                nextBook.setCopyID(data.get(i+5));
            }

            books.add(nextBook);
        }
        return books;
    }

    /**
     * Update the 'available' parameter of of a user's book. This is done by taking the book's ISBN and the user's
     * email and running an UPDATE query
     * @param email
     * @param availability
     * @param ISBN
     * @param copyID
     * @return
     */
    public static Boolean updateBookAvailability(String email, Boolean availability, String ISBN, String copyID) {
        openTheConnection();

        try (PreparedStatement statementToUpdateAvailability = con.prepareStatement(Query.UPDATE_BOOK_AVAILABILITY)){


            statementToUpdateAvailability.setBoolean(1, availability);
            statementToUpdateAvailability.setString(2, ISBN);
            statementToUpdateAvailability.setString(3, email);
            statementToUpdateAvailability.setString(4, copyID);

            statementToUpdateAvailability.executeUpdate();

            con.close();

            return true;

        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se);
            return false;
        }
    }

    /**
     * Sets the loan length
     * @param loanLenght
     * @param copyID
     * @return true if the query was executed successfully
     */
    public static Boolean setLendingTerms(String loanLenght, String copyID) {
        openTheConnection();

        try (PreparedStatement statementToUpdateAvailability = con.prepareStatement(Query.SET_LOAN_TERMS)){

            statementToUpdateAvailability.setString(1, loanLenght);
            statementToUpdateAvailability.setString(2, copyID);

            statementToUpdateAvailability.executeUpdate();
            con.close();
            return true;

        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se);
            return false;
        }
    }

}
