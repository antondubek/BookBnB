package server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
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
        User newUser = getUserFromJSON(data);
        String password = data.get("password").toString();

        Boolean insert = UserDatabaseLogic.insertNewUser(newUser, password);

        return (insert) ? new ResponseEntity<String>(HttpStatus.OK) : new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Gets user from JSNONObject
     * @param data Json object which contains "name", "email", "city"
     * @return User to register
     */
    public static User getUserFromJSON(JSONObject data){
        String name = data.get("name").toString();
        String email = data.get("email").toString();
        String city = data.get("city").toString();
        return new User(name, email, city);
    }

    /**
     * Login request method
     * @param jsonString gets email and password to login
     * @return status if login was successful
     */
    @RequestMapping(method= RequestMethod.POST, value = "/login")
    public ResponseEntity<String> logintest(@RequestBody String jsonString){

        JSONObject data = new JSONObject(jsonString);

        String email = data.get("email").toString();
        String password = data.get("password").toString();

        return (UserDatabaseLogic.loginIsSuccessful(password, email)) ? new ResponseEntity<String>(HttpStatus.OK) : new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }

    /**
     * get books request method, GET request
     * @param command command which table of books to send
     * @return books for user or all books depending on request
     */
    @RequestMapping(method = RequestMethod.GET, value="/book")
    public String allBooks(@RequestParam(value="command", defaultValue = "none") String command){
        if(!command.equals("all")){ return "error"; }

        ArrayList<Book> books = BookDatabaseLogic.fetchAllBooks("all");

        String JSON;
        ArrayList<String> JSONBooks = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        for(Book book : books) {
            try {
                JSON = mapper.writeValueAsString(book);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                continue;
            }

            JSONBooks.add(JSON);
        }

        return JSONBooks.toString();
    }

    /**
     * Searching for the user. if user is not found, returns user with empty fields.
     * @param jsonString
     * @return users
     */
    @RequestMapping(method= RequestMethod.POST, value = "/profile")
    public String loadProfile(@RequestBody String jsonString) {
        JSONObject data = new JSONObject(jsonString);
        String email = data.get("email").toString();
        ArrayList<User> user = UserDatabaseLogic.findUser(email);
        User specificUser;

        if(user.size() == 1){
            specificUser = user.get(0);
        } else {
            specificUser = new User("","","");
        }
        String JSON;
        ObjectMapper mapper = new ObjectMapper();
        try {
            JSON = mapper.writeValueAsString(specificUser);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            JSON = "error";
        }

        return JSON;
    }

    /**
     * get all books for the user
     * @param jsonString
     * @return
     */
    @RequestMapping(method= RequestMethod.POST, value = "/profile/books")
    public String loadUserbooks(@RequestBody String jsonString) {
        JSONObject data = new JSONObject(jsonString);
        String email = data.get("email").toString();

        ArrayList<User> user = UserDatabaseLogic.findUser(email);
        if(user.size() != 1){
            return "No user found with this email address";
        }

        User specificUser = user.get(0);

        ArrayList<Book> books = BookDatabaseLogic.fetchAllBooks(specificUser.getEmail());
        String JSON;
        ArrayList<String> JSONBooks = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        for(Book book : books) {
            try {
                JSON = mapper.writeValueAsString(book);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                continue;
            }

            JSONBooks.add(JSON);
        }

        return JSONBooks.toString();

    }

    /**
     * Update whether or not a book is available for lending. If update successful, send STATUS OK, otherwise send 404
     * @param jsonString JSON passed in the request body, contains "email", "available", "ISBN", "copyID"
     * @return HttpStatus Ok if updated successfully
     */
    @RequestMapping(method= RequestMethod.POST, value = "/profile/books/availability")
    public ResponseEntity<String> updateBookAvailability(@RequestBody String jsonString) {
        JSONObject data = new JSONObject(jsonString);

        String email = data.get("email").toString();
        String availability = data.get("available").toString();
        Boolean currentAvailability = Boolean.parseBoolean(availability);
        String ISBN = data.get("ISBN").toString();
        String copyID   = data.get("copyID").toString();

        //TODO need to account for a the copy ID of a book, as one user may own multiple copies of a give book

        Boolean updatedAvailability;

        ArrayList<User> user = UserDatabaseLogic.findUser(email);
        if(user.size() != 1){
            updatedAvailability = false;
        } else {
            User specificUser = user.get(0);

            updatedAvailability = BookDatabaseLogic.updateBookAvailability(specificUser.getEmail(), currentAvailability, ISBN, copyID);
        }

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

        String[] followFields = getFollowFields(data);
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

        String[] followFields = getFollowFields(data);
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
        String[] followFields = getFollowFields(data);
        Boolean isFollowed = UserDatabaseLogic.userIsFollowed(followFields[0], followFields[1]);
        return Collections.singletonMap("userIsFollowed", isFollowed);
    }

    /**
     * getFollowFields returns an array with email and email of following user
     * @param data JSSONObject which contains "email" and "friendEmail" fields
     * @return String[0] - email, String[1] - friendEmail
     */
    public String[] getFollowFields(JSONObject data){
        String[] followFields = new String[2];
        followFields[0] = data.get("email").toString();
        followFields[1] = data.get("friendEmail").toString();
        return  followFields;
    }

    /**
     * Fetches user's followers from the db.
     * @param jsonString contains "email" of the user
     * @return JSON with emails
     */
    @RequestMapping(method= RequestMethod.POST, value = "/follow/fetch")
    public String getFollows(@RequestBody String jsonString) {
        String email = getEmailToFetchFollowers(jsonString);
        if (email.equals("")){
            return email;
        }

        ArrayList<User> emailsOfFollows = UserDatabaseLogic.fetchFollows(email);
        String JSON;
        ArrayList<String> JSONFollows = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        for(User friend : emailsOfFollows) {
            try {
                JSON = mapper.writeValueAsString(friend);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                continue;
            }

            JSONFollows.add(JSON);
        }
        return JSONFollows.toString();
    }

    /**
     * get's Email from JSON String
     * @param jsonString JSON in string
     * @return email, or "" if the json was send in wrong format
     */
    public String getEmailToFetchFollowers(String jsonString){
        try {
            JSONObject data = new JSONObject(jsonString);
            return data.get("email").toString();
        } catch (JSONException se){
            System.out.println("Error occurred");
            return "";
        }
    }

    /**
     * Add book request method
     * @param jsonString
     * @return ok if the book is added to the database successfully
     */
    @RequestMapping(method= RequestMethod.POST, value = "/profile/addBook")
    public ResponseEntity<String> addBook(@RequestBody String jsonString) {
        JSONObject data = new JSONObject(jsonString);

        //TODO BREAK THIS OUT INTO ITS OWN METHOD
        String ISBN = data.get("ISBN").toString();
        String title = data.get("title").toString();
        String author = data.get("author").toString();
        String edition = data.get("edition").toString();
        String email = data.get("email").toString();

        Book newBook = new Book(ISBN, title, author);

        if(edition != null && !edition.equals("")) {
            newBook.setEdition(edition);
        }

        Boolean insert = BookDatabaseLogic.insertNewBook(newBook, email);

        return (insert) ? new ResponseEntity<String>(HttpStatus.OK) : new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }
}