package sunDevil_Books;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SplashScreenView extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("SunDevil Books");

        ImageView logoView = new ImageView();
        Image logo = new Image(getClass().getResourceAsStream("sundevilbooks.png"));
            
        logoView.setImage(logo);
            
        logoView.setFitWidth(400);  
        logoView.setFitHeight(200); 
        logoView.setPreserveRatio(true); 

        // Login and Create Account buttons
        Button loginButton = Utils.createStyledButton("Login");
        Button createAccountButton = Utils.createStyledButton("Create Account");

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
        VBox vbox = new VBox(10, logoView, buttonBox);
        vbox.setStyle("-fx-background-color: white; -fx-border-color: #FFC627; -fx-border-width: 3px; -fx-border-style: solid; ");
        
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
