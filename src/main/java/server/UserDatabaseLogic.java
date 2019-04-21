package server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
     * @param password string containing the entered password
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

    /**
     * Either follows or unfollows the user depending on query.
     * @param email of user
     * @param friendEmail email of user which needs to be followed or unfollowed
     * @param query query can be any query, usually follow or unfollow query
     * @return true if execuion done successfully
     */
    public static boolean manageFollow(String email, String friendEmail, String query){
        if (!openTheConnection()){
            return false;
        }
        try (PreparedStatement statementToFollow = con.prepareStatement(query)){
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
     * Follow user
     * @param email email of user
     * @param friendEmail email of user to follow
     * @return true if followed successfully
     */
    public static boolean followPeople(String email, String friendEmail){
        return manageFollow(email, friendEmail, Query.FOLLOW_PEOPLE);
    }

    /**
     * Unfollow user
     * @param email email of user
     * @param friendEmail email of user to unfollow
     * @return true if unfollowed successfully
     */
    public static Boolean deleteFollow(String email, String friendEmail){
        return manageFollow(email, friendEmail, Query.DELETE_FOLLOW);
    }

    /**
     * Fetch the follows, by email of user.
     * @param email email of the user
     * @param fetchFollowersOfUser a boolean that determines whether to fetch the followers of a given user (if true)
     *                             or to fetch the list of who a user follows (if false)
     * @return ArrayListOfUsers Follows
     */
    public static ArrayList<User> fetchFollows(String email, boolean fetchFollowersOfUser){
        openTheConnection();
        ArrayList<String> data = new ArrayList<>();
        ArrayList<User> user= new ArrayList<User>();
        try (PreparedStatement statementToSearchUserByMail = con.prepareStatement(queryToFetchWhoFollowsUser(fetchFollowersOfUser))){

            statementToSearchUserByMail.setString(1,email);

            ResultSet queryResults = statementToSearchUserByMail.executeQuery();
            user = getUsersFromResultSet(queryResults);
            con.close();
        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se); //
        }
        return user;
    }

    /**
     * This method decides whether to search for fetchFollowersOfUser or who the user is following
     * @param fetchFollowersOfUser a boolean, if true, want to use the query to fetch the list of who follows the user
     *                  if false want to get the list of users that this user is following
     * @return a string containing the query template
     */
    private static String queryToFetchWhoFollowsUser (boolean fetchFollowersOfUser){
        return (fetchFollowersOfUser) ? Query.FETCH_USERS_FOLLOWERS : Query.FETCH_WHO_USER_FOLLOWS;
    }

    /**
     * Checks if User is follows another user or not.
     * @param email email of iuser
     * @param friendEmail email of user which we check if is followed or not
     * @return true if the friendEmail is folowed by email user.
     */
    public static Boolean userIsFollowed(String email, String friendEmail){
        if (!openTheConnection()){
            return false;
        }
        try (PreparedStatement statementToFollow = con.prepareStatement(Query.GET_IF_USER_IS_FOLLOWED)){
            statementToFollow.setString(1,email);
            statementToFollow.setString(2,friendEmail);
            ResultSet queryResult = statementToFollow.executeQuery();
            Integer numberOfElementsInResult = 0;
            if (queryResult != null)
            {
                queryResult.last();    // moves cursor to the last row
                numberOfElementsInResult = queryResult.getRow(); // get row id
            }
            con.close();
            if ( numberOfElementsInResult!= 0){
                return true;
            }
        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se);
        }
        return false;
    }

    /**
     *
     * @param email
     * @return
     */
    public static String getAverageRating(String email){
        openTheConnection();

        try (PreparedStatement statementToGetRating = con.prepareStatement(Query.AVERAGE_USER_REPUTATION)){

            statementToGetRating.setString(1, email);

            ResultSet queryResults = statementToGetRating.executeQuery();
            ArrayList<String> averageRatingList = getArrayListFromResultSet(queryResults,new String[]{"AVG(rating)"});
            String averageRating;
            if (averageRatingList.size()>0){
                averageRating = averageRatingList.get(0);
            } else {
                averageRating = "";
            }
            con.close();
            return averageRating;
        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se); //
        }
        return "";
    }

    /**
     *
     * @param borowerEmail
     * @param lenderEmail
     * @param rating
     * @param review
     * @return
     */
    public static Boolean setReputation(String borowerEmail, String lenderEmail, Integer rating,String review){
        openTheConnection();

        try (PreparedStatement statementToSetRating = con.prepareStatement(Query.SET_USER_REPUTATION)){

            statementToSetRating.setString(1, lenderEmail);
            statementToSetRating.setString(2, borowerEmail);
            statementToSetRating.setInt(3, rating);
            statementToSetRating.setString(4, review);
            statementToSetRating.setDate(5, getCurrentDate());

            statementToSetRating.executeUpdate();
            con.close();
            return true;
        } catch (SQLException se) {
            System.out.println("SQL ERR: " + se); //
        }
        return false;
    }

}
