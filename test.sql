-- test.sql - SQL Practice Questions and Answers
-- Product Management System - SQL Test File
-- Date: September 16, 2025

-- Sample Data Setup
-- First, let's create a test table with category column for these exercises

USE product_management; 

DROP TABLE IF EXISTS products_test;
CREATE TABLE products_test (
    id INT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL
);

-- Insert sample data
INSERT INTO products_test VALUES
(1, 'Laptop', 'Electronics', 10, 55000),
(2, 'Chair', 'Furniture', 20, 2500),
(3, 'Pen', 'Stationery', 100, 10),
(4, 'Phone', 'Electronics', 15, 30000),
(5, 'Table', 'Furniture', 5, 8000);

-- ==========================================
-- SQL PRACTICE QUESTIONS AND ANSWERS
-- ==========================================

-- Question 1: Show all products in the inventory.
-- Query:
SELECT * FROM products_test;

-- Answer:
-- +----+--------+-------------+----------+----------+
-- | id | name   | category    | quantity | price    |
-- +----+--------+-------------+----------+----------+
-- |  1 | Laptop | Electronics |       10 | 55000.00 |
-- |  2 | Chair  | Furniture   |       20 |  2500.00 |
-- |  3 | Pen    | Stationery  |      100 |    10.00 |
-- |  4 | Phone  | Electronics |       15 | 30000.00 |
-- |  5 | Table  | Furniture   |        5 |  8000.00 |
-- +----+--------+-------------+----------+----------+

-- ==========================================

-- Question 2: Show only product names and categories.
-- Query:
SELECT name, category FROM products_test;

-- Answer:
-- +--------+-------------+
-- | name   | category    |
-- +--------+-------------+
-- | Laptop | Electronics |
-- | Chair  | Furniture   |
-- | Pen    | Stationery  |
-- | Phone  | Electronics |
-- | Table  | Furniture   |
-- +--------+-------------+

-- ==========================================

-- Question 3: Find products where quantity is more than 10.
-- Query:
SELECT * FROM products_test WHERE quantity > 10;

-- Answer:
-- +----+-------+-------------+----------+----------+
-- | id | name  | category    | quantity | price    |
-- +----+-------+-------------+----------+----------+
-- |  2 | Chair | Furniture   |       20 |  2500.00 |
-- |  3 | Pen   | Stationery  |      100 |    10.00 |
-- |  4 | Phone | Electronics |       15 | 30000.00 |
-- +----+-------+-------------+----------+----------+

-- ==========================================

-- Question 4: Find products where price is less than 5000.
-- Query:
SELECT * FROM products_test WHERE price < 5000;

-- Answer:
-- +----+-------+-----------+----------+---------+
-- | id | name  | category  | quantity | price   |
-- +----+-------+-----------+----------+---------+
-- |  2 | Chair | Furniture |       20 | 2500.00 |
-- |  3 | Pen   | Stationery|      100 |   10.00 |
-- +----+-------+-----------+----------+---------+

-- ==========================================

-- Question 5: Show all Electronics products.
-- Query:
SELECT * FROM products_test WHERE category = 'Electronics';

-- Answer:
-- +----+--------+-------------+----------+----------+
-- | id | name   | category    | quantity | price    |
-- +----+--------+-------------+----------+----------+
-- |  1 | Laptop | Electronics |       10 | 55000.00 |
-- |  4 | Phone  | Electronics |       15 | 30000.00 |
-- +----+--------+-------------+----------+----------+

-- ==========================================

-- Question 6: Show all products sorted by price (highest first).
-- Query:
SELECT * FROM products_test ORDER BY price DESC;

