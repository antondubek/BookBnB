package server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserDatabaseLogic extends DatabaseLogic {

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
        } catch (NullPointerException e) {
            System.out.println("ERR: " + e);
        }
        return false;
    }

    /**
     * Find the user, by his email.
     * @param email email of the user to be searched
     * @return ArrayListOfUsers
     */
    public static ArrayList<User> findUser(String email){
        openTheConnection();
        ArrayList<User> data = new ArrayList<>();
        try (PreparedStatement statementToSearchUserByMail = con.prepareStatement(Query.USER_SEARCH_BY_EMAIL)){
            statementToSearchUserByMail.setString(1,email);
            ResultSet queryResults = statementToSearchUserByMail.executeQuery();
            data = getUsersFromResultSet(queryResults);
            con.close();
        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se); //
        }
        return data;
    }

    public static ArrayList<User> getUsersFromResultSet(ResultSet queryResults){
        ArrayList<User> users = new ArrayList<>();
        String[] namesOfFieldsInResponse = new String[]{"name", "email", "city"};
        ArrayList<String> data = getArrayListFromResultSet(queryResults, namesOfFieldsInResponse);

        for (int i = 0; i <= data.size()-namesOfFieldsInResponse.length; i+=namesOfFieldsInResponse.length){
            User nextUser = new User(data.get(i), data.get(i+1), data.get(i+2));
            users.add(nextUser);
        }
        return users;
    }

    /**
     * Registers a new user.
     * @param newUser username
     * @param password password (is hashed)
     * @return false if the connection is failed and the user is not registered
     */
    public static Boolean insertNewUser(User newUser, String password){
        if (!openTheConnection()){
            return false;
        }
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

    /**
     * Either follows or unfollows the user depending on query.
     * @param email of user
     * @param friendEmail email of user which needs to be followed or unfollowed
     * @param query query can be any query, usually follow or unfollow query
     * @return true if execuion done successfully
     */
    public static boolean manageFollow(String email, String friendEmail, String query){
        if (!openTheConnection()){
            return false;
        }
        try (PreparedStatement statementToFollow = con.prepareStatement(query)){
            statementToFollow.setString(1,email);
            statementToFollow.setString(2,friendEmail);
            statementToFollow.executeUpdate();

            con.close();
            return true;
        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se);
        }
        return false;
    }

    /**
     * Follow user
     * @param email email of user
     * @param friendEmail email of user to follow
     * @return true if followed successfully
     */
    public static boolean followPeople(String email, String friendEmail){
        return manageFollow(email, friendEmail, Query.FOLLOW_PEOPLE);
    }

    /**
     * Unfollow user
     * @param email email of user
     * @param friendEmail email of user to unfollow
     * @return true if unfollowed successfully
     */
    public static Boolean deleteFollow(String email, String friendEmail){
        return manageFollow(email, friendEmail, Query.DELETE_FOLLOW);
    }

    /**
     * Fetch the follows, by email of user.
     * @param email email of the user
     * @return ArrayListOfUsers Follows
     */
    public static ArrayList<User> fetchFollows(String email){
        openTheConnection();
        ArrayList<String> data = new ArrayList<>();
        ArrayList<User> user= new ArrayList<User>();
        try (PreparedStatement statementToSearchUserByMail = con.prepareStatement(Query.FETCH_FOLLOWS)){

            statementToSearchUserByMail.setString(1,email);

            ResultSet queryResults = statementToSearchUserByMail.executeQuery();
            user = getUsersFromResultSet(queryResults);
            con.close();
        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se); //
        }
        return user;
    }

    /**
     * Checks if User is follows another user or not.
     * @param email email of iuser
     * @param friendEmail email of user which we check if is followed or not
     * @return true if the friendEmail is folowed by email user.
     */
    public static Boolean userIsFollowed(String email, String friendEmail){
        if (!openTheConnection()){
            return false;
        }
        try (PreparedStatement statementToFollow = con.prepareStatement(Query.GET_IF_USER_IS_FOLLOWED)){
            statementToFollow.setString(1,email);
            statementToFollow.setString(2,friendEmail);
            ResultSet queryResult = statementToFollow.executeQuery();
            Integer numberOfElementsInResult = 0;
            if (queryResult != null)
            {
                queryResult.last();    // moves cursor to the last row
                numberOfElementsInResult = queryResult.getRow(); // get row id
            }
            con.close();
            if ( numberOfElementsInResult!= 0){
                return true;
            }
        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se);
        }
        return false;
    }

    /**
     * Update the request table in the DB so users can make requests to borrow books
     * @param email email of the borrower
     * @param lender Lender who owns the book
     * @return boolean showing the borrow request was made successfully
     */
    public static boolean requestToBorrow(String email, Lender lender) {
        openTheConnection();

        try (PreparedStatement requestToBorrow = con.prepareStatement(Query.REQUEST_TO_BORROW)){

            requestToBorrow.setString(1,email);                 //borrower's email
            requestToBorrow.setString(2,lender.getID());        //Lender's ID
            requestToBorrow.setString(3,lender.getcopyID());    //Lender's copy ID
            requestToBorrow.setString(4,lender.getcopyID());    //Lender's copy ID
            requestToBorrow.setString(5,"pending");    //set the status manually


            requestToBorrow.executeUpdate();

            con.close();
            return true;
        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se);
        }
        return false;
    }

    public static ArrayList<BorrowedBook> pendingRequestsToBorrow(String email) {
        ArrayList<BorrowedBook> pendingBorrowedBooks = new ArrayList<>();

        if(email == null || email.equals("")){
            return pendingBorrowedBooks;
        }

        openTheConnection();

        try (PreparedStatement statementToFetchLenders = con.prepareStatement(Query.SHOW_REQUESTS_TO_BORROW)){

            statementToFetchLenders.setString(1, email);
            System.out.println(statementToFetchLenders);

            ResultSet queryResults = statementToFetchLenders.executeQuery();
            pendingBorrowedBooks = getPendingBorrowedBooksFromResultSet(queryResults);

            con.close();
        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se);
        }


        return pendingBorrowedBooks;
    }

    private static ArrayList<BorrowedBook> getPendingBorrowedBooksFromResultSet(ResultSet queryResults){
        ArrayList<BorrowedBook> pendingBorrowedBooks = new ArrayList<>();
        String[] namesOfFieldsInResponse = new String[]{"ISBN", "title", "author", "status", "name"};

        ArrayList<String> data = getArrayListFromResultSet(queryResults, namesOfFieldsInResponse);

        for (int i = 0; i <= data.size()-namesOfFieldsInResponse.length; i+=namesOfFieldsInResponse.length){
            BorrowedBook nextPending = new BorrowedBook(data.get(i), data.get(i+1), data.get(i+2), data.get(i+3), data.get(i+4));
            System.out.println(data.get(i) + data.get(i+1) + data.get(i+2) + data.get(i+3) + data.get(i+4));

            pendingBorrowedBooks.add(nextPending);
        }

        System.out.println(pendingBorrowedBooks.toString());
        return pendingBorrowedBooks;
    }

    /**
     * Gets book objects from the ResultSet.
     * @param queryResults Query results for getting books from the database
     * @param pending True if books are for 1 user, false if all books should be returned
     * @return ArrayList of books
     */
//    public static ArrayList<BorrowedBook> getPendingBorrowedBooksFromResultSet(ResultSet queryResults, boolean pending){
//        ArrayList<Book> books = new ArrayList<>();
//        String[] namesOfFieldsInResponse;
//        if (pending){
//            namesOfFieldsInResponse = new String[]{"ISBN", "title", "author", "status", "name"};
//        } else {
//            namesOfFieldsInResponse = new String[]{"ISBN", "title", "author", "status", "name"};
//        }
//
//        ArrayList<String> data = getArrayListFromResultSet(queryResults, namesOfFieldsInResponse);
//
//        for (int i = 0; i <= data.size()-namesOfFieldsInResponse.length; i+=namesOfFieldsInResponse.length){
//            Book nextBook = new Book(data.get(i), data.get(i+1), data.get(i+2));
//            if (data.get(i+3) != null) {
//                nextBook.setEdition(data.get(i+3));
//            }
//
//            if (booksAreForUser && data.get(i+4) != null) {
//                nextBook.setAvailable(data.get(i+4));
//            }
//
//            if (booksAreForUser) {
//                nextBook.setCopyID(data.get(i+5));
//            }
//
//            books.add(nextBook);
//        }
////        return books;
//    }

}
