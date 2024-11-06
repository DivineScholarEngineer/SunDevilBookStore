package sunDevil_Books;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SplashScreenView extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("SunDevil Books");

        // Application name with styling
        Label appName = new Label("SunDevil Books");
        appName.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;");

        // Login and Create Account buttons
        Button loginButton = new Button("Login");
        Button createAccountButton = new Button("Create Account");

        // Button styling and positioning
        VBox buttonBox = new VBox(20, loginButton, createAccountButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20));

        // Add event handlers for the buttons
        loginButton.setOnAction(e -> {
            LoginView loginView = new LoginView();
            try {
                loginView.start(primaryStage); // Switch to login view
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        createAccountButton.setOnAction(e -> {
            CreateAccountView createAccountView = new CreateAccountView();
            try {
                createAccountView.start(primaryStage); // Switch to create account view
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Splash screen layout with app name and buttons
        VBox vbox = new VBox(100, appName, buttonBox);
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
