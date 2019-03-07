package hello;

public class Query {
    public static String allUserInformation = "SELECT name, email, city FROM Users";
    public static String userSearchByName = "SELECT name, email, city FROM Users WHERE name = '%s';";
    public static String insertNewUser = "INSERT INTO Users VALUES ('%s', '%s', '%s');";
    public static String insertNewUserPassword = "INSERT INTO Users_password VALUES (SELECT id FROM Users WHERE email = '%s', '%s');";

}
