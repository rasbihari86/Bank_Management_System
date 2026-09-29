package BankManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Accounts {
    private Connection connection;
    private  Scanner sc ;



    public Accounts(Connection connection, Scanner sc) {
        this.connection = connection;
        this.sc = sc ;

    }

    public long open_account(String email){
        if(!account_exist(email)) {
            String open_account_query = "INSERT INTO Accounts(account_number, full_name, email, balance, security_pin) VALUES(?, ?, ?, ?, ?)";
            sc.nextLine();
            System.out.print("Enter Full Name: ");
            String full_name = sc.nextLine();
            System.out.print("Enter Initial Amount: ");
            double balance = sc.nextDouble();
            sc.nextLine();
            System.out.print("Enter Security Pin: ");
            String security_pin = sc.nextLine();
            try {
                long account_number =  Generate_accountNo();
                PreparedStatement preparedStatement = connection.prepareStatement(open_account_query);
                preparedStatement.setLong(1, account_number);
                preparedStatement.setString(2, full_name);
                preparedStatement.setString(3, email);
                preparedStatement.setDouble(4, balance);
                preparedStatement.setString(5, security_pin);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    return account_number;
                } else {
                    throw new RuntimeException("Account Creation failed!!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        throw new RuntimeException("Account Already Exist");

    }


    public long getAccount_number(String email) {
        String query = "SELECT account_number from Accounts WHERE email = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return resultSet.getLong("account_number");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        throw new RuntimeException("Account Number Doesn't Exist!");
    }

    private  long Generate_accountNo(){
        String sql = " SELECT account_number FROM accounts order by account_number DESC limit 1 ";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                long last_accNo = rs.getLong("account_number");
                return  last_accNo +1 ;
            }else {
                return 10000100;
            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return 10000100;

    }

    public  boolean account_exist(String email){
        String sql = "SELECT account_number FROM accounts where email = ?";
        try{
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1 ,email);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return true;
            }else {
                return  false ;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
