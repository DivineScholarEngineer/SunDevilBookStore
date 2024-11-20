package sunDevil_Books;

import java.sql.*;
import javafx.scene.control.Alert;

public class DatabaseOperations {
	public static void initializeDatabase() {
		try {
	        Class.forName(DatabaseConfig.JDBC_DRIVER);

	        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD)) {
	            System.out.println("Connected to the database successfully.");
	        }
	    } catch (ClassNotFoundException e) {
	        Utils.showAlert(Alert.AlertType.ERROR, "JDBC Driver not found: " + e.getMessage());
	    } catch (SQLException e) {
	        Utils.showAlert(Alert.AlertType.ERROR, "Database connection error: " + e.getMessage());
	    }
    }

    // Method to create a new user account
    public static void createAccount(String username, String password, String role) {
        String query = "INSERT INTO users (user_id, username, role, password) VALUES (UUID(), ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, username);
            statement.setString(2, role);
            statement.setString(3, password);
            statement.executeUpdate();
            System.out.println("Account created successfully");
        } catch (SQLException e) {
            System.out.println("Error creating account: " + e.getMessage());
        }
    }

    // Method to log in the user and return their role
    public static String login(String username, String password) {
        String query = "SELECT role FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getString("role");
            } else {
                return null; // Invalid login
            }
        } catch (SQLException e) {
            System.out.println("Error during login: " + e.getMessage());
            return null;
        }
    }

    // Method to remove a user
    public static void removeUser(String userId) {
        String query = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, userId);
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("User removed successfully.");
            } else {
                System.out.println("User ID not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error removing user: " + e.getMessage());
        }
    }
    
    // Retrieve the role of the logged-in user
    public static String getUserRole(String username) {
        String query = "SELECT role FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("role");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if no role found or error occurred
    }
    
    // Retrieve the user Id of the logged-in user
    public static String getUserId(String username) {
        String query = "SELECT user_id FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("user_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if no username found or error occurred
    }
    
 // Retrieve the first name of the logged-in user
    public static String getFirstName(String username) {
        String query = "SELECT first_name FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("first_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if no first name found or error occurred
    }
    
 // Retrieve the last name of the logged-in user
    public static String getLastName(String username) {
        String query = "SELECT last_name FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("last_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if no last name found or error occurred
    }

    // Authenticate user by checking the database for matching credentials
    public static boolean authenticateUser(String userId, String password) {
        String query = "SELECT password FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return storedPassword.equals(password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // No match or error occurred
    }
}
