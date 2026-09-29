package BankManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AccountManager {

    private Connection connection;
    private Scanner scanner;
    public AccountManager(Connection connection, Scanner sc) {
        this.connection = connection;
        this.scanner = sc  ;

    }

    public void transfer_money(long sender_account_number) throws SQLException {
        scanner.nextLine();
        System.out.println("Enter Reciver account number");
        Long reciver = scanner.nextLong();
        System.out.print("Enter Amount: ");
        double amount = scanner.nextDouble();
        if(amount <= 0){
            System.out.println("Amount must be greater than 0");
            return;
        }
        scanner.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin = scanner.nextLine();

        try{
            connection.setAutoCommit(false);
            if(sender_account_number!= 0 && reciver != 0) {
                PreparedStatement ps = connection.prepareStatement("SELECT  * from accounts where account_number = ? AND security_pin = ?");
                ps.setLong(1, sender_account_number);
                ps.setString(2, security_pin);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                   double current_balance = rs.getDouble("balance");
                   if(current_balance>=amount){
                       String credit_query = "UPDATE accounts SET balance = balance + ? where account_number = ?";
                       String debit_query = "UPDATE accounts SET balance = balance - ? where account_number = ?";
                       PreparedStatement preparedStatement1 = connection.prepareStatement(credit_query);
                       PreparedStatement preparedStatement2 = connection.prepareStatement(debit_query);
                        preparedStatement1.setDouble(1,amount);

                       preparedStatement2.setDouble(1, amount);
                       preparedStatement1.setLong(2, reciver);
                       preparedStatement2.setLong(2, sender_account_number);;

                       int rows_affected = preparedStatement1.executeUpdate();
                       int rows_affected2 = preparedStatement2.executeUpdate();

                       if(rows_affected>0 && rows_affected2>0){
                           System.out.println("Transaction Successful!");
                           System.out.println("Rs."+amount+" Transferred Successfully");
                           connection.commit();
                           connection.setAutoCommit(true);
                       }else {
                           System.out.println("Transaction failed");
                           connection.rollback();
                           connection.setAutoCommit(true);
                       }
                   }else{
                       System.out.println("INSUFFICIENT BALANCE ");
                   }

                }else{
                    System.out.println("Invalid Security pin ");
                }
            }else{
                System.out.println("Invalid account number");

            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        connection.setAutoCommit(true);
    }

    public void getBalance(long account_number){
        scanner.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin = scanner.nextLine();
        try{
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT balance FROM Accounts WHERE account_number = ? AND security_pin = ?");
            preparedStatement.setLong(1, account_number);
            preparedStatement.setString(2, security_pin);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                double balance = resultSet.getDouble("balance");
                System.out.println("Balance: "+balance);
            }else{
                System.out.println("Invalid Pin!");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public void credit_money(long account_number)throws SQLException {
        scanner.nextLine();
        System.out.print("Enter Amount: ");

        double amount = scanner.nextDouble();
        if(amount <= 0){
            System.out.println("Amount must be greater than 0");
            return;
        }
        scanner.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin = scanner.nextLine();

        try {
            connection.setAutoCommit(false);
            if(account_number != 0) {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Accounts WHERE account_number = ? and security_pin = ? ");
                preparedStatement.setLong(1, account_number);
                preparedStatement.setString(2, security_pin);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    String credit_query = "UPDATE Accounts SET balance = balance + ? WHERE account_number = ?";
                    PreparedStatement preparedStatement1 = connection.prepareStatement(credit_query);
                    preparedStatement1.setDouble(1, amount);
                    preparedStatement1.setLong(2, account_number);
                    int rowsAffected = preparedStatement1.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Rs."+amount+" credited Successfully");
                        connection.commit();
                        connection.setAutoCommit(true);
                        return;
                    } else {
                        System.out.println("Transaction Failed!");
                        connection.rollback();
                        connection.setAutoCommit(true);
                    }
                }else{
                    System.out.println("Invalid Security Pin!");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        connection.setAutoCommit(true);
    }

    public  void debit_money (long account_number) throws SQLException {
        scanner.nextLine();
        System.out.println("Enter amount : ");
        double amount = scanner.nextDouble();
        if(amount <= 0){
            System.out.println("Amount must be greater than 0");
            return;
        }
        scanner.nextLine();
        System.out.println("Enter security pin");
        String security_pin = scanner.nextLine();
        scanner.nextLine();

        try{
            connection.setAutoCommit(false);
            if(account_number!=0){
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM Accounts WHERE account_number = ? and security_pin = ? ");
                ps.setLong(1,account_number);
                ps.setString(2,security_pin);
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    double available_balance = rs.getDouble("balance");
                    if(amount<=available_balance){
                        String debit_query = "UPDATE accounts SET balance = balance - ? where account_number = ?";
                        PreparedStatement preparedStatement = connection.prepareStatement(debit_query);
                        preparedStatement.setDouble(1,amount);
                        preparedStatement.setLong(2,account_number);

                        int rowsafeected = preparedStatement.executeUpdate();
                        if (rowsafeected>0){
                            System.out.println("Rs."+amount+" debited Successfully");
                            connection.commit();
                            connection.setAutoCommit(true);
                        }else {
                            System.out.println("Transaction Failed ");
                            connection.rollback();
                            connection.setAutoCommit(true);
                        }
                    }else {
                        System.out.println("Insufficient balance ");
                        connection.setAutoCommit(true);
                    }

                }else{
                    System.out.println("Invalid pin ");
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        connection.setAutoCommit(true);



    }
}
