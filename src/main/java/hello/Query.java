package hello;

public class Query {
    public static String allUserInformation = "SELECT name, email, city FROM Users";
    public static String userSearchByEmail = "SELECT name, email, city FROM Users WHERE email = '%s';";
    public static String insertNewUser = "INSERT INTO Users (name, email, city) VALUES ('%s', '%s', '%s');";
    public static String insertNewUserPassword = "INSERT INTO Users_password VALUES ((SELECT id FROM Users WHERE email = '%s'), '%s');";
    public static String login = "SELECT password FROM Users_password INNER JOIN Users ON Users_password.users_id = Users.id WHERE Users.email = '%s';";
    public static String fetchAllBooks = "SELECT * FROM Book;";

}
