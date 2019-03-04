

import java.sql.*;

public class DataBase {

    public static String getData() {
        Connection con = null;
        String url = "jdbc:mysql://dag8.host.cs.st-andrews.ac.uk/";
        String db = "dag8_RickDB";
        String driver = "com.mysql.cj.jdbc.Driver";
        String user = "ri31";
        String pass = "33.1Z4HLNfnbuy";
        
        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            
            try {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM test");
                while (rs.next()) {
                    String name = rs.getString("name");
                    System.out.println(name);
                    return name;
                }

                //ADD User
                //st.executeUpdate("INSERT INTO dag8_RickDB.test (name) Values ('Praneeth'), ('John'); ");


                //DELETE
                //st.executeUpdate("DELETE FROM dag8_RickDB.test WHERE name = 'John'");

                //UPDATE
                //st.executeUpdate("UPDATE dag8_RickDB.test SET name = 'Edwin' WHERE name = 'Edvin'");
                con.close();
            } catch (SQLException se) {
                 System.out.println("SQL ERR: " + se); //
            }
        } catch (Exception e) {
            System.out.println("ERR: " + e);
        }
        return "Default";
    }
}