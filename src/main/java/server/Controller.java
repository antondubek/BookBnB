package server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Controller class, responsible for the connection with the clients.
 */
@RestController
public class Controller {

    @RequestMapping(method = RequestMethod.GET, value="/alive")
    public ResponseEntity<String> alive(@RequestParam(value="command", defaultValue = "none") String command){
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    /**
     * Register request method.
     * @param jsonString json contains fields of the user
     * @return response if the registration is successful or not.
     */
    @RequestMapping(method= RequestMethod.POST, value = "/register")
    public ResponseEntity<String> register(@RequestBody String jsonString){

        JSONObject data = new JSONObject(jsonString);
        User newUser = ControllerHelper.getUserFromJSON(data);
        String password = ControllerHelper.getPasswordFromJson(data);

        Boolean insert = UserDatabaseLogic.insertNewUser(newUser, password);

        return (insert) ? new ResponseEntity<String>(HttpStatus.OK) : new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Login request method
     * @param jsonString gets email and password to login
     * @return status if login was successful
     */
    @RequestMapping(method= RequestMethod.POST, value = "/login")
    public ResponseEntity<String> login(@RequestBody String jsonString){

        JSONObject data = new JSONObject(jsonString);

        String email = ControllerHelper.getEmailFromJson(data);
        String password = ControllerHelper.getPasswordFromJson(data);

        return (UserDatabaseLogic.loginIsSuccessful(password, email)) ? new ResponseEntity<String>(HttpStatus.OK) : new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }

    /**
     * get books request method, GET request
     * @param command command which table of books to send
     * @return books for user or all books depending on request
     */
    @RequestMapping(method = RequestMethod.GET, value="/book")
    public String allBooks(@RequestParam(value="command", defaultValue = "none") String command){
        if(!command.equals("all")){
            return "error";
        }
        ArrayList<Book> books = BookDatabaseLogic.fetchAllBooks("all");
        return ControllerHelper.getJSONBooks(books).toString();
    }

    /**
     * Mapping to find all the available lenders of a particular book
     * @param ISBN unique identifier of the book, used to find available lenders
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value="/book/lenders")
    public String allLendersOfBook(@RequestParam(value="ISBN", defaultValue = "none") String ISBN){

        ArrayList<Lender> Lenders = BookDatabaseLogic.fetchAllLenders(ISBN);
        return ControllerHelper.getJSONLenders(Lenders).toString();
    }

    /**
     * Searching for the user. if user is not found, returns user with empty fields.
     * @param jsonString
     * @return users
     */
    @RequestMapping(method= RequestMethod.POST, value = "/profile")
    public String loadProfile(@RequestBody String jsonString) {
        JSONObject data = new JSONObject(jsonString);
        String email = ControllerHelper.getEmailFromJson(data);
        ArrayList<User> user = UserDatabaseLogic.findUser(email);
        User specificUser = ControllerHelper.getUserFromArrayList(user);

        return ControllerHelper.createJSONFromUser(specificUser);
    }

    /**
     * get all books for the user
     * @param jsonString
     * @return
     */
    @RequestMapping(method= RequestMethod.POST, value = "/profile/books")
    public String loadUserBooks(@RequestBody String jsonString) {
        JSONObject data = new JSONObject(jsonString);
        String email = ControllerHelper.getEmailFromJson(data);

        ArrayList<User> user = UserDatabaseLogic.findUser(email);
        if(user.size() != 1){
            return "No user found with this email address";
        }

        User specificUser = ControllerHelper.getUserFromArrayList(user);

        ArrayList<Book> books = BookDatabaseLogic.fetchAllBooks(specificUser.getEmail());

        return ControllerHelper.getJSONBooks(books).toString();
    }

    /**
     * Update whether or not a book is available for lending. If update successful, send STATUS OK, otherwise send 404
     * @param jsonString JSON passed in the request body, contains "email", "available", "ISBN", "copyID"
     * @return HttpStatus Ok if updated successfully
     */
    @RequestMapping(method= RequestMethod.POST, value = "/profile/books/availability")
    public ResponseEntity<String> updateBookAvailability(@RequestBody String jsonString) {
        JSONObject data = new JSONObject(jsonString);

        String email = ControllerHelper.getEmailFromJson(data);
        Boolean currentAvailability = ControllerHelper.getAvailabilityFromJSON(data);
        String ISBN = data.get("ISBN").toString();
        String copyID   = data.get("copyID").toString();

        Boolean updatedAvailability = ControllerHelper.updateAvailability(email, currentAvailability, ISBN, copyID);
        return (updatedAvailability) ? new ResponseEntity<String>(HttpStatus.OK) : new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }

    /**
     * RequestMapping to follow the user
     * @param jsonString contains "email" and "friendEmail" which it needs to follow
     * @return (HttpStatus.OK) if followed successfully
     */
    @RequestMapping(method= RequestMethod.POST, value = "/follow")
    public ResponseEntity<String> follow(@RequestBody String jsonString) {
        JSONObject data = new JSONObject(jsonString);

        String[] followFields = ControllerHelper.getFollowFields(data);
        Boolean followSuccessful;
        if (!followFields[0].equals((followFields[1]))) {
            followSuccessful = UserDatabaseLogic.followPeople(followFields[0], followFields[1]);
        } else {
            followSuccessful = false;
        }
        return (followSuccessful) ? new ResponseEntity<String>(HttpStatus.OK) : new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }

    /**
     * RequestMapping to unfollow the user
     * @param jsonString contains "email" and "friendEmail" which it needs to unfollow
     * @return (HttpStatus.OK) if unfollowed successfully
     */
    @RequestMapping(method= RequestMethod.POST, value = "/follow/delete")
    public ResponseEntity<String> deleteFollow(@RequestBody String jsonString) {
        JSONObject data = new JSONObject(jsonString);

        String[] followFields = ControllerHelper.getFollowFields(data);
        Boolean followSuccesfull = UserDatabaseLogic.deleteFollow(followFields[0], followFields[1]);
        return (followSuccesfull) ? new ResponseEntity<String>(HttpStatus.OK) : new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }

    /**
     * RequestMapping to check if the user is followed by another user
     * @param jsonString contains "email" and "friendEmail" which it check if is followed or not
     * @return JSon object string with field userIsFollowed, true if the user follows friendEmail
     */
    @RequestMapping(method= RequestMethod.POST, value = "/follow/true")
    public Map<String, Boolean> getIsFollowed(@RequestBody String jsonString) {
        JSONObject data = new JSONObject(jsonString);
        String[] followFields = ControllerHelper.getFollowFields(data);
        Boolean isFollowed = UserDatabaseLogic.userIsFollowed(followFields[0], followFields[1]);
        return Collections.singletonMap("userIsFollowed", isFollowed);
    }

    /**
     * Fetches the user's followers (people who follow that user) from the db.
     * @param jsonString contains "email" of the user
     * @return JSON with users
     */
    @RequestMapping(method= RequestMethod.POST, value = "/follow/fetch")
    public String getFollows(@RequestBody String jsonString) {
        String email = ControllerHelper.getEmailToFetchFollowers(jsonString);
        if (email.equals("")){
            return email;
        }

        ArrayList<User> emailsOfFollows = UserDatabaseLogic.fetchFollows(email, true);
        ArrayList<String> JSONFollows = ControllerHelper.getJSONFollows(emailsOfFollows);

        return JSONFollows.toString();
    }

    /**
     * Fetches from the database the list of all users who the user follows (people who that user follows)
     * @param jsonString contains "email" of the user
     * @return JSON with users
     */
    @RequestMapping(method= RequestMethod.POST, value = "/follow/following")
    public String getWhoUserIsFollowing(@RequestBody String jsonString) {
        String email = ControllerHelper.getEmailToFetchFollowers(jsonString);
        if (email.equals("")){
            return email;
        }

        ArrayList<User> emailsOfFollows = UserDatabaseLogic.fetchFollows(email, false);
        ArrayList<String> JSONFollows = ControllerHelper.getJSONFollows(emailsOfFollows);

        return JSONFollows.toString();
    }

    /**
     * Add book request method
     * @param jsonString
     * @return ok if the book is added to the database successfully
     */
    @RequestMapping(method= RequestMethod.POST, value = "/profile/addBook")
    public ResponseEntity<String> addBook(@RequestBody String jsonString) {
        JSONObject data = new JSONObject(jsonString);
        String email = ControllerHelper.getEmailFromJson(data);
        Book newBook = ControllerHelper.getBookFromJSON(data);
        Boolean insert = BookDatabaseLogic.insertNewBook(newBook, email);
        return (insert) ? new ResponseEntity<String>(HttpStatus.OK) : new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }

    /**
<<<<<<< HEAD
     * Mapping for a user to request to borrow a book
     * @param jsonString containing information needed for a user to request to borrow a book from a lender
     * @return ok if the request to borrow was made successfully, error if otherwise
     */
    @RequestMapping(method= RequestMethod.POST, value = "/request")
    public ResponseEntity<String> requestToBorrow(@RequestBody String jsonString) {
        JSONObject data = new JSONObject(jsonString);
        String email = ControllerHelper.getEmailFromJson(data);
        Lender requestForSpecificLender = ControllerHelper.getBorrowRequestFields(data);
        Boolean insert = UserDatabaseLogic.requestToBorrow(email, requestForSpecificLender);
        return (insert) ? new ResponseEntity<String>(HttpStatus.OK) : new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }

    /**
     * This returns the list of all lenders that a user is borrowing from along with some information about what is
     * being borrow
     * @param jsonString JSON containing parameters needed for getting this information
     * @return a list of all lenders that a user is borrowing from plus some other key information
     */
    @RequestMapping(method = RequestMethod.POST, value = "/request/borrow")
    public String getBorrowRequests(@RequestBody String jsonString) {
        JSONObject data = new JSONObject(jsonString);
        String email = ControllerHelper.getEmailFromJson(data);

        ArrayList<BorrowedBook> pendingBorrowRequests = UserDatabaseLogic.booksRequestedToBorrowOrLoan(email, true);

        ArrayList<String> JSONPendingBooks = ControllerHelper.getJSONBorrowedBooks(pendingBorrowRequests);

        return JSONPendingBooks.toString();

    }

    /**
     * Request mapping to return a list of users who have request to loan books from the given users along with the
     * books they hvae requested to borrow
     * @param jsonString JSON containing parameters needed for getting this information
     * @return a string containing JSON with the requested information
     */
    @RequestMapping(method = RequestMethod.POST, value = "/request/loan")
    public String getLoanRequests(@RequestBody String jsonString) {
        JSONObject data = new JSONObject(jsonString);
        String email = ControllerHelper.getEmailFromJson(data);

        ArrayList<BorrowedBook> pendingBorrowRequests = UserDatabaseLogic.booksRequestedToBorrowOrLoan(email, false);

        ArrayList<String> JSONPendingBooks = ControllerHelper.getJSONBorrowedBooks(pendingBorrowRequests);

        return JSONPendingBooks.toString();
    }

    /**
     * Request mapping to handle when a user makes a request to borrow a book
     * @param jsonString JSON containing parameters needed for performing this action
     * @return JSON string containing a list of BorrowedBooks
     */
    @RequestMapping(method = RequestMethod.POST, value = "/request/process")
    public String processBorrowRequest(@RequestBody String jsonString) {
        JSONObject data = new JSONObject(jsonString);
        String email = ControllerHelper.getEmailFromJson(data);

        ArrayList<BorrowedBook> pendingBorrowRequests = UserDatabaseLogic.booksRequestedToBorrowOrLoan(email, false);

        ArrayList<String> JSONPendingBooks = ControllerHelper.getJSONBorrowedBooks(pendingBorrowRequests);

        return JSONPendingBooks.toString();
    }

    /**
     * Request mapping to handle the act of a user approving or denying a loan request
     * @param jsonString JSON containing parameters needed for performing this action
     * @return a status response of OK if the loan request response was processed correctly, a bad request otherwise
     */
    @RequestMapping(method = RequestMethod.POST, value = "/request/approveOrDenyRequest")
    public ResponseEntity<String> updateBorrowRequest(@RequestBody String jsonString) {
        JSONObject data = new JSONObject(jsonString);

        ArrayList<String> requestNumberDate = ControllerHelper.getRequestNumberDateStatusFromJSON(data);

        String status = requestNumberDate.get(0);
        Boolean updateSuccessful = ControllerHelper.processRequestStatus(status, requestNumberDate);

        return (updateSuccessful) ? new ResponseEntity<String>(HttpStatus.OK) : new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Request mapping to handle when a user recalls a book
     * @param jsonString JSON containing parameters needed for performing this action
     * @return a status response of OK if the recall action was processed correctly, a bad request otherwise
     */
    @RequestMapping(method = RequestMethod.POST, value = "/recall")
    public ResponseEntity<String> recallLoan(@RequestBody String jsonString) {
        JSONObject data = new JSONObject(jsonString);

        String requestNumber = data.get("requestNumber").toString();

        boolean recallSuccessful = UserDatabaseLogic.returnOrRecallBook(requestNumber, false);

        return (recallSuccessful) ? new ResponseEntity<String>(HttpStatus.OK) : new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Request mapping to handle when a user checks in a book they have lent out after it has been returned from loan
     * @param jsonString JSON containing parameters needed for performing this action
     * @return a status response of OK if the return action was processed correctly, a bad request otherwise
     */
    @RequestMapping(method = RequestMethod.POST, value = "/returnBook")
    public ResponseEntity<String> returnBook(@RequestBody String jsonString) {
        JSONObject data = new JSONObject(jsonString);

        String requestNumber = data.get("requestNumber").toString();

        boolean returnSuccessful = UserDatabaseLogic.returnOrRecallBook(requestNumber, true);

        return (returnSuccessful) ? new ResponseEntity<String>(HttpStatus.OK) : new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }

    /**
     *
     * @param jsonString JSON containing parameters needed for performing this action
     * @return a status response of OK if the return action was processed correctly, a bad request otherwise
     */
    @RequestMapping(method = RequestMethod.POST, value = "/rating/book")
    public String getAverageRatingOfBook(@RequestBody String jsonString) {
        JSONObject data = new JSONObject(jsonString);
        String ISBN = data.get("ISBN").toString();

        String rating = BookDatabaseLogic.getAverageRating(ISBN);
        JSONObject output = new JSONObject(jsonString);
        output.put("AverageRating",rating);
        return output.toString();
    }

    /**
     *
     * @param jsonString JSON containing parameters needed for performing this action
     * @return a status response of OK if the return action was processed correctly, a bad request otherwise
     */
    @RequestMapping(method = RequestMethod.POST, value = "/rating/book/set")
    public ResponseEntity<String> setRatingOfBook(@RequestBody String jsonString) {
        JSONObject data = new JSONObject(jsonString);
        String email = data.get("email").toString();
        String ISBN = data.get("ISBN").toString();
        Integer rating = Integer.parseInt(data.get("rating").toString());
        String review = data.get("review").toString();

        Boolean ratingIsSet = BookDatabaseLogic.setRating(email,ISBN,rating,review);

        return (ratingIsSet) ? new ResponseEntity<String>(HttpStatus.OK) : new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }


    /**
     * Set loan length
     * @param jsonString
     * @return ok if the table is updated successfully
     */
    @RequestMapping(method= RequestMethod.POST, value = "/loanLength/set")
    public ResponseEntity<String> setLoanLength(@RequestBody String jsonString) {
        JSONObject data = new JSONObject(jsonString);
        String loanLength = data.getString("loanLength");
        String copyID = data.getString("copyID");
        Boolean insert = BookDatabaseLogic.setLendingTerms(loanLength,copyID);
        return (insert) ? new ResponseEntity<String>(HttpStatus.OK) : new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Get loan length
     * @param jsonString with copyID
     * @return JSON with loanLength
     */
    @RequestMapping(method= RequestMethod.POST, value = "/loanLength/get")
    public String getLoanLength(@RequestBody String jsonString) {
        JSONObject data = new JSONObject(jsonString);
        String copyID = data.getString("copyID");
        String loanLenght = BookDatabaseLogic.getLendingTerms(copyID);
        JSONObject output = new JSONObject();
        output.put("loanLength",loanLenght);
        return output.toString();
    }
}