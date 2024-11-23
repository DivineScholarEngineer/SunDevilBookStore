package sunDevil_Books;

import java.util.Random;
import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.sql.*;

public class SellerView extends Application {
    
    private String userId;
    private String username;

    private Label statusLabel; // Declaration remains

    // Constructor to initialize userId and username
    public SellerView(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    @Override
    public void start(Stage primaryStage) { // Overridden method
        primaryStage.setTitle(username + "'s Seller Dashboard");

        // Initialize statusLabel first to avoid NullPointerException
        statusLabel = Utils.createStyledLabel("");

        // User Information Section
        Label userInfoLabel = Utils.createStyledLabel("User: " + username);
        Label userRoleLabel = Utils.createStyledLabel("Role: Seller");
        Button logoutButton = Utils.createStyledButton("Logout");
        logoutButton.setOnAction(e -> {
            Utils.showAlert(Alert.AlertType.INFORMATION, "Logged out successfully!");
            SplashScreenView splashScreenView = new SplashScreenView();
            splashScreenView.start(primaryStage); // Redirect to splash screen
        });
        
        // Top Bar with Hamburger Menu
//        Button hamburgerMenu = Utils.createStyledButton("☰");
        Button hamburgerMenu = Utils.createStyledButton("Setting");
        HBox topBar = new HBox(10, hamburgerMenu);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10));

        // Hamburger menu event - Open settings in a new window
        hamburgerMenu.setOnAction(e -> openSettingsWindow(primaryStage));

//        VBox userInfoSection = new VBox(10, userInfoLabel, userRoleLabel, logoutButton);
        VBox userInfoSection = new VBox(10, userInfoLabel, userRoleLabel, hamburgerMenu, logoutButton);
        userInfoSection.setAlignment(Pos.TOP_RIGHT);
        userInfoSection.setPadding(new Insets(10));

        // Book Information Section
        Label bookInfoLabel = Utils.createStyledLabel("Book Information");
        Label bookNameLabel = new Label("Book Name:");
        TextField bookNameField = new TextField();
        Label originalPriceLabel = new Label("Original Price:");
        TextField originalPriceField = new TextField();
        Label authorLabel = new Label("Author:");
        TextField authorField = new TextField();
        Label publishedYearLabel = new Label("Published Year:");
        TextField publishedYearField = new TextField();

        VBox bookInfoSection = new VBox(10, bookInfoLabel,
                new HBox(10, bookNameLabel, bookNameField),
                new HBox(10, originalPriceLabel, originalPriceField),
                new HBox(10, authorLabel, authorField),
                new HBox(10, publishedYearLabel, publishedYearField));
        bookInfoSection.setPadding(new Insets(10));
        bookInfoSection.setStyle("-fx-border-color: #000000; -fx-border-width: 1; -fx-border-radius: 5;");

        // Selling Information Section
        Label sellingInfoLabel = Utils.createStyledLabel("Selling Information");
        Label bookCategoryLabel = new Label("Book Category:");
        ComboBox<String> bookCategoryComboBox = new ComboBox<>();
        bookCategoryComboBox.getItems().addAll("Fiction", "Non-Fiction", "Textbook", "Other");

        Label bookConditionLabel = new Label("Book Condition:");
        ComboBox<String> bookConditionComboBox = new ComboBox<>();
        bookConditionComboBox.getItems().addAll("New", "Used", "Heavily Used");

        VBox sellingInfoSection = new VBox(10, sellingInfoLabel,
                new HBox(10, bookCategoryLabel, bookCategoryComboBox),
                new HBox(10, bookConditionLabel, bookConditionComboBox));
        sellingInfoSection.setPadding(new Insets(10));
        sellingInfoSection.setStyle("-fx-border-color: #000000; -fx-border-width: 1; -fx-border-radius: 5;");

        // Buttons Section
        Button calculateSellingPriceButton = Utils.createStyledButton("Calculate Selling Price");
        TextField sellingPriceField = new TextField();
        sellingPriceField.setPromptText("Selling Price");
        sellingPriceField.setEditable(false); // Make selling price field non-editable

