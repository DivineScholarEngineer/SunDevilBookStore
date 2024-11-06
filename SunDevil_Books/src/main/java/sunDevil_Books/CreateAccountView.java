package sunDevil_Books;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class CreateAccountView extends Application {

    private TextField firstNameField;
    private TextField lastNameField;
    private TextField usernameField;
    private PasswordField passwordField;
    private Label accountStatusLabel;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Create Account");

        // UI elements for account creation screen
        firstNameField = new TextField();
        firstNameField.setPromptText("First Name");

        lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");

        usernameField = new TextField();
        usernameField.setPromptText("Username");

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button createAccountButton = new Button("Create Account");
        Button backButton = new Button("Back");

        accountStatusLabel = new Label();

        // Event for create account button
        createAccountButton.setOnAction(e -> handleCreateAccount());

        // Event to return to splash screen
        backButton.setOnAction(e -> {
            SplashScreenView splashScreenView = new SplashScreenView();
            splashScreenView.start(primaryStage); // Go back to splash screen
        });

        // Layout for the create account screen
        VBox layout = new VBox(10, firstNameField, lastNameField, usernameField, passwordField, createAccountButton, backButton, accountStatusLabel);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Handle the creation of a new account
    private void handleCreateAccount() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            accountStatusLabel.setText("Please fill in all fields.");
            return;
        }

        // Insert the new user into the database with the role of Buyer by default
        if (createUser(firstName, lastName, username, password)) {
            accountStatusLabel.setText("Account created successfully! You can now log in.");
        } else {
            accountStatusLabel.setText("Error: Username may already exist.");
        }
    }

    // Create a new user in the database
    private boolean createUser(String firstName, String lastName, String username, String password) {
        String checkQuery = "SELECT * FROM users WHERE username = ?";
        String insertQuery = "INSERT INTO users (user_id, first_name, last_name, username, password, role) VALUES (?, ?, ?, ?, ?, 'Buyer')"; // Default role: Buyer
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {

            // Check if username already exists
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                // Username already exists
                return false;
            }

            // Generate a unique user ID
            String userId = generateUserId(firstName, lastName);

            // Insert new user into the database
            insertStmt.setString(1, userId);
            insertStmt.setString(2, firstName);
            insertStmt.setString(3, lastName);
            insertStmt.setString(4, username);
            insertStmt.setString(5, password);
            insertStmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Method to generate a unique user ID
    private String generateUserId(String firstName, String lastName) {
        String firstPart = firstName.substring(0, 3).toUpperCase();
        String secondPart = lastName.substring(0, 3).toUpperCase();
        int randomNum = (int) (Math.random() * 9000) + 1000; // Generate a random 4-digit number
        return firstPart + secondPart + randomNum;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
