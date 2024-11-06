package sunDevil_Books; // Replace with your actual package name

import java.sql.*;

public class DatabaseOperations {

    // Method to create a new user account
    public void createAccount(String username, String password, String role) {
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
    public String login(String username, String password) {
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
    public void removeUser(String userId) {
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
}
