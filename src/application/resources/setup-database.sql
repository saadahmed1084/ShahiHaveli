-- SQL script to create database and user for Shahi Haveli
-- Run this as MySQL root user: mysql -u root -p < setup-database.sql

-- Create database
CREATE DATABASE IF NOT EXISTS shahi_haveli CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create user (adjust password as needed)
CREATE USER IF NOT EXISTS 'shahi_user'@'localhost' IDENTIFIED BY 'Abdulrafay12';

-- Grant privileges
GRANT ALL PRIVILEGES ON shahi_haveli.* TO 'shahi_user'@'localhost';

-- Flush privileges to apply changes
FLUSH PRIVILEGES;

-- Use the database
USE shahi_haveli;

-- Show confirmation
SELECT 'Database shahi_haveli created successfully!' AS Status;
SELECT 'User shahi_user created with privileges!' AS Status;

