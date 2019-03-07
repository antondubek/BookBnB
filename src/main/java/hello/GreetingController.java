package hello;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;


import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/greeting")
    public Greeting getRequest(@RequestParam(value="name", defaultValue="Rick") String name) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, DataBase.getData("test")));
    }

    @PostMapping("/greeting")
    public Greeting postRequest(Greeting post) {
        return post;
    }

    @RequestMapping(method= RequestMethod.POST, value = "/login")
    public ResponseEntity<String> login(@RequestBody String jsonString){
        System.out.println(jsonString);

        JSONObject data = new JSONObject(jsonString);

        // data.put("name", "Rick Sanchez");
        // data.toString();

        System.out.println("Name: " + data.get("name").toString());
        System.out.println("Email: " + data.get("email").toString());
        System.out.println("Password: " + data.get("password").toString());
        System.out.println("City: " + data.get("city").toString());

        String name = data.get("name").toString();
        String email = data.get("email").toString();
        String city = data.get("city").toString();
        String password = data.get("password").toString();

        User new_user = new User(name, email, city);
        DataBase.insertNewUser(new_user, password);

        return new ResponseEntity<String>(HttpStatus.OK);
    }

    @RequestMapping(method= RequestMethod.POST, value = "/logintest")
    public String logintest(@RequestBody String jsonString){
        System.out.println(jsonString);

        JSONObject data = new JSONObject(jsonString);

        System.out.println("ID: " + data.get("id").toString());
        System.out.println("Content: " + data.get("content").toString());

        return "Login GOOD";
    }

    @RequestMapping(method= RequestMethod.POST, value = "/logintestj")
    public String logintestJson(@RequestBody String jsonString){
        System.out.println(jsonString);

        JSONArray array = new JSONArray();
        JSONObject obj1 = new JSONObject(new Greeting(1, "content"));
        JSONObject obj2 = new JSONObject(new Greeting(2, "content2"));
        JSONObject obj3 = new JSONObject(new Greeting(3, "content3"));

        array.put(obj1);
        array.put(obj2);
        array.put(obj3);

        return array.toString();
    }


    @RequestMapping("/addNewUser")
    public Greeting addNewUser(@RequestParam(value="name", defaultValue="Rick") String name) {
        DataBase.addNewUser(name);
        return new Greeting(counter.incrementAndGet(),
                String.format(template, DataBase.getData("test")));
    }

    @RequestMapping("/deleteTheUser")
    public Greeting deleteTheUser(@RequestParam(value="name", defaultValue="Rick") String name) {
        DataBase.deleteTheUser(name);
        return new Greeting(counter.incrementAndGet(),
                String.format(template, DataBase.getData("test")));
    }

    /**
     * This method is a slightly altered version of the first methods Riad wrote test using a GET request to query
     * the database and send back data as a response.
     * @param name query string parameter
     * @return
     */
    @RequestMapping("/testQueries")
    public Greeting get(@RequestParam(value="name", defaultValue = "Rick") String name) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, DataBase.userList()));
    }

    /**
     * This returns all the users in the database right now. It does not need any request parameters.
     * @return a greeting object back to client, containing the requested data
     */
    @RequestMapping("/testUserObject")
    public Greeting noRequestParam() {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, DataBase.userObjects()));
    }

    /**
     * This grabs a parameter of the query string passed as part of the GET request and uses its value to run an SQL
     * query, then sends the results of that query back to the client.
     * @param name the value of the name variable in the query string passed to the API
     * @return a greeting object that gets sent back to the client
     */
    @RequestMapping("/searchSpecificUser")
    public String userRequestParam(@RequestParam(value="name", defaultValue = "Rick") String name) {
        ArrayList<User> user = DataBase.findUser(name);
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

        //TODO most likely need to implement Jackson API to get this to work
    }

}