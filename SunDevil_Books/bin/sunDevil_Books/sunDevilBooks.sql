CREATE DATABASE IF NOT EXISTS sunDevilBooks
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE sunDevilBooks;

CREATE TABLE users (
    user_id VARCHAR(50) PRIMARY KEY, -- Unique user ID, e.g., JOHDOE1234
    first_name VARCHAR(50) NOT NULL, -- First name of the user
    last_name VARCHAR(50) NOT NULL,  -- Last name of the user
    username VARCHAR(50) NOT NULL UNIQUE, -- Username must be unique
    password VARCHAR(50) NOT NULL, -- Password for the user
    role ENUM('Admin', 'Buyer', 'Seller') DEFAULT 'Buyer' NOT NULL, -- Role of the user, default to 'Buyer'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- Created at timestamp
);
DROP TABLE IF EXISTS users; 
SET FOREIGN_KEY_CHECKS = 0;


CREATE TABLE IF NOT EXISTS books (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    category ENUM('Natural Science', 'Engineering', 'Literature', 'Other') NOT NULL,
    name VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    publishing_year YEAR NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    book_condition ENUM('New', 'Used', 'Heavily Used') NOT NULL,  -- Renamed the column
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    buyer_id VARCHAR(50) NOT NULL,
    seller_id VARCHAR(50) NOT NULL,
    sale_price DECIMAL(10,2) NOT NULL,
    sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE,
    FOREIGN KEY (buyer_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (seller_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

SELECT * FROM books;
SELECT * FROM users;
SELECT * FROM transactions;

-- ALTER USER 'root'@'localhost' IDENTIFIED BY 'Sql-Protect_21-2023-EF24';
SET PASSWORD FOR 'root'@'localhost' = 'Sql-Protect_24-2023-EF24';

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