        Button listMyBookButton = Utils.createStyledButton("List My Book");
        listMyBookButton.setOnAction(e -> addBook(bookNameField, originalPriceField, authorField, publishedYearField, bookCategoryComboBox, bookConditionComboBox, sellingPriceField));

        VBox buttonSection = new VBox(10, calculateSellingPriceButton, sellingPriceField, listMyBookButton);
        buttonSection.setAlignment(Pos.CENTER);

        // Event Handlers
        calculateSellingPriceButton.setOnAction(e -> {
            String condition = bookConditionComboBox.getValue();
            String originalPriceStr = originalPriceField.getText().trim();

            if (condition == null || condition.isEmpty()) {
                statusLabel.setText("Please select the book condition.");
                return;
            }

            if (originalPriceStr.isEmpty()) {
                statusLabel.setText("Please enter the original price.");
                return;
            }

            if (!isCurrencyFormat(originalPriceStr)) {
                statusLabel.setText("Please enter a valid currency format (e.g., 99.99).");
                return;
            }

            double originalPrice = Double.parseDouble(originalPriceStr);
            String sellingPrice = calculateSellingPrice(condition, originalPrice);
            sellingPriceField.setText("$" + sellingPrice);
            statusLabel.setText("Selling price calculated successfully.");
        });

        // Main Layout Setup
        VBox bookAndSellingInfo = new VBox(20, bookInfoSection, sellingInfoSection);
        bookAndSellingInfo.setAlignment(Pos.TOP_LEFT);

        HBox mainContent = new HBox(30, bookAndSellingInfo, buttonSection);
        mainContent.setAlignment(Pos.CENTER);



        // Combine Top Bar and User Info Section
        VBox combinedTop = new VBox(topBar, userInfoSection);

        // BorderPane Layout to organize top and center sections
        BorderPane root = new BorderPane();
        root.setTop(combinedTop);
        root.setCenter(mainContent);
        root.setBottom(statusLabel); // Now, statusLabel is properly initialized
        BorderPane.setAlignment(statusLabel, Pos.CENTER);
        BorderPane.setMargin(statusLabel, new Insets(10));

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Adds a book to the database.
     *
     * @param name              The name of the book.
     * @param originalPrice     The original price of the book.
     * @param author            The author of the book.
     * @param publishedYear     The year the book was published.
     * @param categoryBox       The category of the book.
     * @param conditionBox      The condition of the book.
     * @param sellingPriceField The field displaying the selling price.
     */
    
    
//    private void addBook(TextField nameField, TextField priceField, TextField authorField, TextField publishedYearField,
//                        ComboBox<String> categoryBox, ComboBox<String> conditionBox, TextField sellingPriceField) {
//        String name = nameField.getText().trim();
//        String author = authorField.getText().trim();
//        String category = categoryBox.getValue();
//        String condition = conditionBox.getValue();
//        String sellingPriceStr = sellingPriceField.getText().trim();
//
//        if (name.isEmpty() || author.isEmpty() || category == null || condition == null || sellingPriceStr.isEmpty()) {
//            statusLabel.setText("Please fill in all fields and calculate selling price.");
//            return;
//        }
//
//        double sellingPrice;
//        try {
//            sellingPrice = Double.parseDouble(sellingPriceStr.replace("$", ""));
//        } catch (NumberFormatException ex) {
//            statusLabel.setText("Invalid selling price format.");
//            return;
//        }
//
//        String publishedYearStr = publishedYearField.getText().trim();
//        int publishedYear;
//        try {
//            publishedYear = Integer.parseInt(publishedYearStr);
//        } catch (NumberFormatException ex) {
//            statusLabel.setText("Invalid published year format.");
//            return;
//        }
//
//        String query = "INSERT INTO books (category, name, author, publishing_year, price, book_condition, seller_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
//        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
//             PreparedStatement stmt = conn.prepareStatement(query)) {
//
//            stmt.setString(1, category);
//            stmt.setString(2, name);
//            stmt.setString(3, author);
//            stmt.setInt(4, publishedYear);
//            stmt.setDouble(5, sellingPrice);
//            stmt.setString(6, condition);
//            stmt.setString(7, userId);
//            stmt.executeUpdate();
//
//            statusLabel.setText("Book added successfully!");
//            clearFields(nameField, priceField, authorField, publishedYearField, categoryBox, conditionBox, sellingPriceField);
//        } catch (SQLException e) {
//            statusLabel.setText("Error adding book: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }

