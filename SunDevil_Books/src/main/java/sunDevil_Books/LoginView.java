package sunDevil_Books;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.*;

public class LoginView extends Application {

    private TextField userIdField;
    private PasswordField passwordField;
    private Label loginStatusLabel;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Login");

        // UI elements for the login screen
        userIdField = new TextField();
        userIdField.setPromptText("User ID");

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");
        Button createAccountButton = new Button("Create Account");
        Button backButton = new Button("Back");
        Button forgotPasswordButton = new Button("Forgot Password");


        loginStatusLabel = new Label();

        // Event for login button
        loginButton.setOnAction(e -> handleLogin(primaryStage));

        // Event for create account button
        createAccountButton.setOnAction(e -> {
            CreateAccountView createAccountView = new CreateAccountView();
            createAccountView.start(primaryStage); // Switch to Create Account View
        });

        // Event for back button
        backButton.setOnAction(e -> {
            SplashScreenView splashScreenView = new SplashScreenView();
            splashScreenView.start(primaryStage); // Switch back to splash screen
        });
        
        // Event for forgot password
        forgotPasswordButton.setOnAction(e -> {
            ForgotPasswordView forgotPasswordView = new ForgotPasswordView();
            forgotPasswordView.start(primaryStage); // Switch to Forgot Password view
        });


        // Layout for the login screen
        VBox layout = new VBox(10, userIdField, passwordField, loginButton, createAccountButton, forgotPasswordButton, backButton, loginStatusLabel);;
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 400, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Handle the login process
    private void handleLogin(Stage stage) {
        String userId = userIdField.getText();
        String password = passwordField.getText();

        if (userId.isEmpty() || password.isEmpty()) {
            loginStatusLabel.setText("Please enter both user ID and password.");
            return;
        }

        // Call the function to check login
        if (authenticateUser(userId, password)) {
            loginStatusLabel.setText("Login successful!");

            // Fetch role and redirect accordingly
         // Fetch username and role and redirect accordingly
         // Fetch first name, last name, username, and role, and redirect accordingly
            // Call the function to check login
            if (authenticateUser(userId, password)) {
                loginStatusLabel.setText("Login successful!");

                // Fetch user info
                String role = getUserRole(userId);
                String firstName = getFirstName(userId);
                String lastName = getLastName(userId);
                String username = getUsername(userId);  // Fetch username

                if (role != null) {
                    switch (role) {
                        case "Admin":
                            // Pass full user info to AdminView
                            AdminView adminView = new AdminView(firstName, lastName, username, userId);
                            adminView.start(stage); // Redirect to AdminView
                            break;
                        default:
                            // Show full name for non-admin users
                            loginStatusLabel.setText("Welcome, " + firstName + " " + lastName + " (" + userId + ")!");
                            break;
                    }
                }
            } else {
                loginStatusLabel.setText("Invalid user ID or password.");
            }
        }
    }
    
 // Retrieve the username of the logged-in user
    private String getUsername(String userId) {
        String query = "SELECT username FROM users WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if no username found or error occurred
    }
    
 // Retrieve the first name of the logged-in user
    private String getFirstName(String userId) {
        String query = "SELECT first_name FROM users WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userId);
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
    private String getLastName(String userId) {
        String query = "SELECT last_name FROM users WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userId);
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
    private boolean authenticateUser(String userId, String password) {
        String query = "SELECT password FROM users WHERE user_id = ?";
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

    // Retrieve the role of the logged-in user
    private String getUserRole(String userId) {
        String query = "SELECT role FROM users WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("role");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if no role found or error occurred
    }

    public static void main(String[] args) {
        launch(args);
    }
}
