package sunDevil_Books;


import java.util.Random;
import java.util.regex.Pattern;

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
import java.sql.*;

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
				bookName = bookNameField.getText();
				authorName = authorNameField.getText();
				publishedYear = publishedYearField.getText();
				
				
				
				boolean isYear = isYearFormat(publishedYear);
				originalPrice = originalPriceField.getText();
				boolean inCurrency = isCurrencyFormat(originalPrice);
				if(inCurrency == true)
				{
					sellingPrice = calculateSellingPrice(bookCondition, originalPrice);
					isSellingPrice = true;
					if(bookName.isEmpty() || authorName.isEmpty() || isYear == false || bookCondition == null || bookCategory == null)
					{
						
						Alert alert = new Alert(AlertType.ERROR);
				        alert.setTitle("Error");
				        alert.setHeaderText("Something went wrong!");
				        alert.setContentText("Please fill out all details");
				        alert.showAndWait();
						//Add book to database
						
					}
					else
					{
						
						sellingPriceField.setText(sellingPrice);
					}
				}
				else
				{
					Alert alert = new Alert(AlertType.ERROR);
			        alert.setTitle("Error");
			        alert.setHeaderText("Something went wrong!");
			        alert.setContentText("Please enter a valid Currency.");
			        alert.showAndWait();
				}
				
				
				
				
			});
			listBookButton.setOnAction(e -> {
				
				
				
				if(isSellingPrice == true )
				{
					createUser(bookCategory, bookName, authorName, publishedYear, sellingPrice, bookCondition);
					
					 Alert alert = new Alert(AlertType.INFORMATION);
			         alert.setTitle("Information");
			         alert.setHeaderText("Book Sale");
			         alert.setContentText("Book Sale Posted");
			         alert.showAndWait();
			         bookNameField.clear();
					 originalPriceField.clear();  
					 authorNameField.clear(); 
					 publishedYearField.clear();  
					 sellingPriceField.clear(); 
					 bookCategoryCombo.setValue(null);
					 bookConditionCombo.setValue(null);
			         
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
		
		
		public static boolean isCurrencyFormat(String str) {
		   // General pattern: optional currency symbol, optional commas, and two decimal places
		     String currencyPattern = "^[\\$€₹¥]?[1-9]\\d{0,2}(,\\d{3})*(\\.\\d{2})?$";
		     return Pattern.matches(currencyPattern, str);
		 }
		
		 public static boolean isYearFormat(String str) {
		        // Regex pattern for integers 0-2024
		        String rangePattern = "^(202[0-4]|20[0-1]\\d|1\\d{3}|[1-9]\\d{0,2}|0)$";
		        return Pattern.matches(rangePattern, str);
		 }
		 
		 private boolean createUser(String category, String name, String author, String year, String price, String condition) {
			 
		        String insertQuery = "INSERT INTO books (book_id, category, name, author, publishing_year, price, book_condition) VALUES (?, ?, ?, ?, ?, ?, ?)"; // Default role: Buyer
		        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
		            
		             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {

		            // Check if username already exists
		       
		            

		            // Generate a unique user ID
		            String book_id = generateID();

		            // Insert new user into the database
		            insertStmt.setString(1, book_id);
		            insertStmt.setString(2, category);
		            insertStmt.setString(3, name);
		            insertStmt.setString(4, author);
		            insertStmt.setString(5, year);
		            insertStmt.setString(6, price);
		            insertStmt.setString(7, condition);
		            insertStmt.executeUpdate();

		            return true;
		        } catch (SQLException e) {
		            e.printStackTrace();
		        }
		        return false;
		    }
		 
		 private String generateID()
		{
				Random random = new Random();
				
		        int randINT = 10000 + random.nextInt(90000); 
		        
		        String randString = Integer.toString(randINT);
				return randString;
		}
		 
		 private String calculateSellingPrice(String condition, String price)
		 {
			 
			 if( price.startsWith("$")) 
			 {
				 price = price.substring(1);
			 }
			 
			 double doublePrice = Double.parseDouble(price);
			 
			 switch (condition) {
             case "Like New":
                
                 doublePrice = doublePrice * .85 ;
                 break;
             case "Used":
            	 doublePrice = doublePrice * .75 ;
             	break;
             case "Moderately Used":
            	 doublePrice = doublePrice * .65 ;
            	 break;
             case "Heavily Used":
            	 doublePrice = doublePrice * .55 ;
            	 break;
             default:
                 // Show full name for non-admin users
                 break;
			 }
			 
			 String newPrice = String.format("%.2f", doublePrice);
			 return newPrice;
		 }
		    
		public static void main(String[] args) 
		{
			launch(args);
		}
}
