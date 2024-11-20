package sunDevil_Books;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class SellerView extends Application {
		private String bookName;
		private String originalPrice;
		private String authorName;
		private String publishedYear;
		private String bookCategory;
		private String bookCondition;
		private String sellingPrice;
		private static String userName;
		private boolean isSellingPrice;
				
		public void start(Stage primaryStage)
		{
			
			Label title1Label = new Label("Book Information");
			Label title2Label = new Label("Selling Information");
			Label bookNameLabel = new Label("Book Name: ");
			Label originialPriceLabel = new Label("Originial Price: ");
			Label authorNameLabel = new Label("Author: ");
			Label publishedYearLabel = new Label("Published Year: ");
			Label bookCategoryLabel= new Label("Book Category: ");
			Label bookConditionLabel= new Label("Book Condition: ");
			Label sellingPriceLabel= new Label("Selling Price: ");
			
			
			
			TextField bookNameField = new TextField(); 
			TextField originalPriceField = new TextField(); 
			TextField authorNameField = new TextField(); 
			TextField publishedYearField = new TextField(); 
			TextField sellingPriceField = new TextField(); 
			
			Button listBookButton = new Button("List My Book");
			Button calculateSellingPriceButton = new Button("Calculate Selling Price");
			ComboBox<String> bookCategoryCombo = new ComboBox<>();
			ComboBox<String> bookConditionCombo = new ComboBox<>();
			
			bookCategoryCombo.getItems().addAll("Natural Science", "Computer", "Math", "English Language", "Other");
			bookConditionCombo.getItems().addAll("Like New", "Used", "Moderately Used", "Heavily Used");
			GridPane gridPane = new GridPane();
			isSellingPrice = false;
			bookCategoryCombo.setOnAction(event -> {
	            bookCategory = bookCategoryCombo.getValue();
	            System.out.println("Selected: " + bookCategory);
			});
			bookConditionCombo.setOnAction(event -> {
	            bookCondition = bookConditionCombo.getValue();
	            System.out.println("Selected: " + bookCondition);
			});
			calculateSellingPriceButton.setOnAction(e -> {
				
				
				isSellingPrice = true;
				bookName = bookNameField.getText();
				originalPrice = originalPriceField.getText();
				authorName = authorNameField.getText();
				publishedYear = publishedYearField.getText();
				sellingPriceField.setText("testing");
				
			});
			listBookButton.setOnAction(e -> {
				if(isSellingPrice == true)
				{
					bookName = bookNameField.getText();
					originalPrice = originalPriceField.getText();
					authorName = authorNameField.getText();
					publishedYear = publishedYearField.getText();
					
					//Add book to database
					
					
				}
				else
				{
					Alert alert = new Alert(AlertType.ERROR);
			        alert.setTitle("Error");
			        alert.setHeaderText("Something went wrong!");
			        alert.setContentText("Please calculate selling Price.");
			        alert.showAndWait();
				}
				
				
			});
			gridPane.add(title1Label, 1, 0);
			gridPane.add(title2Label, 1, 5);
			gridPane.add(bookNameLabel, 0, 1);
			gridPane.add(originialPriceLabel, 0, 2);
			gridPane.add(authorNameLabel, 0, 3);
			gridPane.add(publishedYearLabel, 0, 4);
			gridPane.add(bookCategoryLabel, 0, 6);
			gridPane.add(bookConditionLabel, 0, 7);
			gridPane.add(sellingPriceLabel, 3, 3);
			
			
			gridPane.add(bookNameField, 1, 1);
			gridPane.add(originalPriceField, 1, 2);
			gridPane.add(authorNameField, 1, 3);
			gridPane.add(publishedYearField, 1, 4);
			gridPane.add(sellingPriceField, 3, 4);
			gridPane.add(bookCategoryCombo, 1, 	6);
			gridPane.add(bookConditionCombo, 1, 7);
			
			gridPane.add(listBookButton, 3, 2);
			
			gridPane.add(calculateSellingPriceButton, 3, 5);
			gridPane.setPadding(new Insets(10, 10, 10, 10));
			gridPane.setHgap(10); 
	        gridPane.setVgap(20);
	        
			Scene scene = new Scene(gridPane, 400, 400);
	        primaryStage.setScene(scene);
	        primaryStage.show();
	        System.out.println("\nsellerview");
		}
		
		public static void setUserName(String userName2) {
	        userName = userName2;
	    }
		public static void main(String[] args) 
		{
			launch(args);
		}
}
