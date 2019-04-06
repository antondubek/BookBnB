package server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ControllerHelper {

    /**
     * gets password from Json
     * @param data JSONObject
     * @return data
     */
    public static String getPasswordFromJson(JSONObject data){
        String password;
        try {
            password = data.get("password").toString();
        } catch (JSONException se){
            System.out.println("Error occurred");
            password = "";
        }
        return password;
    }

    /**
     * Gets email from the JSON object
     * @param data JOSN object received from client
     * @return Email in String.
     */
    public static String getEmailFromJson(JSONObject data) {
        String email;
        try {
            email = data.get("email").toString();
        } catch (JSONException se) {
            System.out.println("Error occurred");
            email = "";
        }
        return email;
    }

    /**
     * Gets user from JSNONObject
     * @param data Json object which contains "name", "email", "city"
     * @return User to register
     */
    public static User getUserFromJSON(JSONObject data){
        try{
            String name = data.get("name").toString();
            String email = data.get("email").toString();
            String city = data.get("city").toString();
            return new User(name, email, city);
        } catch (JSONException se) {
            System.out.println("Error occurred");
        }
        return null;
    }

    /**
     * Gets JSON String ArrayList From ArrayList of books
     * @param books ArrayList of type Book
     * @return ArrayList of String in JSON format
     */
    public static ArrayList<String> getJSONBooks(ArrayList<Book> books){
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
        return JSONBooks;
    }

    /**
     * Creates JSON of Follows in ArrayList
     * @param emailsOfFollows
     * @return
     */
    public static ArrayList<String> getJSONFollows(ArrayList<User> emailsOfFollows){
        ArrayList<String> JSONFollows = new ArrayList<>();
        String JSON;
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
        return JSONFollows;
    }

    /**
     * Get user from ArrayList of users.
     * @param user ArraList of users
     * @return first element of user arrayList.
     */
    public static User getUserFromArrayList(ArrayList<User> user){
        User specificUser;
        if(user.size() == 1){
            specificUser = user.get(0);
        } else {
            specificUser = new User("","","");
        }
        return specificUser;
    }

    /**
     * Creates JSON String from User object
     * @param specificUser User object
     * @return JSON string
     */
    public static String createJSONFromUser(User specificUser){
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
     * getFollowFields returns an array with email and email of following user
     * @param data JSSONObject which contains "email" and "friendEmail" fields
     * @return String[0] - email, String[1] - friendEmail
     */
    public static String[] getFollowFields(JSONObject data){
        String[] followFields = new String[2];
        followFields[0] = data.get("email").toString();
        followFields[1] = data.get("friendEmail").toString();
        return  followFields;
    }

    /**
     * get's Email from JSON String
     * @param jsonString JSON in string
     * @return email, or "" if the json was send in wrong format
     */
    public static String getEmailToFetchFollowers(String jsonString){
        try {
            JSONObject data = new JSONObject(jsonString);
            return data.get("email").toString();
        } catch (JSONException se){
            System.out.println("Error occurred");
            return "";
        }
    }

    /**
     * Get Book object from JSON object.
     * @param data JSON object which contains Book fields
     * @return Book object
     */
    public static Book getBookFromJSON(JSONObject data){
        String ISBN = data.get("ISBN").toString();
        String title = data.get("title").toString();
        String author = data.get("author").toString();
        String edition = data.get("edition").toString();
        Book newBook = new Book(ISBN, title, author);
        if(edition != null && !edition.equals("")) {
            newBook.setEdition(edition);
        }
        return newBook;
    }

    /**
     * get availability boolean from JSON
     * @param data JSON object
     * @return boolean of availability of book from JSON object
     */
    public static Boolean getAvailabilityFromJSON(JSONObject data){
        String availability = data.get("available").toString();
        Boolean currentAvailability = Boolean.parseBoolean(availability);
        return currentAvailability;
    }

    /**
     * Update availability of the book, of the user with specific copy ID.
     * @param email email of the user, who owns a book
     * @param currentAvailability What was the availability of the book before the update
     * @param ISBN ISBN of the book
     * @param copyID copy ID of the specified book
     * @return if the update was successful.
     */
    public static Boolean updateAvailability(String email, Boolean currentAvailability, String ISBN, String copyID){
        Boolean updatedAvailability;

        ArrayList<User> user = UserDatabaseLogic.findUser(email);
        if(user.size() != 1){
            updatedAvailability = false;
        } else {
            User specificUser = user.get(0);

            updatedAvailability = BookDatabaseLogic.updateBookAvailability(specificUser.getEmail(), currentAvailability, ISBN, copyID);
        }
        return updatedAvailability;
    }

}
