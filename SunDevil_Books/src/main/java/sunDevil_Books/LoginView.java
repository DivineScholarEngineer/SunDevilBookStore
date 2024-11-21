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

        Button loginButton = Utils.createStyledButton("Login");
        Button createAccountButton = Utils.createStyledButton("Create Account");
        Button backButton = Utils.createStyledButton("Back");
        Button forgotPasswordButton = Utils.createStyledButton("Forgot Password");

        loginStatusLabel = new Label();

        loginButton.setOnAction(e -> handleLogin(primaryStage));

        createAccountButton.setOnAction(e -> {
            CreateAccountView createAccountView = new CreateAccountView();
            createAccountView.start(primaryStage); // Switch to Create Account View
        });

        backButton.setOnAction(e -> {
            SplashScreenView splashScreenView = new SplashScreenView();
            splashScreenView.start(primaryStage); // Switch back to splash screen
        });
        
        forgotPasswordButton.setOnAction(e -> {
            ForgotPasswordView forgotPasswordView = new ForgotPasswordView();
            forgotPasswordView.start(primaryStage); // Switch to Forgot Password view
        });


        // Layout for the login screen
        VBox layout = new VBox(10, usernameField, passwordField, loginButton, createAccountButton, forgotPasswordButton, backButton, loginStatusLabel);
        layout.setStyle("-fx-background-color: white; -fx-border-color: #FFC627; -fx-border-width: 3px; -fx-border-style: solid;");
        
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 400, 250);
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

        // Call the function to check login
        if (DatabaseOperations.authenticateUser(username, password)) {
            loginStatusLabel.setText("Login successful!");

            // Call the function to check login
            if (DatabaseOperations.authenticateUser(username, password)) {
                loginStatusLabel.setText("Login successful!");

                // Fetch user info
                String role = DatabaseOperations.getUserRole(username);
                String firstName = DatabaseOperations.getFirstName(username);
                String lastName = DatabaseOperations.getLastName(username);
                String userId = DatabaseOperations.getUserId(username);

                if (role != null) {
                    switch (role) {
                        case "Admin":
                            AdminView adminView = new AdminView(firstName, lastName, username, userId);
                            adminView.start(stage); 
                            break;
                            
                        case "Buyer":
                        	BuyerView buyerView = new BuyerView(firstName, lastName, username, userId);
                        	buyerView.start(stage);
                        	break;
                        	
                        case "Seller":
                        	SellerView sellerView = new SellerView();
                        	sellerView.start(stage);
                        	break;
                        	
                        default:
                            break;
                    }
                }
            } else {
                loginStatusLabel.setText("Invalid user ID or password.");
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
