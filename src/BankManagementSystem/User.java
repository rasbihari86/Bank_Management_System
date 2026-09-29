package BankManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class User {
    private Connection connection;
    private Scanner sc ;


    public User(Connection connection, Scanner sc) {
        this.connection = connection;
        this.sc = sc;
    }
    public void register(){
        sc.nextLine();
        System.out.println("Enter your full name : ");
        String full_name = sc.nextLine();
        System.out.println("Email : ");
        String email = sc.nextLine();
        System.out.println("password : ");
        String password = sc.nextLine();

        if(user_exist(email)){
            System.out.println(" User already exists");
            return;
        }

        String register_query = "insert into users ( full_name , email , password) values(?,?,?)";
        try {
            PreparedStatement ps = connection.prepareStatement(register_query);
            ps.setString(1,full_name);
            ps.setString(2,email);
            ps.setString(3,password);
            int rowsaffected = ps.executeUpdate();
            if(rowsaffected>0){
                System.out.println("Registration SuccessFull ");
            }else{
                System.out.println("Registration Failed ");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
    public String login(){
        sc.nextLine();
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();
        String login_query = "SELECT * FROM User WHERE email = ? AND password = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(login_query);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return email;
            }else{
                return null;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;

    }

    public  boolean user_exist(String email){
        String sql = "select * from users where email = ?";
        try{
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1,email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                return  true;
            }else {
                return  false;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
