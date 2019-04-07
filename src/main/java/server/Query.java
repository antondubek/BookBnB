package server;

/**
 * Query class, contains all possible queries that are used in DataBase class.
 */
public class Query {
    public final static String USER_SEARCH_BY_EMAIL = "SELECT name, email, city FROM Users WHERE email = ?;";
    public final static String INSERT_NEW_USER = "INSERT INTO Users (name, email, city) VALUES (?, ?, ?);";
    public final static String INSERT_NEW_USER_PASSWORD = "INSERT INTO Users_password VALUES ((SELECT id FROM Users WHERE email = ?), ?);";
    public final static String LOGIN = "SELECT password FROM Users_password INNER JOIN Users ON Users_password.users_id = Users.id WHERE Users.email = ?;";
    public final static String FETCH_BOOKS_BASE = "SELECT * FROM Book;";
    public final static String FETCH_USER_BOOKS = "SELECT ISBN, title, author, book_version, available, copy_id FROM Book " +
                                          "INNER JOIN Users_book ON Book.ISBN = Users_book.Book_ISBN INNER JOIN Users ON Users_book.Users_id = Users.id " +
                                          "WHERE EMAIL = ?;";
    public final static String CHECK_IF_BOOK_IN_DB = "SELECT ISBN FROM Book WHERE ISBN = ?;";
    public final static String INSERT_NEW_BOOK = "INSERT INTO Book VALUES (?, ?, ?, ?);";
    public final static String ADD_USER_TO_BOOK = "INSERT INTO Users_book (Users_id, Book_ISBN, available) VALUES ((SELECT id FROM Users WHERE email = ?), ?, false);";
    public final static String UPDATE_BOOK_AVAILABILITY = "UPDATE Users_book SET available = ? WHERE Book_ISBN = ? AND Users_id = (SELECT id FROM Users WHERE email = ?) AND copy_id = ?;";
    public final static String FOLLOW_PEOPLE = "INSERT INTO Users_followers (Users_id, follower) VALUES ((SELECT id FROM Users WHERE email = ?),(SELECT id FROM Users WHERE email = ?));";
    public final static String FETCH_FOLLOWS = "select k.email, k.city, k.name from Users_followers t left join Users e on t.Users_id = e.id left join Users k on t.follower = k.id where e.email = ?;";
    public final static String DELETE_FOLLOW = "DELETE FROM Users_followers WHERE Users_id = (SELECT id FROM Users WHERE email = ?) and follower = (SELECT id FROM Users WHERE email = ?);";
    public final static String GET_IF_USER_IS_FOLLOWED =  "SELECT Users_id FROM Users_followers WHERE Users_id = (SELECT id FROM Users WHERE email = ?) and follower = (SELECT id FROM Users WHERE email = ?);";
    public final static String GET_LENDERS = "SELECT Users_id, name, city, loan_length, copy_id " +
                                            "FROM Users_book INNER JOIN Users on Users_id = id " +
                                            "WHERE Book_ISBN = ? AND available = TRUE;";
    public final static String REQUEST_TO_BORROW = "INSERT INTO Request (borrower_id, lender_id, Book_ISBN, copy_id, status) " +
                                                "VALUES ((SELECT id FROM Users WHERE email = ?), ?, (SELECT Book_ISBN from Users_book WHERE copy_id = ?), ?, ?);";
}
