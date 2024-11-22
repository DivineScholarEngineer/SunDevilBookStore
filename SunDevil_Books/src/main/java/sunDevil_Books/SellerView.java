package sunDevil_Books;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;

public class SellerView extends Application {

    private String userId;
    private String username;

    private ListView<String> booksListView;
    private Label statusLabel;

    public SellerView(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle(username + "'s Seller Dashboard");

        // Welcome message
        Label welcomeLabel = new Label("Welcome, " + getFirstName() + "!");
        welcomeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Top bar with Hamburger menu and Logout button
        Button hamburgerMenu = new Button("☰");
        Button logoutButton = new Button("Logout");
        HBox topBar = new HBox(10, hamburgerMenu, logoutButton);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(10));

        // Logout button action
        logoutButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Logged out successfully!", ButtonType.OK);
            alert.showAndWait();

            // Redirect to the splash screen instead of exiting the application
            SplashScreenView splashScreenView = new SplashScreenView();
            splashScreenView.start(primaryStage); // Go back to splash screen
        });

        // Books list and management buttons
        booksListView = new ListView<>();
        statusLabel = new Label();

        fetchSellerBooks();

        Button addBookButton = new Button("Add Book");
        addBookButton.setOnAction(e -> addBook());

        Button deleteBookButton = new Button("Delete Selected Book");
        deleteBookButton.setOnAction(e -> deleteSelectedBook());

        // Hamburger menu event - Open settings in a new window
        hamburgerMenu.setOnAction(e -> openSettingsWindow(primaryStage));

        // Layout
        VBox layout = new VBox(10, welcomeLabel, topBar, booksListView, addBookButton, deleteBookButton, statusLabel);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 500, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void fetchSellerBooks() {
        String query = "SELECT book_id, name, author, price FROM books WHERE seller_id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            booksListView.getItems().clear();
            while (rs.next()) {
                int bookId = rs.getInt("book_id");
                String name = rs.getString("name");
                String author = rs.getString("author");
                double price = rs.getDouble("price");
                booksListView.getItems().add(bookId + " - " + name + " by " + author + " ($" + price + ")");
            }
        } catch (SQLException e) {
            statusLabel.setText("Error fetching books: " + e.getMessage());
        }
    }

    private void addBook() {
        // Open a new window to add book details
        Stage addBookStage = new Stage();
        addBookStage.setTitle("Add Book");

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        TextField authorField = new TextField();
        TextField priceField = new TextField();

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Author:"), 0, 1);
        grid.add(authorField, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(priceField, 1, 2);

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String author = authorField.getText().trim();
            double price;

            try {
                price = Double.parseDouble(priceField.getText().trim());
            } catch (NumberFormatException ex) {
                statusLabel.setText("Invalid price format.");
                return;
            }

            String query = "INSERT INTO books (name, author, price, seller_id) VALUES (?, ?, ?, ?)";
            try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, name);
                stmt.setString(2, author);
                stmt.setDouble(3, price);
                stmt.setString(4, userId);
                stmt.executeUpdate();

                statusLabel.setText("Book added successfully!");
                fetchSellerBooks();
                addBookStage.close();
            } catch (SQLException ex) {
                statusLabel.setText("Error adding book: " + ex.getMessage());
            }
        });

        grid.add(saveButton, 1, 3);

        Scene scene = new Scene(grid, 300, 250);
        addBookStage.setScene(scene);
        addBookStage.show();
    }

    private void deleteSelectedBook() {
        String selectedBook = booksListView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            statusLabel.setText("Please select a book to delete.");
            return;
        }

        String[] bookDetails = selectedBook.split(" - ");
        int bookId = Integer.parseInt(bookDetails[0]);

        String query = "DELETE FROM books WHERE book_id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, bookId);
            int rowsDeleted = stmt.executeUpdate();

            if (rowsDeleted > 0) {
                statusLabel.setText("Book deleted successfully!");
                fetchSellerBooks();
            } else {
                statusLabel.setText("Error: Book not found.");
            }
        } catch (SQLException e) {
            statusLabel.setText("Error deleting book: " + e.getMessage());
        }
    }

    private void openSettingsWindow(Stage currentStage) {
        Stage settingsStage = new Stage();
        settingsStage.setTitle("Settings");

        VBox settingsLayout = new VBox(10);
        settingsLayout.setPadding(new Insets(20));

        Button switchToBuyerButton = new Button("Switch to Buyer View");
        Button becomeBuyerButton = new Button("Become Buyer");
        Button displayUserInfoButton = new Button("Display User Info");
        Button changePasswordButton = new Button("Change Password");

        // Event handlers
        switchToBuyerButton.setOnAction(e -> {
            if (hasBuyerRole()) {
                switchToBuyer(currentStage);
                settingsStage.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "You do not have Buyer access.", ButtonType.OK);
                alert.showAndWait();
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

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("User Info");
                alert.setHeaderText("Your Information");
                alert.setContentText("First Name: " + firstName + "\n" +
                        "Last Name: " + lastName + "\n" +
                        "User ID: " + userId + "\n" +
                        "Role: " + role);
                alert.showAndWait();
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

    private String getFirstName() {
        String query = "SELECT first_name FROM users WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("first_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Seller";
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

    public static void main(String[] args) {
        // Launch the application without hardcoded values
        launch(args);
    }
}
