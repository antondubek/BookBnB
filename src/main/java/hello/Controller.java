package hello;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class Controller {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping(method= RequestMethod.POST, value = "/register")
    public ResponseEntity<String> login(@RequestBody String jsonString){

        JSONObject data = new JSONObject(jsonString);

        String name = data.get("name").toString();
        String email = data.get("email").toString();
        String city = data.get("city").toString();
        String password = data.get("password").toString();

        User new_user = new User(name, email, city);
        Boolean insert = Database.insertNewUser(new_user, password);

        return (insert) ? new ResponseEntity<String>(HttpStatus.OK) : new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method= RequestMethod.POST, value = "/login")
    public ResponseEntity<String> logintest(@RequestBody String jsonString){

        JSONObject data = new JSONObject(jsonString);

        String email = data.get("email").toString();
        String password = data.get("password").toString();

        return (Database.login(password, email)) ? new ResponseEntity<String>(HttpStatus.OK) : new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.GET, value="/book")
    public String allBooks(@RequestParam(value="command", defaultValue = "none") String command){
        if(!command.equals("all")){ return "error"; }

        ArrayList<Book> books = Database.fetchAllBooks("all");

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

    @RequestMapping(method= RequestMethod.POST, value = "/profile")
    public String loadProfile(@RequestBody String jsonString) {
        JSONObject data = new JSONObject(jsonString);
        String email = data.get("email").toString();

        ArrayList<User> user = Database.findUser(email);
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

    @RequestMapping(method= RequestMethod.POST, value = "/profile/books")
    public String loadUserbooks(@RequestBody String jsonString) {
        JSONObject data = new JSONObject(jsonString);
        String email = data.get("email").toString();

        ArrayList<User> user = Database.findUser(email);
        if(user.size() != 1){
            return "error";
        }

        User specificUser = user.get(0);

        ArrayList<Book> books = Database.fetchAllBooks(specificUser.email);

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
     * This returns all the users in the database right now. It does not need any request parameters.
     * @return a greeting object back to client, containing the requested data
     */
    @RequestMapping("/testUserObject")
    public Greeting noRequestParam() {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, Database.userObjects()));
    }
}