-- Answer:
-- +----+--------+-------------+----------+----------+
-- | id | name   | category    | quantity | price    |
-- +----+--------+-------------+----------+----------+
-- |  1 | Laptop | Electronics |       10 | 55000.00 |
-- |  4 | Phone  | Electronics |       15 | 30000.00 |
-- |  5 | Table  | Furniture   |        5 |  8000.00 |
-- |  2 | Chair  | Furniture   |       20 |  2500.00 |
-- |  3 | Pen    | Stationery  |      100 |    10.00 |
-- +----+--------+-------------+----------+----------+

-- ==========================================

-- Question 7: Show the top 3 most expensive products.
-- Query:
SELECT * FROM products_test ORDER BY price DESC LIMIT 3;

-- Answer:
-- +----+--------+-------------+----------+----------+
-- | id | name   | category    | quantity | price    |
-- +----+--------+-------------+----------+----------+
-- |  1 | Laptop | Electronics |       10 | 55000.00 |
-- |  4 | Phone  | Electronics |       15 | 30000.00 |
-- |  5 | Table  | Furniture   |        5 |  8000.00 |
-- +----+--------+-------------+----------+----------+

-- ==========================================

-- Question 8: Find the total number of products (sum of quantity).
-- Query:
SELECT SUM(quantity) as total_quantity FROM products_test;

-- Answer:
-- +----------------+
-- | total_quantity |
-- +----------------+
-- |            150 |
-- +----------------+

-- ==========================================

-- Question 9: Find the average price of products.
-- Query:
SELECT AVG(price) as average_price FROM products_test;

-- Answer:
-- +---------------+
-- | average_price |
-- +---------------+
-- |     19102.00  |
-- +---------------+

-- ==========================================

-- Question 10: Find the highest priced product.
-- Query:
SELECT * FROM products_test WHERE price = (SELECT MAX(price) FROM products_test);

-- Alternative Query:
SELECT * FROM products_test ORDER BY price DESC LIMIT 1;

-- Answer:
-- +----+--------+-------------+----------+----------+
-- | id | name   | category    | quantity | price    |
-- +----+--------+-------------+----------+----------+
-- |  1 | Laptop | Electronics |       10 | 55000.00 |
-- +----+--------+-------------+----------+----------+

-- ==========================================
-- ADDITIONAL PRACTICE QUERIES
-- ==========================================

-- Question 11: Find the lowest priced product.
-- Query:
SELECT * FROM products_test WHERE price = (SELECT MIN(price) FROM products_test);

-- Answer:
-- +----+------+------------+----------+-------+
-- | id | name | category   | quantity | price |
-- +----+------+------------+----------+-------+
-- |  3 | Pen  | Stationery |      100 | 10.00 |
-- +----+------+------------+----------+-------+

-- Question 12: Count products by category.
-- Query:
SELECT category, COUNT(*) as product_count FROM products_test GROUP BY category;

-- Answer:
-- +-------------+---------------+
-- | category    | product_count |
-- +-------------+---------------+
-- | Electronics |             2 |
-- | Furniture   |             2 |
-- | Stationery  |             1 |
-- +-------------+---------------+

-- Question 13: Find total value of inventory (quantity * price) for each product.
-- Query:
SELECT name, quantity, price, (quantity * price) as total_value 
FROM products_test ORDER BY total_value DESC;

-- Answer:
-- +--------+----------+----------+-------------+
-- | name   | quantity | price    | total_value |
-- +--------+----------+----------+-------------+
-- | Laptop |       10 | 55000.00 |   550000.00 |
-- | Phone  |       15 | 30000.00 |   450000.00 |
-- | Chair  |       20 |  2500.00 |    50000.00 |
-- | Table  |        5 |  8000.00 |    40000.00 |
-- | Pen    |      100 |    10.00 |     1000.00 |
-- +--------+----------+----------+-------------+

-- Question 14: Find products with quantity between 10 and 50.
-- Query:
SELECT * FROM products_test WHERE quantity BETWEEN 10 AND 50;

