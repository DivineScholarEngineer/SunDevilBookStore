package sunDevil_Books;


import java.util.Random;
import java.util.regex.Pattern;



import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.control.Alert.AlertType;

import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


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
		
		public SellerView(String userName) {
			this.userName = userName;
		}
		
		
		public void start(Stage primaryStage)
		{
			primaryStage.setTitle("Seller View");
			BorderPane mainLayout = new BorderPane();
			GridPane gridPane = new GridPane();
			GridPane box1 = new GridPane();
			GridPane box3 = new GridPane();
			GridPane userBox = new GridPane();
			ImageView logoView = new ImageView();
	        /*Image logo = new Image(getClass().getResourceAsStream("sundevilbooks.png"));
	        logoView.setImage(logo);
	        logoView.setFitWidth(200);  
	        logoView.setFitHeight(100); 
	        logoView.setPreserveRatio(true);*/ 
	        
	        mainLayout.setStyle("-fx-background-color: white; -fx-border-color: #FFC627; -fx-border-width: 3px; -fx-border-style: solid;");
	        mainLayout.setPadding(new Insets(50,20,50,150));
	        
	        
			Label title1Label = Utils.createStyledLabel("Book Information");
			Label title2Label = Utils.createStyledLabel("Selling Information");
			Label bookNameLabel = new Label("Book Name: ");
			Label originialPriceLabel = new Label("Originial Price: ");
			Label authorNameLabel = new Label("Author: ");
			Label publishedYearLabel = new Label("Published Year: ");
			Label bookCategoryLabel= new Label("Book Category: ");
			Label bookConditionLabel= new Label("Book Condition: ");
			Label sellingPriceLabel= new Label("Selling Price: ");
			
			Label userLabel = new Label("User : " + userName);
			Label roleLabel = new Label("Role: Seller");
			
			TextField bookNameField = new TextField(); 
			TextField originalPriceField = new TextField(); 
			TextField authorNameField = new TextField(); 
			TextField publishedYearField = new TextField(); 
			TextField sellingPriceField = new TextField(); 
			
			Button listBookButton = Utils.createStyledButton("List My Book");
			Button calculateSellingPriceButton = Utils.createStyledButton("Calculate Selling Price");
			Button logoutButton = Utils.createStyledButton("Log Out");
			ComboBox<String> bookCategoryCombo = new ComboBox<>();
			ComboBox<String> bookConditionCombo = new ComboBox<>();
			
			
			bookCategoryCombo.getItems().addAll("Natural Science", "Computer", "Math", "English Language", "Other");
			bookConditionCombo.getItems().addAll("Like New", "Used", "Moderately Used", "Heavily Used");
			
			
			
			
			
			
			isSellingPrice = false;
			bookCategoryCombo.setOnAction(event -> {
				
	            bookCategory = bookCategoryCombo.getValue();
	            //System.out.println("Selected: " + bookCategory);
			});
			bookConditionCombo.setOnAction(event -> {
	            bookCondition = bookConditionCombo.getValue();
	            //System.out.println("Selected: " + bookCondition);
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
			logoutButton.setOnAction(e -> {
				Alert alert = new Alert(Alert.AlertType.INFORMATION, "Logged out successfully!", ButtonType.OK);
	            alert.showAndWait();

	            SplashScreenView splashScreenView = new SplashScreenView();
	            splashScreenView.start(primaryStage); // Go back to splash screen
				
			});
			box1.add(title1Label, 1, 0);
			box1.add(bookNameLabel, 0, 1);
			box1.add(originialPriceLabel, 0, 2);
			box1.add(authorNameLabel, 0, 3);
			box1.add(publishedYearLabel, 0, 4);
			box1.add(bookNameField, 1, 1);
			box1.add(originalPriceField, 1, 2);
			box1.add(authorNameField, 1, 3);
			box1.add(publishedYearField, 1, 4);
			
			gridPane.add(title2Label, 1, 0);
			gridPane.add(bookCategoryLabel, 0, 1);
			gridPane.add(bookConditionLabel, 0, 2);
			gridPane.add(bookCategoryCombo, 1, 	1);
			gridPane.add(bookConditionCombo, 1, 2);
			
			box3.add(sellingPriceLabel, 0, 1);
			box3.add(sellingPriceField, 0, 2);
			box3.add(listBookButton, 0, 0);
			
			userBox.add(userLabel, 0, 0);
			userBox.add(roleLabel, 0, 1);
			userBox.add(logoutButton, 0, 2);
			
			box3.add(calculateSellingPriceButton, 0, 3);
			gridPane.setPadding(new Insets(10, 10, 10, 10));
			gridPane.setHgap(10); 
	        gridPane.setVgap(20);
	        gridPane.setPrefSize(350,200);
	        
	        gridPane.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
	        box1.setPrefSize(350, 200);
	        box1.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
	        box1.setPadding(new Insets(10, 10, 10, 10));
	        box1.setHgap(10); 
	        box1.setVgap(20);
			
	        userBox.setPadding(new Insets(10, 10, 100, 10));
	        userBox.setHgap(10); 
	        userBox.setVgap(10);
	        
	        VBox leftColumn = new VBox(20);
	        box3.setPadding(new Insets(100, 10, 10, 100));
	        box3.setHgap(10); 
	        box3.setVgap(20);
	        leftColumn.getChildren().addAll(box1, gridPane);
	        
	        
			mainLayout.setLeft(leftColumn);
			mainLayout.setCenter(box3);
			mainLayout.setRight(userBox);
			//mainLayout.setTop(userBox);
	        
	        
	        
	        
	        
	        
	        
	        
	        
	        
	        
			Scene scene = new Scene(mainLayout, 900, 500);
	        primaryStage.setScene(scene);
	        primaryStage.show();
	        
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
