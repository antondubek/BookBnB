package hello;

public class Query {
    public static String allUserInformation = "SELECT name, email, city FROM Users";
    public static String userSearchByEmail = "SELECT name, email, city FROM Users WHERE name = '%s'; ";
}