-- Answer:
-- +----+--------+-------------+----------+----------+
-- | id | name   | category    | quantity | price    |
-- +----+--------+-------------+----------+----------+
-- |  1 | Laptop | Electronics |       10 | 55000.00 |
-- |  2 | Chair  | Furniture   |       20 |  2500.00 |
-- |  4 | Phone  | Electronics |       15 | 30000.00 |
-- +----+--------+-------------+----------+----------+

-- Question 15: Find the category with the highest total inventory value.
-- Query:
SELECT category, SUM(quantity * price) as total_category_value 
FROM products_test 
GROUP BY category 
ORDER BY total_category_value DESC 
LIMIT 1;

-- Answer:
-- +-------------+----------------------+
-- | category    | total_category_value |
-- +-------------+----------------------+
-- | Electronics |          1000000.00  |
-- +-------------+----------------------+

-- ==========================================
-- CLEANUP
-- ==========================================

-- Clean up test table (optional)
-- DROP TABLE products_test;

-- ==========================================
-- NOTES FOR IMPLEMENTATION
-- ==========================================

-- IMPORTANT: The above examples use a test table with sample data including categories.
-- The actual products table in your application has different data and no category column.

-- Current products table structure:
-- CREATE TABLE products (
--     id INT PRIMARY KEY,
--     name VARCHAR(255) NOT NULL,
--     price DECIMAL(10, 2) NOT NULL,
--     quantity INT NOT NULL,
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
-- );

-- ACTUAL DATA IN YOUR DATABASE (as of September 16, 2025):
-- +----+----------+-------+----------+---------------------+---------------------+
-- | id | name     | price | quantity | created_at          | updated_at          |
-- +----+----------+-------+----------+---------------------+---------------------+
-- |  2 | Mouse    | 25.50 |       20 | 2025-09-15 20:53:07 | 2025-09-15 20:53:07 |
-- |  3 | Keyboard | 75.00 |       20 | 2025-09-15 20:53:07 | 2025-09-15 20:54:13 |
-- +----+----------+-------+----------+---------------------+---------------------+

-- QUERIES WITH ACTUAL DATA RESULTS:

-- Question 8: Find the total number of products (sum of quantity).
-- Query: SELECT SUM(quantity) as total_quantity FROM products;
-- Actual Answer: 40

-- Question 9: Find the average price of products.
-- Query: SELECT AVG(price) as average_price FROM products;
-- Actual Answer: 50.25

-- Question 10: Find the highest priced product.
-- Query: SELECT * FROM products WHERE price = (SELECT MAX(price) FROM products);
-- Actual Answer: Keyboard (75.00)

-- To implement categories in the main application, you would need to:
-- 1. ALTER TABLE products ADD COLUMN category VARCHAR(100) NOT NULL DEFAULT 'General';
-- 2. Update existing products with appropriate categories
-- 3. Modify the Product.java model class to include category field
-- 4. Update DAO methods to handle category operations

-- WORKING QUERIES FOR YOUR CURRENT DATABASE:
-- Use these queries with your actual product_management database:

USE product_management;

-- 1. Show all products in the inventory
SELECT * FROM products;

-- 2. Show only product names (no categories available)
SELECT name FROM products;

-- 3. Find products where quantity is more than 10
SELECT * FROM products WHERE quantity > 10;

-- 4. Find products where price is less than 5000
SELECT * FROM products WHERE price < 5000;

-- 5. Show all Electronics products (N/A - no category column)
-- Would need: SELECT * FROM products WHERE category = 'Electronics';

-- 6. Show all products sorted by price (highest first)
SELECT * FROM products ORDER BY price DESC;

-- 7. Show the top 3 most expensive products
SELECT * FROM products ORDER BY price DESC LIMIT 3;

-- 8. Find the total number of products (sum of quantity)
SELECT SUM(quantity) as total_quantity FROM products;

-- 9. Find the average price of products
SELECT AVG(price) as average_price FROM products;

-- 10. Find the highest priced product
SELECT * FROM products WHERE price = (SELECT MAX(price) FROM products);