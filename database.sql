CREATE DATABASE IF NOT EXISTS banking_system;
USE banking_system;

CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS accounts (
    account_number BIGINT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    security_pin VARCHAR(20) NOT NULL,
    CONSTRAINT fk_accounts_user
        FOREIGN KEY (email) REFERENCES users(email)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE INDEX idx_accounts_email ON accounts(email);