    /**
     * Clears all input fields after successful book addition.
     *
     * @param nameField          The book name field.
     * @param originalPriceField The original price field.
     * @param authorField        The author field.
     * @param publishedYearField The published year field.
     * @param categoryComboBox   The book category combo box.
     * @param conditionComboBox  The book condition combo box.
     * @param sellingPriceField  The selling price field.
     */
    private void addBook(TextField nameField, TextField priceField, TextField authorField, TextField publishedYearField,
            ComboBox<String> categoryBox, ComboBox<String> conditionBox, TextField sellingPriceField) {
String name = nameField.getText().trim();
String author = authorField.getText().trim();
String category = categoryBox.getValue();
String condition = conditionBox.getValue();
String priceStr = priceField.getText().trim();

if (name.isEmpty() || author.isEmpty() || category == null || condition == null || priceStr.isEmpty()) {
statusLabel.setText("Please fill in all fields.");
return;
}

double price;
try {
price = Double.parseDouble(priceStr);
} catch (NumberFormatException ex) {
statusLabel.setText("Invalid price format.");
return;
}

String publishedYearStr = publishedYearField.getText().trim();
int publishedYear;
try {
publishedYear = Integer.parseInt(publishedYearStr);
} catch (NumberFormatException ex) {
statusLabel.setText("Invalid published year format.");
return;
}

String query = "INSERT INTO books (category, name, author, publishing_year, price, book_condition, seller_id) " +
          "VALUES (?, ?, ?, ?, ?, ?, ?)";
try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
PreparedStatement stmt = conn.prepareStatement(query)) {

stmt.setString(1, category);
stmt.setString(2, name);
stmt.setString(3, author);
stmt.setInt(4, publishedYear);
stmt.setDouble(5, price);
stmt.setString(6, condition);
stmt.setString(7, userId); // Assuming `userId` is the seller's ID
stmt.executeUpdate();

statusLabel.setText("Book added successfully!");
clearFields(nameField, priceField, authorField, publishedYearField, categoryBox, conditionBox, sellingPriceField);
} catch (SQLException e) {
statusLabel.setText("Error adding book: " + e.getMessage());
e.printStackTrace();
}
}

    
    private void clearFields(TextField nameField, TextField originalPriceField, TextField authorField,
                            TextField publishedYearField, ComboBox<String> categoryComboBox,
                            ComboBox<String> conditionComboBox, TextField sellingPriceField) {
        nameField.clear();
        originalPriceField.clear();
        authorField.clear();
        publishedYearField.clear();
        categoryComboBox.setValue(null);
        conditionComboBox.setValue(null);
        sellingPriceField.clear();
    }

    /**
     * Opens the settings window.
     *
     * @param currentStage The current primary stage.
     */
    private void openSettingsWindow(Stage currentStage) {
        Stage settingsStage = new Stage();
        settingsStage.setTitle("Settings");

        VBox settingsLayout = new VBox(10);
        settingsLayout.setPadding(new Insets(20));
        settingsLayout.setAlignment(Pos.CENTER);

        Button switchToBuyerButton = Utils.createStyledButton("Switch to Buyer View");
        Button displayUserInfoButton = Utils.createStyledButton("Display User Info");
        Button changePasswordButton = Utils.createStyledButton("Change Password");

        // Switch to Buyer View Action
        switchToBuyerButton.setOnAction(e -> {
            switchToBuyer(currentStage);
            settingsStage.close();
        });

        // Display User Info Action
        displayUserInfoButton.setOnAction(e -> displayUserInfo());

        // Change Password Action
        changePasswordButton.setOnAction(e -> changePassword());

        settingsLayout.getChildren().addAll(switchToBuyerButton, displayUserInfoButton, changePasswordButton);

        Scene settingsScene = new Scene(settingsLayout, 300, 200);
        settingsStage.setScene(settingsScene);
        settingsStage.initOwner(currentStage); // Set owner to currentStage
        settingsStage.show();
    }

    /**
     * Switches to the Buyer View.
     *
     * @param currentStage The current primary stage.
     */
