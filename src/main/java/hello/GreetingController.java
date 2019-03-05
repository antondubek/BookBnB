package hello;

import java.util.concurrent.atomic.AtomicLong;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;

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

}