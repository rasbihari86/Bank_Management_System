package BankManagementSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class BankingApp {

    private static String url =  "jdbc:mysql://localhost:3306/banking_system";
    private static String username = "root";
    private  static  String password = "password";

    public static void main(String[] args) throws SQLException,ClassNotFoundException {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            Connection connection = DriverManager.getConnection(url,username, password);
            Scanner sc = new Scanner(System.in);
            Accounts accounts = new Accounts(connection , sc);
            AccountManager accountManager = new AccountManager(connection , sc);
            User user = new User(connection , sc);



        } catch (SQLException e) {
            throw new RuntimeException(e);
        }



    }
}
