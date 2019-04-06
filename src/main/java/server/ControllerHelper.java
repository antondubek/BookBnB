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
}
