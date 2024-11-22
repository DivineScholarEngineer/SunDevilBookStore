package sunDevil_Books;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;

public class SellerView { // **Removed 'extends Application'**

    private String userId;
    private String username;

    private Label statusLabel; // **Declaration remains**

    public SellerView(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public void start(Stage primaryStage) { // **Regular method, not overridden**
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

        VBox userInfoSection = new VBox(10, userInfoLabel, userRoleLabel, logoutButton);
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
        bookConditionComboBox.getItems().addAll("New", "Good", "Used");

        VBox sellingInfoSection = new VBox(10, sellingInfoLabel,
                new HBox(10, bookCategoryLabel, bookCategoryComboBox),
                new HBox(10, bookConditionLabel, bookConditionComboBox));
        sellingInfoSection.setPadding(new Insets(10));
        sellingInfoSection.setStyle("-fx-border-color: #000000; -fx-border-width: 1; -fx-border-radius: 5;");

        // Buttons Section
        Button calculateSellingPriceButton = Utils.createStyledButton("Calculate Selling Price");
        TextField sellingPriceField = new TextField();
        sellingPriceField.setPromptText("Selling Price");

        Button listMyBookButton = Utils.createStyledButton("List My Book");
        listMyBookButton.setOnAction(e -> addBook(bookNameField, originalPriceField, authorField, publishedYearField, bookCategoryComboBox, bookConditionComboBox));

        VBox buttonSection = new VBox(10, calculateSellingPriceButton, sellingPriceField, listMyBookButton);
        buttonSection.setAlignment(Pos.CENTER);

        // Main Layout
//        VBox mainLayout = new VBox(20, bookInfoSection, sellingInfoSection, buttonSection);
//        mainLayout.setPadding(new Insets(10));
        
     // Wrapping Book Info and Seller Info in a VBox
        VBox centerLayout = new VBox(15, bookInfoSection, sellingInfoSection);
        centerLayout.setAlignment(Pos.CENTER); // Center-align Book and Seller sections
        centerLayout.setPadding(new Insets(10));

        // Using BorderPane for layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setCenter(centerLayout); // Place Book and Seller sections in the center
        mainLayout.setRight(buttonSection); // Place buttons on the right
        BorderPane.setMargin(buttonSection, new Insets(0, 10, 0, 10)); // Optional: Add padding for buttons


        // Top Bar with Hamburger Menu
        Button hamburgerMenu = Utils.createStyledButton("☰");
        HBox topBar = new HBox(10, hamburgerMenu);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10));

        // Hamburger menu event - Open settings in a new window
        hamburgerMenu.setOnAction(e -> openSettingsWindow(primaryStage));

        // Combine Top Bar and User Info Section
        VBox combinedTop = new VBox(topBar, userInfoSection);

