package hello;

import java.util.concurrent.atomic.AtomicLong;


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

    @RequestMapping("/testQueries")
    public Greeting get(@RequestParam(value="name", defaultValue = "Rick") String name) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, DataBase.userList()));
    }

    @RequestMapping("/testUserObject")
    public Greeting noRequestParam() {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, DataBase.userObjects()));
    }

    @RequestMapping("/searchSpecificUser")
    public Greeting userRequestParam(@RequestParam(value="name", defaultValue = "Rick") String name) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, DataBase.findUser(name)));
    }

}