package hello;

public class Query {
    public static String userSearchByEmail = "SELECT name, email, city FROM Users WHERE email = ?;";
    public static String insertNewUser = "INSERT INTO Users (name, email, city) VALUES ('%s', '%s', '%s');";
    public static String insertNewUserPassword = "INSERT INTO Users_password VALUES ((SELECT id FROM Users WHERE email = '%s'), '%s');";
    public static String login = "SELECT password FROM Users_password INNER JOIN Users ON Users_password.users_id = Users.id WHERE Users.email = ?;";
    public static String fetchBooksBase = "SELECT * FROM Book;";
    public static String fetchUserBooks = "SELECT ISBN, title, author, book_version, available FROM Book " +
                                          "INNER JOIN Users_book ON Book.ISBN = Users_book.Book_ISBN INNER JOIN Users ON Users_book.Users_id = Users.id " +
                                          "WHERE EMAIL = ?;";
    public static String checkIfBookInDB = "SELECT ISBN FROM Book WHERE ISBN = ?;";
    public static String insertNewBook = "INSERT INTO Book VALUES (?, ?, ?, ?);";
    public static String addUserToBook = "INSERT INTO Users_book (Users_id, Book_ISBN, available) VALUES ((SELECT id FROM Users WHERE email = ?), ?, false);";

}
