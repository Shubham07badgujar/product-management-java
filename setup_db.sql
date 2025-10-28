-- MySQL Database Setup Script for Product Management System

-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS product_management;

-- Use the database
USE product_management;

-- Create products table
CREATE TABLE IF NOT EXISTS products (
    id INT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    quantity INT NOT NULL CHECK (quantity >= 0),
    category VARCHAR(100) DEFAULT 'General',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Add category column if it doesn't exist (for existing databases)
ALTER TABLE products ADD COLUMN IF NOT EXISTS category VARCHAR(100) DEFAULT 'General';

-- Drop and recreate users table with password column
DROP TABLE IF EXISTS users;

-- Create users table with password
CREATE TABLE users (
    id INT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert sample data for products
INSERT IGNORE INTO products (id, name, price, quantity, category) VALUES
(1, 'Laptop', 999.99, 5, 'Electronics'),
(2, 'Mouse', 25.50, 20, 'Electronics'),
(3, 'Keyboard', 75.00, 10, 'Electronics');

-- Insert sample data for users with passwords
INSERT INTO users (id, username, email, first_name, last_name, password, phone_number) VALUES
(1, 'john_doe', 'john.doe@email.com', 'John', 'Doe', 'password123', '123-456-7890'),
(2, 'jane_smith', 'jane.smith@email.com', 'Jane', 'Smith', 'secure456', '987-654-3210'),
(3, 'bob_johnson', 'bob.johnson@email.com', 'Bob', 'Johnson', 'bobpass789', NULL),
(4, 'alice_brown', 'alice.brown@email.com', 'Alice', 'Brown', 'alice2024', '555-123-4567');

-- Display created tables
SHOW TABLES;

-- Display table structures
DESCRIBE products;
DESCRIBE users;

-- Display sample data
SELECT 'Products Table:' as Info;
SELECT * FROM products;
SELECT 'Users Table:' as Info;
SELECT * FROM users;