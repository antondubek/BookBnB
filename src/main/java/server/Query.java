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

    public final static String FETCH_USER_BOOKS = "SELECT ISBN, title, author, book_version, available, copy_id, loan_to AS isLoaned FROM Book " +
                                          "INNER JOIN Users_book ON Book.ISBN = Users_book.Book_ISBN INNER JOIN Users ON Users_book.Users_id = Users.id " +
                                          "WHERE EMAIL = ?;";

    public final static String CHECK_IF_BOOK_IN_DB = "SELECT ISBN FROM Book WHERE ISBN = ?;";

    public final static String INSERT_NEW_BOOK = "INSERT INTO Book VALUES (?, ?, ?, ?);";

    public final static String ADD_USER_TO_BOOK = "INSERT INTO Users_book (Users_id, Book_ISBN, available) VALUES ((SELECT id FROM Users WHERE email = ?), ?, false);";

    public final static String UPDATE_BOOK_AVAILABILITY = "UPDATE Users_book SET available = ? WHERE Book_ISBN = ? AND Users_id = (SELECT id FROM Users WHERE email = ?) AND copy_id = ?;";

    public final static String FOLLOW_PEOPLE = "INSERT INTO Users_followers (Users_id, follower) VALUES ((SELECT id FROM Users WHERE email = ?),(SELECT id FROM Users WHERE email = ?));";

    public final static String FETCH_USERS_FOLLOWERS = "select k.email, k.city, k.name from Users_followers t left join Users e on t.Users_id = e.id left join Users k on t.follower = k.id where e.email = ?;";

    public final static String FETCH_WHO_USER_FOLLOWS = "select k.email, k.city, k.name " +
                                                    "from Users_followers t left join Users e on t.follower = e.id left join Users k on t.Users_id = k.id " +
                                                    "where e.email = ?;";

    public final static String DELETE_FOLLOW = "DELETE FROM Users_followers WHERE Users_id = (SELECT id FROM Users WHERE email = ?) and follower = (SELECT id FROM Users WHERE email = ?);";

    public final static String GET_IF_USER_IS_FOLLOWED =  "SELECT Users_id FROM Users_followers WHERE Users_id = (SELECT id FROM Users WHERE email = ?)" +
                                                            " and follower = (SELECT id FROM Users WHERE email = ?);";

    public final static String GET_LENDERS = "SELECT Users_id, name, city, loan_length, copy_id " +
                                            "FROM Users_book INNER JOIN Users on Users_id = id " +
                                            "WHERE Book_ISBN = ? AND available = TRUE;";

    public final static String REQUEST_TO_BORROW = "INSERT INTO Request (borrower_id, lender_id, Book_ISBN, copy_id, status) " +
                                                "VALUES ((SELECT id FROM Users WHERE email = ?), ?, (SELECT Book_ISBN from Users_book WHERE copy_id = ?), ?, ?);";

    public final static  String BORROW_REQUESTS = "SELECT ISBN, title, author, status, GroupOne.name AS person_of_interest, loan_start, loan_end, request_number, copy_id " +
                                        "FROM Request INNER JOIN Users AS GroupOne ON lender_id = GroupOne.id " +
                                        "INNER JOIN Book ON Book_ISBN = ISBN " +
                                        "INNER JOIN Users AS GroupTwo ON borrower_id = GroupTwo.id " +
                                        "NATURAL JOIN Users_book " +
                                        "WHERE GroupTwo.email = ?;";

    public final static  String LOAN_REQUESTS = "SELECT ISBN, title, author, status, GroupTwo.name AS person_of_interest, loan_start, loan_end, request_number, copy_id " +
                                                "FROM Request INNER JOIN Users AS GroupOne ON lender_id = GroupOne.id " +
                                                "INNER JOIN Book ON Book_ISBN = ISBN " +
                                                "INNER JOIN Users AS GroupTwo ON borrower_id = GroupTwo.id " +
                                                "NATURAL JOIN Users_book " +
                                                "WHERE GroupOne.email = ?;";

    public final static String UPDATE_BORROW_REQUEST = "UPDATE Request SET status = ? WHERE request_number = ?;";

    public final static String UPDATE_FOR_NEW_LOAN = "UPDATE Users_book SET " +
                                                    "available = false, " +
                                                    "loan_to = (SELECT borrower_id FROM Request WHERE request_number = ?), " +
                                                    "loan_start = ?, " +
                                                    "loan_end = Users_book.loan_start + Users_book.loan_length " +
                                                    "WHERE copy_id = (SELECT copy_id FROM Request WHERE request_number = ?);";

    public static String RETURN_OR_RECALL_BOOK = "UPDATE Users_book SET loan_to = null, loan_start = null, loan_end = null WHERE copy_id = (SELECT copy_id FROM Request WHERE request_number = ?);";

    public static String UPDATE_STATUS_AT_END_OF_LOAN = "UPDATE Request SET status = ? WHERE request_number = ? ";

    public static String AVERAGE_BOOK_RATING = "SELECT AVG(rating) FROM Users_rating GROUP BY(Book_ISBN);";

    public static String AVERAGE_USER_REPUTATION = "SELECT AVG(rating), borrower FROM Reputation GROUP BY(borrower);"; //SELECT AVG(rating), borrower FROM Reputation WHERE borrower = '3' GROUP BY(borrower);



}
