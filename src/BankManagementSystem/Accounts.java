package BankManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Accounts {
    private final Connection connection;
    private final Scanner scanner;

    public Accounts(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public long openAccount(String email) {
        if (accountExist(email)) {
            System.out.println("An account already exists for this email.");
            return getAccountNumber(email);
        }

        System.out.println("\n========== CREATE BANK ACCOUNT ==========");

        System.out.print("Enter full name: ");
        String fullName = scanner.nextLine().trim();

        if (fullName.isEmpty()) {
            System.out.println("Full name cannot be empty.");
            return -1;
        }

        double balance = readAmount("Enter initial amount: ");

        System.out.print("Enter security PIN: ");
        String securityPin = scanner.nextLine().trim();

        if (securityPin.length() < 4) {
            System.out.println("Security PIN must contain at least 4 characters.");
            return -1;
        }

        String query = "INSERT INTO accounts " +
                "(account_number, full_name, email, balance, security_pin) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            long accountNumber = generateAccountNumber();

            ps.setLong(1, accountNumber);
            ps.setString(2, fullName);
            ps.setString(3, email);
            ps.setDouble(4, balance);
            ps.setString(5, securityPin);

            if (ps.executeUpdate() > 0) {
                return accountNumber;
            }
        } catch (SQLException e) {
            System.out.println("Account creation failed: " + e.getMessage());
        }

        return -1;
    }

    public long getAccountNumber(String email) {
        String query = "SELECT account_number FROM accounts WHERE email = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("account_number");
                }
            }
        } catch (SQLException e) {
            System.out.println("Unable to fetch account number: " + e.getMessage());
        }

        return -1;
    }

    public boolean accountExist(String email) {
        String query = "SELECT 1 FROM accounts WHERE email = ? LIMIT 1";

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

    public void showAccountDetails(long accountNumber) {
        String query = "SELECT account_number, full_name, email, balance FROM accounts WHERE account_number = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setLong(1, accountNumber);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\n========== ACCOUNT DETAILS ==========");
                    System.out.println("Account Number : " + rs.getLong("account_number"));
                    System.out.println("Full Name      : " + rs.getString("full_name"));
                    System.out.println("Email          : " + rs.getString("email"));
                    System.out.printf("Balance        : Rs. %.2f%n", rs.getDouble("balance"));
                } else {
                    System.out.println("Account not found.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Unable to fetch account details: " + e.getMessage());
        }
    }

    private long generateAccountNumber() throws SQLException {
        String query = "SELECT COALESCE(MAX(account_number), 10000100) + 1 AS next_account FROM accounts";

        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getLong("next_account");
            }
        }

        return 10000101;
    }

    private double readAmount(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();

            try {
                double amount = Double.parseDouble(input);
                if (amount >= 0) {
                    return amount;
                }
            } catch (NumberFormatException ignored) {
            }

            System.out.println("Please enter a valid non-negative amount.");
        }
    }
}
