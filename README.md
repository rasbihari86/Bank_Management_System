# Bank Management System

A console-based **Bank Management System** built using **Core Java, JDBC and MySQL**.

## Features

- User registration
- User login
- Automatic bank-account creation after login
- Unique account number generation
- Check account balance
- Deposit money
- Withdraw money
- Transfer money between accounts
- Security PIN verification
- Account details
- Input validation
- JDBC PreparedStatement
- Database transactions with commit/rollback
- Logout

## Technologies

- Java
- JDBC
- MySQL
- IntelliJ IDEA / Eclipse

## Project Structure

```
src/
└── BankManagementSystem/
    ├── BankingApp.java
    ├── User.java
    ├── Accounts.java
    └── AccountManager.java

database.sql
README.md
```

## Database Setup

1. Start MySQL.
2. Open `database.sql` in MySQL Workbench.
3. Run the complete script.
4. The script creates the `banking_system` database and the required `users` and `accounts` tables.
5. In `BankingApp.java`, update these values if your MySQL credentials are different:

```java
private static final String URL = "jdbc:mysql://localhost:3306/banking_system";
private static final String USERNAME = "root";
private static final String PASSWORD = "password";
```

> For a real production application, passwords and database credentials should not be stored directly in source code. This project keeps them simple for learning purposes.

## How to Run

1. Install Java JDK.
2. Install MySQL Server.
3. Add **MySQL Connector/J** to the Java project.
4. Run `database.sql`.
5. Update the database username/password in `BankingApp.java`.
6. Run `BankingApp.java`.
7. Register a user.
8. Login.
9. If the user has no bank account, the application asks for account details and creates one.
10. Use the account menu to deposit, withdraw, transfer and check balance.

## Application Flow

```
Start
  |
  v
Register / Login
  |
  v
Check Bank Account
  |
  +---- No account ----> Create Account
  |
  v
Account Menu
  |
  +--> Check Balance
  +--> Deposit
  +--> Withdraw
  +--> Transfer
  +--> Account Details
  +--> Logout
```

## Important JDBC Concepts Used

- Connection
- PreparedStatement
- ResultSet
- Try-with-resources
- SQL CRUD operations
- Transactions
- `setAutoCommit(false)`
- `commit()`
- `rollback()`

## Note

This is an educational console project. A production banking application would additionally require password hashing, secure PIN handling, authentication controls, audit logs, transaction history, concurrency protection and proper externalized configuration.
