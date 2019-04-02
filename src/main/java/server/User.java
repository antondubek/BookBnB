package server;

/**
 * User class
 */
public class User {
    private String name;
    private String email;
    private String city;

    //Getters
    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    public String getCity(){
        return city;
    }

    //setters
    public void setEmail(String email){
        this.email = email;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setCity(String city){
        this.city = city;
    }

    /**
     * User constructor.
     * @param name name of the registered user.
     * @param email email of the user.
     * @param city city where the user lends his books.
     */
    public User(String name, String email, String city){
        this.name = name;
        this.email = email;
        this. city = city;
    }

    @Override
    public String toString() {
        return "name: " + this.name + " email: " + this.email + " city: " + this.city;
    }
}
