package sunDevil_Books;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ForgotPasswordView extends Application {

    private TextField userIdField;
    private PasswordField newPasswordField;
    private Label resetStatusLabel;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Forgot Password");

        userIdField = new TextField();
        userIdField.setPromptText("Enter your User ID");

        newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Enter new password");

        Button resetPasswordButton = Utils.createStyledButton("Reset Password");
        Button backButton = Utils.createStyledButton("Back");

        resetStatusLabel = new Label();

        resetPasswordButton.setOnAction(e -> handlePasswordReset());
        backButton.setOnAction(e -> {
            LoginView loginView = new LoginView();
            loginView.start(primaryStage); // Go back to login screen
        });

        VBox layout = new VBox(10, userIdField, newPasswordField, resetPasswordButton, backButton, resetStatusLabel);
        layout.setStyle("-fx-background-color: white; -fx-border-color: #FFC627;-fx-border-width: 3px; -fx-border-style: solid; ");
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 400, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handlePasswordReset() {
        String userId = userIdField.getText();
        String newPassword = newPasswordField.getText();

        if (userId.isEmpty() || newPassword.isEmpty()) {
            resetStatusLabel.setText("Please enter your User ID and new password.");
            return;
        }

        // Call the method to reset the password
        if (resetPassword(userId, newPassword)) {
            resetStatusLabel.setText("Password reset successful! You can now log in.");
        } else {
            resetStatusLabel.setText("User ID not found.");
        }
    }

    private boolean resetPassword(String userId, String newPassword) {
        String query = "UPDATE users SET password = ? WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newPassword);
            stmt.setString(2, userId);
            int rowsUpdated = stmt.executeUpdate();

            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