        // BorderPane Layout to organize top and center sections
        BorderPane root = new BorderPane();
        root.setTop(combinedTop);
        root.setCenter(mainLayout);
        root.setBottom(statusLabel); // **Now, statusLabel is properly initialized**
        BorderPane.setAlignment(statusLabel, Pos.CENTER);
        BorderPane.setMargin(statusLabel, new Insets(10));

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addBook(TextField nameField, TextField priceField, TextField authorField, TextField publishedYearField,
                        ComboBox<String> categoryBox, ComboBox<String> conditionBox) {
        String name = nameField.getText().trim();
        String author = authorField.getText().trim();
        String category = categoryBox.getValue();
        String condition = conditionBox.getValue();
        double price;
        int publishedYear;

        if (name.isEmpty() || author.isEmpty() || category == null || condition == null) {
            statusLabel.setText("Please fill in all fields.");
            return;
        }

        try {
            price = Double.parseDouble(priceField.getText().trim());
            publishedYear = Integer.parseInt(publishedYearField.getText().trim());
        } catch (NumberFormatException ex) {
            statusLabel.setText("Invalid price or year format.");
            return;
        }

        String query = "INSERT INTO books (name, author, price, category, condition, published_year, seller_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, name);
            stmt.setString(2, author);
            stmt.setDouble(3, price);
            stmt.setString(4, category);
            stmt.setString(5, condition);
            stmt.setInt(6, publishedYear);
            stmt.setString(7, userId);
            stmt.executeUpdate();

            statusLabel.setText("Book added successfully!");
        } catch (SQLException e) {
            statusLabel.setText("Error adding book: " + e.getMessage());
        }
    }

    private void openSettingsWindow(Stage currentStage) {
        Stage settingsStage = new Stage();
        settingsStage.setTitle("Settings");

        VBox settingsLayout = new VBox(10);
        settingsLayout.setPadding(new Insets(20));

        Button switchToBuyerButton = Utils.createStyledButton("Switch to Buyer View");
        Button becomeBuyerButton = Utils.createStyledButton("Become Buyer");
        Button displayUserInfoButton = Utils.createStyledButton("Display User Info");
        Button changePasswordButton = Utils.createStyledButton("Change Password");

        // Event handlers
        switchToBuyerButton.setOnAction(e -> {
            if (hasBuyerRole()) {
                switchToBuyer(currentStage);
                settingsStage.close();
            } else {
                Utils.showAlert(Alert.AlertType.WARNING, "You do not have Buyer access.");
            }
        });

        becomeBuyerButton.setOnAction(e -> becomeBuyer());

        displayUserInfoButton.setOnAction(e -> displayUserInfo());

        changePasswordButton.setOnAction(e -> changePassword());

        settingsLayout.getChildren().addAll(switchToBuyerButton, becomeBuyerButton, displayUserInfoButton, changePasswordButton);

        Scene settingsScene = new Scene(settingsLayout, 300, 250);
        settingsStage.setScene(settingsScene);
        settingsStage.show();
    }

    private void becomeBuyer() {
        String role = getUserRole();
        if (role.contains("Buyer")) {
            statusLabel.setText("You are already a buyer.");
            return;
        }

        if (role.equals("Admin")) {
            statusLabel.setText("Admins already have all permissions.");
            return;
        }

        // Update the user's role to include 'Buyer'
        String newRole = role.equals("Seller") ? "BuyerSeller" : "Buyer";
        String query = "UPDATE users SET role = ? WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newRole);
            stmt.setString(2, userId);
            stmt.executeUpdate();
            statusLabel.setText("You are now a buyer!");

        } catch (SQLException e) {
            statusLabel.setText("Error becoming buyer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean hasBuyerRole() {
        String role = getUserRole();
        return role.contains("Buyer") || role.equals("Admin") || role.equals("BuyerSeller");
    }

    private void switchToBuyer(Stage currentStage) {
        // Close the current window
        currentStage.close();

        // Open BuyerView
        BuyerView buyerView = new BuyerView(userId, username);
        buyerView.start(new Stage());
    }

    private void displayUserInfo() {
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

                Utils.showAlert(Alert.AlertType.INFORMATION, userInfo);
            }
        } catch (SQLException e) {
            statusLabel.setText("Error fetching user information: " + e.getMessage());
        }
    }

    private void changePassword() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Enter a new password:");
        dialog.setContentText("New Password:");

        dialog.showAndWait().ifPresent(newPassword -> {
            if (newPassword.trim().isEmpty()) {
                statusLabel.setText("Password cannot be empty.");
                return;
            }

            String query = "UPDATE users SET password = ? WHERE user_id = ?";
            try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, newPassword);
                stmt.setString(2, userId);
                stmt.executeUpdate();
                statusLabel.setText("Password updated successfully.");
            } catch (SQLException e) {
                statusLabel.setText("Error updating password: " + e.getMessage());
            }
        });
    }

    private String getUserRole() {
        String query = "SELECT role FROM users WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("role");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Seller";
    }
}
