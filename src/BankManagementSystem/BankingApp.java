package BankManagementSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class BankingApp {

    private static final String URL = "jdbc:mysql://localhost:3306/banking_system";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "password";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                 Scanner scanner = new Scanner(System.in)) {

                User user = new User(connection, scanner);
                Accounts accounts = new Accounts(connection, scanner);
                AccountManager accountManager = new AccountManager(connection, scanner);

                runApplication(connection, scanner, user, accounts, accountManager);
            }
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found.");
            System.out.println("Add MySQL Connector/J to your project.");
        } catch (SQLException e) {
            System.out.println("Unable to connect to the database.");
            System.out.println("Check MySQL is running and verify database credentials.");
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void runApplication(Connection connection, Scanner scanner,
                                       User user, Accounts accounts,
                                       AccountManager accountManager) throws SQLException {
        boolean running = true;

        while (running) {
            System.out.println("\n========== BANK MANAGEMENT SYSTEM ==========");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            int choice = readInt(scanner);

            switch (choice) {
                case 1:
                    user.register();
                    break;

                case 2:
                    String email = user.login();
                    if (email != null) {
                        userMenu(connection, scanner, email, accounts, accountManager);
                    }
                    break;

                case 3:
                    running = false;
                    System.out.println("Thank you for using Bank Management System!");
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void userMenu(Connection connection, Scanner scanner, String email,
                                 Accounts accounts, AccountManager accountManager) throws SQLException {
        long accountNumber;

        if (accounts.accountExist(email)) {
            accountNumber = accounts.getAccountNumber(email);
            System.out.println("\nLogin successful!");
            System.out.println("Your account number: " + accountNumber);
        } else {
            System.out.println("\nNo bank account is linked to this email.");
            System.out.println("Let's create your bank account.");
            accountNumber = accounts.openAccount(email);
            System.out.println("Account created successfully!");
            System.out.println("Your account number: " + accountNumber);
        }

        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n============== ACCOUNT MENU ==============");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit Money");
            System.out.println("3. Withdraw Money");
            System.out.println("4. Transfer Money");
            System.out.println("5. Account Details");
            System.out.println("6. Logout");
            System.out.print("Enter your choice: ");

            int choice = readInt(scanner);

            switch (choice) {
                case 1:
                    accountManager.getBalance(accountNumber);
                    break;

                case 2:
                    accountManager.creditMoney(accountNumber);
                    break;

                case 3:
                    accountManager.debitMoney(accountNumber);
                    break;

                case 4:
                    accountManager.transferMoney(accountNumber);
                    break;

                case 5:
                    accounts.showAccountDetails(accountNumber);
                    break;

                case 6:
                    loggedIn = false;
                    System.out.println("Logged out successfully.");
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static int readInt(Scanner scanner) {
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }
}
