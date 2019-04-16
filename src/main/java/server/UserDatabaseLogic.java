package server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
     * @param fetchFollowersOfUser a boolean that determines whether to fetch the followers of a given user (if true)
     *                             or to fetch the list of who a user follows (if false)
     * @return ArrayListOfUsers Follows
     */
    public static ArrayList<User> fetchFollows(String email, boolean fetchFollowersOfUser){
        openTheConnection();
        ArrayList<String> data = new ArrayList<>();
        ArrayList<User> user= new ArrayList<User>();
        try (PreparedStatement statementToSearchUserByMail = con.prepareStatement(queryToFetchWhoFollowsUser(fetchFollowersOfUser))){

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
     * This method decides whether to search for fetchFollowersOfUser or who the user is following
     * @param fetchFollowersOfUser a boolean, if true, want to use the query to fetch the list of who follows the user
     *                  if false want to get the list of users that this user is following
     * @return a string containing the query template
     */
    private static String queryToFetchWhoFollowsUser (boolean fetchFollowersOfUser){
        return (fetchFollowersOfUser) ? Query.FETCH_USERS_FOLLOWERS : Query.FETCH_WHO_USER_FOLLOWS;
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
            requestToBorrow.setString(5,"pending");         //set the status manually


            requestToBorrow.executeUpdate();

            con.close();
            return true;
        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se);
        }
        return false;
    }

    /**
     * Fetches information pertaining to the books a user has requested to borrow or is borrowing
     * @param email the user's email
     * @param requestBorrowedBooks a boolean that if true, returns the books you have requested to borrow, if false
     *                             will return the books that others have requested to borrow off you
     * @return
     */
    public static ArrayList<BorrowedBook> booksRequestedToBorrowOrLoan(String email, boolean requestBorrowedBooks) {
        ArrayList<BorrowedBook> pendingBorrowedBooks = new ArrayList<>();

        if(email == null || email.equals("")){
            return pendingBorrowedBooks;
        }

        openTheConnection();

        try (PreparedStatement statementToFetchBooksToBorrowOrLoan = con.prepareStatement(loanOrBorrowQuery(requestBorrowedBooks))){

            statementToFetchBooksToBorrowOrLoan.setString(1, email);

            ResultSet queryResults = statementToFetchBooksToBorrowOrLoan.executeQuery();
            pendingBorrowedBooks = getBorrowedOrLoanedBooksFromResultSet(queryResults);

            con.close();
        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se);
        }


        return pendingBorrowedBooks;
    }

    /**
     * Determines whether to call the query for books a user is borrowing and wants to borrow or books the user
     * is loaning and/or has been requested to lend
     * @param requestBorrowedBooks a boolean that is true if looking for borrowed book, false if for loaned books
     * @return a string containing a query
     */
    private static String loanOrBorrowQuery(boolean requestBorrowedBooks) {
        return (requestBorrowedBooks) ? Query.BORROW_REQUESTS : Query.LOAN_REQUESTS;
    }


    /**
     * Gets BorrowedBook objects from the ResultSet.
     * @param queryResults Query results for getting BorrowedBooks from the database
     * @return ArrayList of BorrowedBooks
     */
    private static ArrayList<BorrowedBook> getBorrowedOrLoanedBooksFromResultSet(ResultSet queryResults){
        ArrayList<BorrowedBook> borrowedOrLoanedBooks = new ArrayList<>();
        String[] namesOfFieldsInResponse = new String[]{"ISBN", "title", "author", "status", "person_of_interest", "loan_start", "loan_end", "request_number", "copy_id"};

        ArrayList<String> data = getArrayListFromResultSet(queryResults, namesOfFieldsInResponse);

        for (int i = 0; i <= data.size()-namesOfFieldsInResponse.length; i+=namesOfFieldsInResponse.length){
            BorrowedBook nextBorrowedOrLoaned = new BorrowedBook(data.get(i), data.get(i+1), data.get(i+2), data.get(i+3), data.get(i+4));
            if (data.get(i+5) == null) {
                nextBorrowedOrLoaned.setStartDate("");
                nextBorrowedOrLoaned.setEndDate("");
                nextBorrowedOrLoaned.setRequestNumber(data.get(i+7));
                nextBorrowedOrLoaned.setCopyID(data.get(8));
            } else {
                nextBorrowedOrLoaned.setStartDate(data.get(i+5));
                nextBorrowedOrLoaned.setEndDate(data.get(i+6));
                nextBorrowedOrLoaned.setRequestNumber(data.get(i+7));
                nextBorrowedOrLoaned.setCopyID(data.get(8));
            }

            borrowedOrLoanedBooks.add(nextBorrowedOrLoaned);
        }
       return borrowedOrLoanedBooks;
    }

    //TODO REFACTOR THIS METHOD
    /**
     *
     * @param status
     * @param requestNumberDateCopyId
     * @return
     */
    public static boolean processApprovalOrDenialOfBorrowRequest(String status, ArrayList<String> requestNumberDateCopyId) {
        openTheConnection();

        try (PreparedStatement updateBorrowRequest = con.prepareStatement(Query.UPDATE_BORROW_REQUEST);
             PreparedStatement updateLoanConditionsUponApproval = con.prepareStatement(Query.UPDATE_FOR_NEW_LOAN)){

            updateBorrowRequest.setString(1, status);
            updateBorrowRequest.setInt(2, Integer.parseInt(requestNumberDateCopyId.get(1)));

            updateBorrowRequest.executeUpdate();

            if(status.equals("approved")) {
                approveRequest(updateLoanConditionsUponApproval, requestNumberDateCopyId);
            }

            con.close();
            return true;
        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void approveRequest(PreparedStatement updateLoanConditionsUponApproval, ArrayList<String> requestNumberDateCopyId) throws SQLException, ParseException {

        updateLoanConditionsUponApproval.setInt(1, Integer.parseInt(requestNumberDateCopyId.get(1)));

        java.util.Date start_date = new SimpleDateFormat("yyyy-MM-dd").parse(requestNumberDateCopyId.get(2));
        java.sql.Date loanStartInSQL = new java.sql.Date(start_date.getTime());

        updateLoanConditionsUponApproval.setDate(2, loanStartInSQL);
        updateLoanConditionsUponApproval.setInt(3, Integer.parseInt(requestNumberDateCopyId.get(1)));

        updateLoanConditionsUponApproval.executeUpdate();
    }

    /**
     * This method updates the database when the user wants to recall one of the books the have lent or check back in
     * a book they lent which has now been returned. While these are two separate actions, the database logic to handle
     * them is almost identical so they were combined into one.
     * @param requestNumber a string containing the request number associated with this loan
     * @param returnBook a boolean that if true, means the book is being returned, not recalled
     * @return a boolean, true if the database update was successful, false if otherwise
     */
    public static boolean returnOrRecallBook(String requestNumber, boolean returnBook) {
        openTheConnection();

        try(PreparedStatement recallBookQuery = con.prepareStatement(Query.RETURN_OR_RECALL_BOOK);
            PreparedStatement updateStatus = con.prepareStatement(Query.UPDATE_STATUS_AT_END_OF_LOAN)){

            recallBookQuery.setInt(1, Integer.parseInt(requestNumber));
            recallBookQuery.executeUpdate();

            String status = (returnBook) ? "returned" : "recalled";

            updateStatus.setString(1, status);
            updateStatus.setInt(2, Integer.parseInt(requestNumber));
            updateStatus.executeUpdate();

            con.close();
            return true;

        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se);
        }
        return false;
    }

    /**
     *
     * @param email
     * @return
     */
    public static String getAverageRating(String email){
        openTheConnection();

        try (PreparedStatement statementToGetRating = con.prepareStatement(Query.AVERAGE_USER_REPUTATION)){

            statementToGetRating.setString(1, email);

            ResultSet queryResults = statementToGetRating.executeQuery();
            String averageRating = getArrayListFromResultSet(queryResults,new String[]{"AVG(rating)"}).get(0);
            con.close();
            return averageRating;
        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se); //
        }
        return "";
    }

    /**
     *
     * @param borowerEmail
     * @param lenderEmail
     * @param rating
     * @param review
     * @return
     */
    public static Boolean setReputation(String borowerEmail, String lenderEmail, Integer rating,String review){
        openTheConnection();

        try (PreparedStatement statementToSetRating = con.prepareStatement(Query.SET_USER_REPUTATION)){

            statementToSetRating.setString(1, lenderEmail);
            statementToSetRating.setString(2, borowerEmail);
            statementToSetRating.setInt(3, rating);
            statementToSetRating.setString(4, review);
            statementToSetRating.setDate(5, getCurrentDate());

            statementToSetRating.executeUpdate();
            con.close();
            return true;
        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se); //
        }
        return false;
    }

}
