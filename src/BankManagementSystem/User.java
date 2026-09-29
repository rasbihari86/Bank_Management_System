package BankManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class User {
    private final Connection connection;
    private final Scanner scanner;

    public User(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public void register() {
        System.out.println("\n========== REGISTRATION ==========");

        System.out.print("Enter your full name: ");
        String fullName = scanner.nextLine().trim();

        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            System.out.println("Name, email and password cannot be empty.");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            System.out.println("Please enter a valid email address.");
            return;
        }

        if (userExist(email)) {
            System.out.println("User already exists. Please login.");
            return;
        }

        String query = "INSERT INTO users (full_name, email, password) VALUES (?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, password);

            if (ps.executeUpdate() > 0) {
                System.out.println("Registration successful! You can now login.");
            } else {
                System.out.println("Registration failed.");
            }
        } catch (SQLException e) {
            System.out.println("Registration error: " + e.getMessage());
        }
    }

    public String login() {
        System.out.println("\n========== LOGIN ==========");

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        String query = "SELECT email FROM users WHERE email = ? AND password = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Login successful.");
                    return rs.getString("email");
                }
            }

            System.out.println("Invalid email or password.");
        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        }

        return null;
    }

    public boolean userExist(String email) {
        String query = "SELECT 1 FROM users WHERE email = ? LIMIT 1";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return false;
        }
    }
}
