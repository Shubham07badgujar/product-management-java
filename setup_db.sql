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
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert sample data
INSERT IGNORE INTO products (id, name, price, quantity) VALUES
(1, 'Laptop', 999.99, 5),
(2, 'Mouse', 25.50, 20),
(3, 'Keyboard', 75.00, 10);

-- Display created tables
SHOW TABLES;

-- Display products table structure
DESCRIBE products;

-- Display sample data
SELECT * FROM products;