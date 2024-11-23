package sunDevil_Books;

import java.sql.*;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

public class Utils {
    public static void showAlert(Alert.AlertType type, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type, message, ButtonType.OK);
            alert.showAndWait();
        });
    }
    
    public static Button createStyledButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: #800020; "
                + "-fx-text-fill: #FFC627; "
                + "-fx-cursor: hand; " 
                + "-fx-transition: all 0.1s ease-in-out;"); 
        
        btn.setOnMouseEntered(e -> 
            btn.setStyle("-fx-background-color: #A00030; " 
                + "-fx-text-fill: #FFC627; "
                + "-fx-cursor: hand; "
                + "-fx-scale-x: 1.05; " 
                + "-fx-scale-y: 1.05; "
                + "-fx-transition: all 0.1s ease-in-out;")
        );
        
        btn.setOnMouseExited(e -> 
            btn.setStyle("-fx-background-color: #800020; "
                + "-fx-text-fill: #FFC627; "
                + "-fx-cursor: hand; "
                + "-fx-scale-x: 1.0; "
                + "-fx-scale-y: 1.0; "
                + "-fx-transition: all 0.1s ease-in-out;")
        );
        
        return btn;
    }
    
    public static Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: gold;");
        
        return label;
    }
}