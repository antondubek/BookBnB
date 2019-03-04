package hello;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="Rick") String name) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, DataBase.getData("test")));
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