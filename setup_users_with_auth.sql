-- SQL script to create/update the users table with authentication support
-- Run this script in your MySQL database

-- Drop existing table if you want a fresh start
-- DROP TABLE IF EXISTS users;

-- Create users table with authentication fields
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone_number VARCHAR(20),
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'User',
    verified TINYINT(1) NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_username (username),
    INDEX idx_role (role)
);

-- If table already exists, add missing columns
ALTER TABLE users 
ADD COLUMN IF NOT EXISTS password VARCHAR(255) NOT NULL DEFAULT 'password123',
ADD COLUMN IF NOT EXISTS role VARCHAR(20) NOT NULL DEFAULT 'User',
ADD COLUMN IF NOT EXISTS verified TINYINT(1) NOT NULL DEFAULT 0;

-- Insert sample users for testing
-- Admin user
INSERT INTO users (username, email, first_name, last_name, phone_number, password, role, verified) 
VALUES ('admin_user', 'admin@test.com', 'Admin', 'User', '1234567890', 'admin123', 'Admin', 1)
ON DUPLICATE KEY UPDATE username=username;

-- Regular user
INSERT INTO users (username, email, first_name, last_name, phone_number, password, role, verified) 
VALUES ('test_user', 'user@test.com', 'Test', 'User', '0987654321', 'user123', 'User', 0)
ON DUPLICATE KEY UPDATE username=username;

-- Display created users
SELECT id, username, email, first_name, last_name, role, verified, created_at FROM users;
