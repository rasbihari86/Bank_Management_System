package BankManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AccountManager {
    private final Connection connection;
    private final Scanner scanner;

    public AccountManager(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public void transferMoney(long senderAccountNumber) {
        System.out.println("\n========== TRANSFER MONEY ==========");

        long receiverAccountNumber = readLong("Enter receiver account number: ");

        if (receiverAccountNumber == senderAccountNumber) {
            System.out.println("You cannot transfer money to your own account.");
            return;
        }

        double amount = readAmount("Enter amount: ");
        if (amount <= 0) {
            System.out.println("Amount must be greater than 0.");
            return;
        }

        System.out.print("Enter security PIN: ");
        String pin = scanner.nextLine().trim();

        String balanceQuery = "SELECT balance FROM accounts WHERE account_number = ? AND security_pin = ?";
        String receiverQuery = "SELECT 1 FROM accounts WHERE account_number = ?";
        String debitQuery = "UPDATE accounts SET balance = balance - ? WHERE account_number = ? AND balance >= ?";
        String creditQuery = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement balancePs = connection.prepareStatement(balanceQuery);
                 PreparedStatement receiverPs = connection.prepareStatement(receiverQuery);
                 PreparedStatement debitPs = connection.prepareStatement(debitQuery);
                 PreparedStatement creditPs = connection.prepareStatement(creditQuery)) {

                balancePs.setLong(1, senderAccountNumber);
                balancePs.setString(2, pin);

                try (ResultSet rs = balancePs.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("Invalid security PIN.");
                        connection.rollback();
                        return;
                    }
                }

                receiverPs.setLong(1, receiverAccountNumber);
                try (ResultSet rs = receiverPs.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("Receiver account does not exist.");
                        connection.rollback();
                        return;
                    }
                }

                debitPs.setDouble(1, amount);
                debitPs.setLong(2, senderAccountNumber);
                debitPs.setDouble(3, amount);

                if (debitPs.executeUpdate() == 0) {
                    System.out.println("Insufficient balance.");
                    connection.rollback();
                    return;
                }

                creditPs.setDouble(1, amount);
                creditPs.setLong(2, receiverAccountNumber);

                if (creditPs.executeUpdate() == 0) {
                    System.out.println("Transfer failed.");
                    connection.rollback();
                    return;
                }

                connection.commit();
                System.out.printf("Rs. %.2f transferred successfully.%n", amount);
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ignored) {
            }
            System.out.println("Transfer failed: " + e.getMessage());
        } finally {
            resetAutoCommit();
        }
    }

    public void getBalance(long accountNumber) {
        System.out.println("\n========== CHECK BALANCE ==========");

        if (!verifyPin(accountNumber)) {
            return;
        }

        String query = "SELECT balance FROM accounts WHERE account_number = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setLong(1, accountNumber);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.printf("Available Balance: Rs. %.2f%n", rs.getDouble("balance"));
                } else {
                    System.out.println("Account not found.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Unable to fetch balance: " + e.getMessage());
        }
    }

    public void creditMoney(long accountNumber) {
        System.out.println("\n========== DEPOSIT MONEY ==========");

        double amount = readAmount("Enter amount: ");
        if (amount <= 0) {
            System.out.println("Amount must be greater than 0.");
            return;
        }

        if (!verifyPin(accountNumber)) {
            return;
        }

        String query = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setDouble(1, amount);
            ps.setLong(2, accountNumber);

            if (ps.executeUpdate() > 0) {
                System.out.printf("Rs. %.2f deposited successfully.%n", amount);
            } else {
                System.out.println("Deposit failed.");
            }
        } catch (SQLException e) {
            System.out.println("Deposit failed: " + e.getMessage());
        }
    }

    public void debitMoney(long accountNumber) {
        System.out.println("\n========== WITHDRAW MONEY ==========");

        double amount = readAmount("Enter amount: ");
        if (amount <= 0) {
            System.out.println("Amount must be greater than 0.");
            return;
        }

        if (!verifyPin(accountNumber)) {
            return;
        }

        String query = "UPDATE accounts SET balance = balance - ? WHERE account_number = ? AND balance >= ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setDouble(1, amount);
            ps.setLong(2, accountNumber);
            ps.setDouble(3, amount);

            if (ps.executeUpdate() > 0) {
                System.out.printf("Rs. %.2f withdrawn successfully.%n", amount);
            } else {
                System.out.println("Insufficient balance.");
            }
        } catch (SQLException e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
        }
    }

    private boolean verifyPin(long accountNumber) {
        System.out.print("Enter security PIN: ");
        String pin = scanner.nextLine().trim();

        String query = "SELECT 1 FROM accounts WHERE account_number = ? AND security_pin = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setLong(1, accountNumber);
            ps.setString(2, pin);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }

            System.out.println("Invalid security PIN.");
        } catch (SQLException e) {
            System.out.println("Unable to verify PIN: " + e.getMessage());
        }

        return false;
    }

    private long readLong(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();

            try {
                long value = Long.parseLong(input);
                if (value > 0) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
            }

            System.out.println("Please enter a valid account number.");
        }
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

            System.out.println("Please enter a valid amount.");
        }
    }

    private void resetAutoCommit() {
        try {
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            System.out.println("Could not reset database transaction mode.");
        }
    }
}
