package server;

import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    /**
     * Register request method.
     * @param jsonString json contains fields of the user
     * @return response if the registration is successful or not.
     */
    @RequestMapping(method= RequestMethod.POST, value = "/register")
    public ResponseEntity<String> login(@RequestBody String jsonString){

        JSONObject data = new JSONObject(jsonString);

        String name = data.get("name").toString();
        String email = data.get("email").toString();
        String city = data.get("city").toString();
        String password = data.get("password").toString();

        User newUser = new User(name, email, city);
        Boolean insert = UserDatabase.insertNewUser(newUser, password);

        return (insert) ? new ResponseEntity<String>(HttpStatus.OK) : new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
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

        return (UserDatabase.loginIsSuccessful(password, email)) ? new ResponseEntity<String>(HttpStatus.OK) : new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }

    /**
     * get books request method, GET request
     * @param command command which table of books to send
     * @return books for user or all books depending on request
     */
    @RequestMapping(method = RequestMethod.GET, value="/book")
    public String allBooks(@RequestParam(value="command", defaultValue = "none") String command){
        if(!command.equals("all")){ return "error"; }

        ArrayList<Book> books = BookDatabase.fetchAllBooks("all");

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
     * Searching for the user
     * @param jsonString
     * @return users
     */
    @RequestMapping(method= RequestMethod.POST, value = "/profile")
    public String loadProfile(@RequestBody String jsonString) {
        JSONObject data = new JSONObject(jsonString);
        String email = data.get("email").toString();

        ArrayList<User> user = UserDatabase.findUser(email);
        if(user.size() != 1){
            return "error";
        }

        User specificUser = user.get(0);

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

        ArrayList<User> user = UserDatabase.findUser(email);
        if(user.size() != 1){
            return "error";
        }

        User specificUser = user.get(0);

        ArrayList<Book> books = BookDatabase.fetchAllBooks(specificUser.getEmail());
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
     * Add book request method
     * @param jsonString
     * @return ok if the book is added to the database successfully
     */
    @RequestMapping(method= RequestMethod.POST, value = "/profile/addBook")
    public ResponseEntity<String> addBook(@RequestBody String jsonString) {
        JSONObject data = new JSONObject(jsonString);

        String ISBN = data.get("ISBN").toString();
        String title = data.get("title").toString();
        String author = data.get("author").toString();
        String edition = data.get("edition").toString();
        String email = data.get("email").toString();

        Book newBook = new Book(ISBN, title, author);

        if(edition != null && !edition.equals("")) {
            newBook.setEdition(edition);
        }

        Boolean insert = BookDatabase.insertNewBook(newBook, email);

        return (insert) ? new ResponseEntity<String>(HttpStatus.OK) : new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }
}