//    private void switchToBuyer(Stage currentStage) {
//        // Close the current window
//        currentStage.close();
//
//        // Open BuyerView
//        BuyerView buyerView = new BuyerView(userId, username);
//        buyerView.start(new Stage());
//    }
    
    private void switchToBuyer(Stage currentStage) {
        // Update the user's role to 'Buyer' in the database
        String updateRoleQuery = "UPDATE users SET role = 'Buyer' WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(updateRoleQuery)) {
            stmt.setString(1, userId); // Use the logged-in user's ID
            stmt.executeUpdate();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error switching role: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // Load the BuyerView
        currentStage.close();
        BuyerView buyerView = new BuyerView(userId, username);
        buyerView.start(new Stage());
    }


    private void showAlert(AlertType error, String string, String string2) {
		// TODO Auto-generated method stub
		
	}

	/**
     * Displays user information in an alert.
     */
    private void displayUserInfo() {
        // Fetch user information from the database
        String query = "SELECT first_name, last_name, role FROM users WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String role = rs.getString("role");

                String userInfo = "First Name: " + firstName + "\n" +
                        "Last Name: " + lastName + "\n" +
                        "User ID: " + userId + "\n" +
                        "Role: " + role;

                Utils.showAlert(Alert.AlertType.INFORMATION, "User Information");
            }
        } catch (SQLException e) {
            Utils.showAlert(Alert.AlertType.ERROR, "Error fetching user information: " + e.getMessage());
        }
    }

    /**
     * Changes the user's password.
     */
    private void changePassword() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Enter a new password:");
        dialog.setContentText("New Password:");

        dialog.showAndWait().ifPresent(newPassword -> {
            if (newPassword.trim().isEmpty()) {
                Utils.showAlert(Alert.AlertType.ERROR, "Password cannot be empty.");
                return;
            }

            String query = "UPDATE users SET password = ? WHERE user_id = ?";
            try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, newPassword);
                stmt.setString(2, userId);
                stmt.executeUpdate();
                Utils.showAlert(Alert.AlertType.INFORMATION, "Password updated successfully.");
            } catch (SQLException e) {
                Utils.showAlert(Alert.AlertType.ERROR, "Error updating password: " + e.getMessage());
            }
        });
    }

    /**
     * Calculates the selling price based on the book's condition.
     *
     * @param condition    The condition of the book.
     * @param originalPrice The original price of the book.
     * @return The calculated selling price as a String formatted to two decimal places.
     */
    private String calculateSellingPrice(String condition, double originalPrice) {
        double factor;

        switch (condition) {
            case "New":
                factor = 0.90; // 90% of original price
                break;
            case "Used":
                factor = 0.75; // 75% of original price
                break;
            case "Heavily Used":
                factor = 0.60; // 60% of original price
                break;
            default:
                factor = 1.0; // No discount if condition is unrecognized
                break;
        }

        double sellingPrice = originalPrice * factor;
        return String.format("%.2f", sellingPrice);
    }

    /**
     * Validates if the input string is in a valid currency format.
     *
     * @param str The input string.
     * @return True if valid, false otherwise.
     */
    public static boolean isCurrencyFormat(String str) {
        // General pattern: optional currency symbol, optional commas, and two decimal places
        String currencyPattern = "^[\\$€₹¥]?[1-9]\\d{0,2}(,\\d{3})*(\\.\\d{2})?$";
        return Pattern.matches(currencyPattern, str);
    }

    /**
     * Validates if the input string is a valid year format (e.g., 2023).
     *
     * @param str The input string.
     * @return True if valid, false otherwise.
     */
    public static boolean isYearFormat(String str) {
        // Regex pattern for integers 1000-2024
        String rangePattern = "^(202[0-4]|20[0-1]\\d|1\\d{3})$";
        return Pattern.matches(rangePattern, str);
    }

    /**
     * Main method to launch the application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
