// BuyerView.java
package sunDevil_Books;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;

public class BuyerView extends Application {
    private String userId;
    private String username;

    private ListView<String> booksListView;
    private Label statusLabel;

    public BuyerView(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle(username + "'s Buyer Dashboard");

        Label welcomeLabel = new Label("Welcome, " + getFirstName() + "!");
        welcomeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Button hamburgerMenu = new Button("☰");
        Button logoutButton = new Button("Logout");
        HBox topBar = new HBox(10, hamburgerMenu, logoutButton);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(10));

        logoutButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Logged out successfully!", ButtonType.OK);
            alert.showAndWait();

            SplashScreenView splashScreenView = new SplashScreenView();
            splashScreenView.start(primaryStage);
        });

        booksListView = new ListView<>();
        fetchBooks();

        Button purchaseButton = new Button("Purchase Book");
        purchaseButton.setOnAction(e -> handlePurchase());

        statusLabel = new Label();

        hamburgerMenu.setOnAction(e -> openSettingsWindow(primaryStage));

        VBox layout = new VBox(10, welcomeLabel, topBar, booksListView, purchaseButton, statusLabel);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 450, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void fetchBooks() {
        String query = "SELECT book_id, name, author, price FROM books";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

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

    private void handlePurchase() {
        String selectedBook = booksListView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            statusLabel.setText("Please select a book to purchase.");
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
                statusLabel.setText("Book purchased successfully!");
                fetchBooks();
            } else {
                statusLabel.setText("Error: Book not found.");
            }
        } catch (SQLException e) {
            statusLabel.setText("Error purchasing book: " + e.getMessage());
        }
    }

    private void openSettingsWindow(Stage currentStage) {
        Stage settingsStage = new Stage();
        settingsStage.setTitle("Settings");

        VBox settingsLayout = new VBox(10);
        settingsLayout.setPadding(new Insets(20));

        Button switchToSellerButton = new Button();
        Button requestSellerAccessButton = new Button();
        Button displayUserInfoButton = new Button("Display User Info");
        Button changePasswordButton = new Button("Change Password");

        updateSellerButtons(switchToSellerButton, requestSellerAccessButton);

        switchToSellerButton.setOnAction(e -> {
            switchToSeller(currentStage);
            settingsStage.close();
        });

        requestSellerAccessButton.setOnAction(e -> {
            handleSellerAccessRequest(requestSellerAccessButton, switchToSellerButton);
        });

        displayUserInfoButton.setOnAction(e -> displayUserInfo());

        changePasswordButton.setOnAction(e -> changePassword());

        settingsLayout.getChildren().addAll(switchToSellerButton, requestSellerAccessButton, displayUserInfoButton, changePasswordButton);

        Scene settingsScene = new Scene(settingsLayout, 300, 250);
        settingsStage.setScene(settingsScene);
        settingsStage.show();
    }

    private void updateSellerButtons(Button switchToSellerButton, Button requestSellerAccessButton) {
        String role = getUserRole();

        if (role.contains("Seller") || role.equals("Admin")) {
            switchToSellerButton.setText("Switch to Seller View");
            switchToSellerButton.setDisable(false);

            requestSellerAccessButton.setText("Cancel Seller Access");
            requestSellerAccessButton.setDisable(false);
        } else {
            switchToSellerButton.setText("Switch to Seller View");
            switchToSellerButton.setDisable(true);

            if (hasPendingSellerRequest()) {
                requestSellerAccessButton.setText("Cancel Seller Access Request");
            } else {
                requestSellerAccessButton.setText("Request Seller Access");
            }
            requestSellerAccessButton.setDisable(false);
        }
    }

    private boolean hasPendingSellerRequest() {
        String query = "SELECT status FROM role_change_requests WHERE user_id = ? AND requested_role = 'Seller' ORDER BY request_date DESC LIMIT 1";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String status = rs.getString("status");
                return status.equals("Pending");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void handleSellerAccessRequest(Button requestSellerAccessButton, Button switchToSellerButton) {
        String buttonText = requestSellerAccessButton.getText();
        if (buttonText.equals("Request Seller Access")) {
            requestSellerAccess();
            requestSellerAccessButton.setText("Cancel Seller Access Request");
        } else if (buttonText.equals("Cancel Seller Access Request")) {
            cancelSellerAccessRequest();
            requestSellerAccessButton.setText("Request Seller Access");
        } else if (buttonText.equals("Cancel Seller Access")) {
            confirmCancelSellerAccess(requestSellerAccessButton, switchToSellerButton);
        }
    }

    private void requestSellerAccess() {
        String insertQuery = "INSERT INTO role_change_requests (user_id, requested_role) VALUES (?, 'Seller')";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
            insertStmt.setString(1, userId);
            insertStmt.executeUpdate();
            statusLabel.setText("Seller access request sent to admin.");
        } catch (SQLException e) {
            statusLabel.setText("Error requesting seller access: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cancelSellerAccessRequest() {
        String deleteQuery = "DELETE FROM role_change_requests WHERE user_id = ? AND status = 'Pending' AND requested_role = 'Seller'";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

            stmt.setString(1, userId);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                statusLabel.setText("Seller access request canceled.");
            } else {
                statusLabel.setText("No pending request found.");
            }

        } catch (SQLException e) {
            statusLabel.setText("Error canceling request: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void confirmCancelSellerAccess(Button requestSellerAccessButton, Button switchToSellerButton) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Cancellation");
        confirmDialog.setHeaderText("Are you sure you want to cancel Seller Access?");
        confirmDialog.setContentText("This will remove your seller privileges.");

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmDialog.getButtonTypes().setAll(yesButton, cancelButton);

        confirmDialog.showAndWait().ifPresent(type -> {
            if (type == yesButton) {
                cancelSellerAccess();
                requestSellerAccessButton.setText("Request Seller Access");
                switchToSellerButton.setDisable(true);
            }
        });
    }

    private void cancelSellerAccess() {
        String updateQuery = "UPDATE users SET role = ? WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            String currentRole = getUserRole();
            String newRole;
            if (currentRole.equals("BuyerSeller")) {
                newRole = "Buyer";
            } else if (currentRole.equals("Seller")) {
                newRole = "Buyer";
            } else {
                newRole = currentRole;
            }

            stmt.setString(1, newRole);
            stmt.setString(2, userId);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                statusLabel.setText("Seller access canceled.");
            } else {
                statusLabel.setText("Error updating user role.");
            }

        } catch (SQLException e) {
            statusLabel.setText("Error canceling seller access: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void switchToSeller(Stage currentStage) {
        currentStage.close();

        SellerView sellerView = new SellerView(userId, username);
        sellerView.start(new Stage());
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
            statusLabel.setText("Error displaying user info: " + e.getMessage());
        }
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
            statusLabel.setText("Error fetching user role: " + e.getMessage());
        }
        return "Buyer";
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
            statusLabel.setText("Error fetching first name: " + e.getMessage());
        }
        return "";
    }

    private void changePassword() {
        // Change password implementation (not provided in original code)
    }

    public static void main(String[] args) {
        launch(args);
    }
}
