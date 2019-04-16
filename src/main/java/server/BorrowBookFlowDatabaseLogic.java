package server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class BorrowBookFlowDatabaseLogic extends DatabaseLogic {

    /**
     * Query to return all the available lenders of a particular book
     * @param ISBN the unique identifier for the book
     * @return an ArrayList of Lender objects representing users who are willing to lender the book
     */
    public static ArrayList<Lender> fetchAllLenders(String ISBN) {
        ArrayList<Lender> lenders = new ArrayList<>();
        if (ISBN == null || ISBN.equals("")) {
            return lenders;
        }

        openTheConnection();

        try (PreparedStatement statementToFetchLenders = con.prepareStatement(Query.GET_LENDERS)) {

            statementToFetchLenders.setString(1, ISBN);

            ResultSet queryResults = statementToFetchLenders.executeQuery();
            lenders = getLendersFromResultSet(queryResults);

            con.close();
        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se); //
        }
        return lenders;

    }

    /**
     * Method to extract the query results and create Lender objects to send back to the client. These lenders are
     * associated with a particular book.
     * @param queryResults results from the SELECT query
     * @return an arraylist of Lender objects
     */
    private static ArrayList<Lender> getLendersFromResultSet(ResultSet queryResults) {
        ArrayList<Lender> lenders = new ArrayList<>();
        String[] namesOfFieldsInResponse = new String[]{"Users_id", "name", "city", "loan_length", "copy_id"};

        ArrayList<String> data = getArrayListFromResultSet(queryResults, namesOfFieldsInResponse);

        for (int i = 0; i <= data.size() - namesOfFieldsInResponse.length; i += namesOfFieldsInResponse.length) {
            Lender nextLender = new Lender(data.get(i), data.get(i + 1), data.get(i + 2), data.get(i + 3), data.get(i + 4));

            lenders.add(nextLender);
        }
        return lenders;
    }

    /**
     * Sets the loan length
     * @param loanLength string containing the desired loan length the user wants to configure for their book
     * @param copyID the copyID of the book to update
     * @return true if the query was executed successfully
     */
    public static Boolean setLendingTerms(String loanLength, String copyID) {
        openTheConnection();

        try (PreparedStatement statementToSetLoanTerms = con.prepareStatement(Query.SET_LOAN_TERMS)) {

            statementToSetLoanTerms.setString(1, loanLength);
            statementToSetLoanTerms.setString(2, copyID);

            statementToSetLoanTerms.executeUpdate();
            con.close();
            return true;

        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se);
            return false;
        }
    }

    /**
     * A method to fetch the lending terms of a particular book from the database and send it back to the client
     * @param copyID of the book to fetch the lending terms from
     * @return a string containing the terms
     */
    public static String getLendingTerms(String copyID){
        openTheConnection();
        try (PreparedStatement statementToGetLoanTerms = con.prepareStatement(Query.GET_LOAN_TERMS)) {

            statementToGetLoanTerms.setString(1, copyID);

            ResultSet queryResults = statementToGetLoanTerms.executeQuery();
            ArrayList<String> result = getArrayListFromResultSet(queryResults,new String[]{"loan_length"});
            con.close();
            return result.get(0);

        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se);
            return null;
        } catch (ArrayIndexOutOfBoundsException e){
            System.out.println("SQL ERR: " + e);
            return null;
        }

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
     * @return an array list containing the borrowedbook objects
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


    /**
     * A method to process a loan request and update the database with the result
     * @param status a string containing the desired status of the book being requested, the status will be updated to
     *               the value of this string
     * @param requestNumberDateCopyId an arraylist of strings containing parameters needed for the update
     * @return a boolean, true if the update is successful, false if otherwise
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

    /**
     * A method to update the loan conditions after the loan has been approved. The purpose of this is to make the database
     * reflect the next start and end dates, and to show who the book is now loaned to. This method sets the placeholders
     * of a prepared statement and executes the update
     * @param updateLoanConditionsUponApproval preparedstatement containing the query to update
     * @param requestNumberDateCopyId an arraylist of strings containing parameters needed for the update
     * @throws SQLException an exception if the query fails
     * @throws ParseException an exception if the parsing fails
     */
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
}
