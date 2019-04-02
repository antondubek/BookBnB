package server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserDatabaseLogic extends DatabaseLogic {

    /**
     * Login method. Opens the connection, and calls helping method loginDetailsAreRight.
     * @param password password of the user, who logs in
     * @param email email of the user, who logs in
     * @return true if login is successful
     */
    public static Boolean loginIsSuccessful(String password, String email) {
        if (openTheConnection()){
            return loginDetailsAreRight(password, getDataFromDataBaseForLogin(email));
        }
        return false;

    }

    private static ArrayList<String> getDataFromDataBaseForLogin(String email){
        try (PreparedStatement statementForLogin = con.prepareStatement(Query.LOGIN)) {
            statementForLogin.setString(1, email);
            ResultSet queryResults = statementForLogin.executeQuery();
            String[] namesOfFieldsInResponse = new String[]{"password"};
            ArrayList<String> data = getArrayListFromResultSet(queryResults,namesOfFieldsInResponse);
            return data;
        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se);
        }
        return null;
    }

    /**
     * Helps loginIsSuccessful to do the login part, checks if the password of the user is right or not.
     * @param password
     * @return
     */
    public static Boolean loginDetailsAreRight(String password, ArrayList<String> data){
        try {
            con.close();
            if (data.size() == 1) {
                return password.equals(data.get(0));
            } else {
                return false;
            }
        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se);
        } catch (NullPointerException e) {
            System.out.println("ERR: " + e);
        }
        return false;
    }

    /**
     * Find the user, by his email.
     * @param email email of the user to be searched
     * @return ArrayListOfUsers
     */
    public static ArrayList<User> findUser(String email){
        openTheConnection();
        ArrayList<User> data = new ArrayList<>();
        try (PreparedStatement statementToSearchUserByMail = con.prepareStatement(Query.USER_SEARCH_BY_EMAIL)){
            statementToSearchUserByMail.setString(1,email);
            ResultSet queryResults = statementToSearchUserByMail.executeQuery();
            data = getUsersFromResultSet(queryResults);
            con.close();
        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se); //
        }
        return data;
    }

    public static ArrayList<User> getUsersFromResultSet(ResultSet queryResults){
        ArrayList<User> users = new ArrayList<>();
        String[] namesOfFieldsInResponse = new String[]{"name", "email", "city"};
        ArrayList<String> data = getArrayListFromResultSet(queryResults, namesOfFieldsInResponse);

        for (int i = 0; i <= data.size()-namesOfFieldsInResponse.length; i+=namesOfFieldsInResponse.length){
            User nextUser = new User(data.get(i), data.get(i+1), data.get(i+2));
            users.add(nextUser);
        }
        return users;
    }

    /**
     * Registers a new user.
     * @param newUser username
     * @param password password (is hashed)
     * @return false if the connection is failed and the user is not registered
     */
    public static Boolean insertNewUser(User newUser, String password){
        if (!openTheConnection()){
            return false;
        }
        try (PreparedStatement statementToInsertUser = con.prepareStatement(Query.INSERT_NEW_USER);
             PreparedStatement statementToInsertPassword = con.prepareStatement(Query.INSERT_NEW_USER_PASSWORD)){

            statementToInsertUser.setString(1,newUser.getName());
            statementToInsertUser.setString(2,newUser.getEmail());
            statementToInsertUser.setString(3,newUser.getCity());

            statementToInsertUser.executeUpdate();

            statementToInsertPassword.setString(1,newUser.getEmail());
            statementToInsertPassword.setString(2,password);

            statementToInsertPassword.executeUpdate();

            con.close();
            return true;
        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se);
        }
        return false;
    }

    public static boolean followPeople(String email, String friendEmail){
        if (!openTheConnection()){
            return false;
        }
        try (PreparedStatement statementToFollow = con.prepareStatement(Query.FOLLOW_PEOPLE)){
            statementToFollow.setString(1,email);
            statementToFollow.setString(2,friendEmail);
            statementToFollow.executeUpdate();

            con.close();
            return true;
        } catch (SQLException se) {
        System.out.println("SQL ERR: " + se);
    }
        return false;
    }

    /**
     * Fetch the follows, by email of user.
     * @param email email of the user
     * @return ArrayListOfUsers Follows
     */
    public static ArrayList<String> fetchFollows(String email){
        openTheConnection();
        ArrayList<String> data = new ArrayList<>();
        try (PreparedStatement statementToSearchUserByMail = con.prepareStatement(Query.FETCH_FOLLOWS)){

            statementToSearchUserByMail.setString(1,email);

            ResultSet queryResults = statementToSearchUserByMail.executeQuery();
            String[] namesOfFieldsInResponse = new String[]{"email"};
            data = getArrayListFromResultSet(queryResults, namesOfFieldsInResponse);

            con.close();
        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se); //
        }
        return data;
    }
}
