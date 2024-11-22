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

    private TextField usernameField;
    private PasswordField passwordField;
    private Label loginStatusLabel;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Login");

        // UI elements for the login screen
        usernameField = new TextField();
        usernameField.setPromptText("Username");

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        // Using Utils to create styled buttons
        Button loginButton = Utils.createStyledButton("Login");
        Button createAccountButton = Utils.createStyledButton("Create Account");
        Button backButton = Utils.createStyledButton("Back");
        Button forgotPasswordButton = Utils.createStyledButton("Forgot Password");

        // Using Utils to create styled label
        loginStatusLabel = Utils.createStyledLabel("");

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
        VBox layout = new VBox(10, usernameField, passwordField, loginButton, createAccountButton, forgotPasswordButton, backButton, loginStatusLabel);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 400, 300); // Increased height for better spacing
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Handle the login process
    private void handleLogin(Stage stage) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            loginStatusLabel.setText("Please enter both username and password.");
            return;
        }

        // Authenticate user
        if (authenticateUser(username, password)) {
            loginStatusLabel.setText("Login successful!");

            // Fetch user info
            String role = getUserRole(username);
            String firstName = getFirstName(username);
            String lastName = getLastName(username);
            String userId = getUserId(username);  // Fetch userId

            if (role != null) {
                switch (role) {
                    case "Admin":
                        // Pass full user info to AdminView
                        AdminView adminView = new AdminView(firstName, lastName, username, userId);
                        adminView.start(stage); // Redirect to AdminView
                        break;
                        
                    case "Seller":
                        SellerView sellerView = new SellerView(userId, userId);
                        sellerView.start(stage); // Redirect to SellerView
                        break;
                        
                    case "Buyer":
                        BuyerView buyerView = new BuyerView(userId, userId);
                        buyerView.start(stage); // Redirect to BuyerView
                        break;

                    default:
                        // Show full name for non-admin users
                        loginStatusLabel.setText("Welcome, " + firstName + " " + lastName + " (" + userId + ")!");
                        break;
                }
            }
        } else {
            loginStatusLabel.setText("Invalid username or password.");
        }
    }
    
    // Retrieve the user Id of the logged-in user
    private String getUserId(String username) {
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
    private String getFirstName(String username) {
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
    private String getLastName(String username) {
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
    private boolean authenticateUser(String username, String password) {
        String query = "SELECT password FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
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
    private String getUserRole(String username) {
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

    public static void main(String[] args) {
        launch(args);
    }
}
