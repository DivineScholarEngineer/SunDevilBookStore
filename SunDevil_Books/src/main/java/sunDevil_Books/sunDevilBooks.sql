-- Create the database (if it does not exist already)
CREATE DATABASE IF NOT EXISTS sunDevilBooks;

-- Use the created database
USE sunDevilBooks;

-- Add first_name and last_name columns
-- ALTER TABLE users ADD COLUMN last_name VARCHAR(50) NOT NULL AFTER first_name;

-- Ensure that the username is unique
-- ALTER TABLE users MODIFY COLUMN username VARCHAR(50) NOT NULL UNIQUE;

-- Add a column for created_at timestamp
-- ALTER TABLE users ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP AFTER role;

-- Verify the structure of the modified users table
-- DESCRIBE users;

-- Drop existing users table if it exists (for clean creation)
DROP TABLE IF EXISTS users;

-- SET FOREIGN_KEY_CHECKS = 0;
-- TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;
-- DELETE FROM users;

-- Create users table with first_name, last_name, username, and generated user_id
CREATE TABLE users (
    user_id VARCHAR(50) PRIMARY KEY, -- Unique user ID, e.g., JOHDOE1234
    first_name VARCHAR(50) NOT NULL, -- First name of the user
    last_name VARCHAR(50) NOT NULL,  -- Last name of the user
    username VARCHAR(50) NOT NULL UNIQUE, -- Username must be unique
    password VARCHAR(50) NOT NULL, -- Password for the user
    role ENUM('Admin', 'Buyer', 'Seller') DEFAULT 'Buyer' NOT NULL, -- Role of the user, default to 'Buyer'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- Created at timestamp
);

-- Create books table
DROP TABLE IF EXISTS books;

CREATE TABLE books (
    book_id INT AUTO_INCREMENT PRIMARY KEY, -- Unique book ID
    category VARCHAR(50) NOT NULL, -- Book category (e.g., 'Natural Science')
    name VARCHAR(100) NOT NULL, -- Book name
    author VARCHAR(100) NOT NULL, -- Book author
    publishing_year INT NOT NULL, -- Publishing year of the book
    price DECIMAL(10, 2) NOT NULL, -- Book price
    book_condition ENUM('New', 'Used', 'Heavily Used') NOT NULL -- Condition of the book
);

-- Create transactions table
DROP TABLE IF EXISTS transactions;

CREATE TABLE transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY, -- Unique transaction ID
    book_id INT NOT NULL, -- Reference to books table
    sale_price DECIMAL(10, 2) NOT NULL, -- Sale price
    buyer_id VARCHAR(50), -- Buyer ID (reference to users table)
    seller_id VARCHAR(50), -- Seller ID (reference to users table)
    transaction_date DATE NOT NULL, -- Date of the transaction
    FOREIGN KEY (book_id) REFERENCES books(book_id), -- Foreign key constraint to books
    FOREIGN KEY (buyer_id) REFERENCES users(user_id), -- Foreign key constraint to users (buyer)
    FOREIGN KEY (seller_id) REFERENCES users(user_id) -- Foreign key constraint to users (seller)
);

-- Insert default admin user (replace with desired values)
-- INSERT INTO users (user_id, first_name, last_name, username, password, role) 
-- VALUES ('DIVOLO1324', 'Admin', 'Divine', 'admin', 'admin123', 'Admin');

-- Verify the tables and structure
DESCRIBE users;
DESCRIBE books;
DESCRIBE transactions;

-- Insert Sample Data for Testing Purposes
-- INSERT INTO users (user_id, first_name, last_name, username, password, role) VALUES
-- ('DIVOLO2311', 'Divine', 'Olorunfemi', 'Divine', 'divne', 'Admin');
-- ('admin1', 'John', 'Doe', 'admin', 'password', 'Admin');


SELECT * FROM books;
SELECT * FROM users;
SELECT * FROM transactions;

-- ALTER USER 'root'@'localhost' IDENTIFIED BY 'Sql-Protect_24-2-2023-EF25';
-- SET PASSWORD FOR 'root'@'localhost' = 'Sql-Protect_24-2023-EF24';

-- ALTER DATABASE sunDevilBooks SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
-- DROP DATABASE sunDevilBooks;
-- CREATE DATABASE sunDevilBooks;

-- ======================================================
-- Optional: Insert Sample Data for Testing Purposes
-- ======================================================

-- Uncomment the following section if you want to add sample data.

-- 6. Insert Sample Users
-- INSERT INTO users (user_id, role, password) VALUES
-- ('admin1', 'Admin', 'adminpassword'),
-- ('seller1', 'Seller', 'sellerpassword'),
-- ('buyer1', 'Buyer', 'buyerpassword');

-- 7. Insert Sample Books
-- INSERT INTO books (category, name, author, publishing_year, price, `condition`) VALUES
-- ('Engineering', 'Introduction to Algorithms', 'Thomas H. Cormen', 2009, 99.99, 'New'),
-- ('Literature', 'To Kill a Mockingbird', 'Harper Lee', 1960, 19.99, 'Used');

-- 8. Insert Sample Transactions
-- INSERT INTO transactions (book_id, buyer_id, seller_id, sale_price) VALUES
-- (1, 'buyer1', 'seller1', 99.99),
-- (2, 'buyer1', 'seller1', 19.99);
