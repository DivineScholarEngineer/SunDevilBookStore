package sunDevil_Books;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;

public class BuyerView extends Application {

    private String firstName;
    private String lastName;
    private String username;
    private String userId;

    // Instance variables for filter checkboxes
    private CheckBox scienceCheckBox;
    private CheckBox engineeringCheckBox;
    private CheckBox mathCheckBox;

    private CheckBox newCheckBox;
    private CheckBox usedCheckBox;
    private CheckBox heavilyUsedCheckBox;

    // Instance variable for book list
    private VBox bookList;

    public BuyerView() {
        // Default constructor required by JavaFX
    }

    public BuyerView(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Buyer View");
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: white; -fx-border-color: #FFC627; -fx-border-width: 3px; -fx-border-style: solid;");
        mainLayout.setPadding(new Insets(20));

        HBox topSection = new HBox();
        topSection.setAlignment(Pos.CENTER_RIGHT);
        topSection.setSpacing(10);
        topSection.setPadding(new Insets(10));

        ImageView logoView = new ImageView();
        Image logo;
        try {
            logo = new Image(getClass().getResourceAsStream("sundevilbooks.png"));
        } catch (Exception e) {
            logo = new Image("https://via.placeholder.com/200x100.png?text=SunDevil+Books");
        }
        logoView.setImage(logo);
        logoView.setFitWidth(200);
        logoView.setFitHeight(100);
        logoView.setPreserveRatio(true);

        Button settingsButton = Utils.createStyledButton("Settings");
        settingsButton.setOnAction(e -> openSettingsWindow(primaryStage));

        Button logoutButton = Utils.createStyledButton("Log Out");
        logoutButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Logged out successfully!", ButtonType.OK);
            alert.showAndWait();
            SplashScreenView splashScreenView = new SplashScreenView();
            splashScreenView.start(primaryStage);
        });

        VBox userInfoBox = new VBox(5);
        userInfoBox.getChildren().addAll(
                new Label("User: " + username),
                new Label("Role: Buyer"),
                settingsButton,
                logoutButton
        );
        userInfoBox.setAlignment(Pos.TOP_RIGHT);
        HBox.setHgrow(userInfoBox, Priority.ALWAYS);
        topSection.getChildren().addAll(logoView, userInfoBox);

        HBox mainContent = new HBox(30);
        mainContent.setAlignment(Pos.CENTER);

        VBox filtersSection = new VBox(20);
        filtersSection.setAlignment(Pos.TOP_LEFT);
        HBox filterBoxes = new HBox(20);

        VBox categorySection = new VBox(10);
        categorySection.setAlignment(Pos.TOP_CENTER);
        Label categoryLabel = Utils.createStyledLabel("Category");
        categoryLabel.setAlignment(Pos.CENTER);

        VBox categoryFilters = new VBox(5);
        categoryFilters.setPadding(new Insets(10));
        categoryFilters.setAlignment(Pos.TOP_LEFT);
        scienceCheckBox = new CheckBox("Science");
        engineeringCheckBox = new CheckBox("Engineering");
        mathCheckBox = new CheckBox("Mathematics");
        categoryFilters.getChildren().addAll(scienceCheckBox, engineeringCheckBox, mathCheckBox);
        categoryFilters.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
        categoryFilters.setPrefSize(180, 150);
        categorySection.getChildren().addAll(categoryLabel, categoryFilters);

        VBox conditionSection = new VBox(10);
        conditionSection.setAlignment(Pos.TOP_CENTER);
        Label conditionLabel = Utils.createStyledLabel("Condition");
        conditionLabel.setAlignment(Pos.CENTER);

        VBox conditionFilters = new VBox(5);
        conditionFilters.setPadding(new Insets(10));
        conditionFilters.setAlignment(Pos.TOP_LEFT);
        newCheckBox = new CheckBox("New");
        usedCheckBox = new CheckBox("Used");
        heavilyUsedCheckBox = new CheckBox("Heavily Used");
        conditionFilters.getChildren().addAll(newCheckBox, usedCheckBox, heavilyUsedCheckBox);
        conditionFilters.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
        conditionFilters.setPrefSize(180, 150);
        conditionSection.getChildren().addAll(conditionLabel, conditionFilters);

        filterBoxes.getChildren().addAll(categorySection, conditionSection);

        Button applyFiltersButton = Utils.createStyledButton("Apply Filters");
        applyFiltersButton.setOnAction(e -> applyFilters());

        HBox applyFilterButtonBox = new HBox(applyFiltersButton);
        applyFilterButtonBox.setAlignment(Pos.CENTER);
        filtersSection.getChildren().addAll(filterBoxes, applyFilterButtonBox);

        VBox bookListSection = new VBox(10);
        bookListSection.setAlignment(Pos.TOP_CENTER);
        Label bookListLabel = Utils.createStyledLabel("Book List");
        bookList = new VBox(5);
        bookList.setPadding(new Insets(10));
        bookList.setAlignment(Pos.TOP_LEFT);
        populateInitialBooks();
        bookList.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
        bookList.setPrefSize(500, 400);

        HBox bottomControls = new HBox(10);
        bottomControls.setAlignment(Pos.CENTER);
        TextField searchField = new TextField();
        searchField.setPromptText("Search for a specific book");
        searchField.setPrefWidth(300);

        Button createOrderButton = Utils.createStyledButton("Create Order");
        createOrderButton.setOnAction(e -> createOrder());

        bottomControls.getChildren().addAll(searchField, createOrderButton);
        bookListSection.getChildren().addAll(bookListLabel, bookList, bottomControls);
        mainContent.getChildren().addAll(filtersSection, bookListSection);

        mainLayout.setTop(topSection);
        mainLayout.setCenter(mainContent);

        Scene scene = new Scene(mainLayout, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void populateInitialBooks() {
        bookList.getChildren().clear();

        String query = "SELECT * FROM books ORDER BY book_id DESC";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            int counter = 1;
            while (rs.next()) {
                String bookInfo = String.format("%d - %s - %s - %s - %d - $%.2f - %s",
                        counter,
                        rs.getString("category"),
                        rs.getString("name"),
                        rs.getString("author"),
                        rs.getInt("publishing_year"),
                        rs.getDouble("price"),
                        rs.getString("book_condition"));
                addBookToList(bookInfo);
                counter++;
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error fetching books: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void applyFilters() {
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM books WHERE 1=1");

        if (scienceCheckBox.isSelected()) queryBuilder.append(" AND category = 'Science'");
        if (engineeringCheckBox.isSelected()) queryBuilder.append(" AND category = 'Engineering'");
        if (mathCheckBox.isSelected()) queryBuilder.append(" AND category = 'Mathematics'");

        if (newCheckBox.isSelected()) queryBuilder.append(" AND book_condition = 'New'");
        if (usedCheckBox.isSelected()) queryBuilder.append(" AND book_condition = 'Used'");
        if (heavilyUsedCheckBox.isSelected()) queryBuilder.append(" AND book_condition = 'Heavily Used'");

        bookList.getChildren().clear();

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(queryBuilder.toString())) {

            int counter = 1;
            while (rs.next()) {
                String bookInfo = String.format("%d - %s - %s - %s - %d - $%.2f - %s",
                        counter,
                        rs.getString("category"),
                        rs.getString("name"),
                        rs.getString("author"),
                        rs.getInt("publishing_year"),
                        rs.getDouble("price"),
                        rs.getString("book_condition"));
                addBookToList(bookInfo);
                counter++;
            }

            if (counter == 1) {
                bookList.getChildren().add(new Label("No books match the selected filters."));
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error applying filters: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createOrder() {
        VBox selectedBooksBox = bookList;
        boolean anySelected = false;
        StringBuilder orderDetails = new StringBuilder();

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD)) {
            conn.setAutoCommit(false);

            for (javafx.scene.Node node : selectedBooksBox.getChildren()) {
                if (node instanceof HBox) {
                    HBox hBox = (HBox) node;
                    if (hBox.getChildren().get(0) instanceof CheckBox) {
                        CheckBox checkBox = (CheckBox) hBox.getChildren().get(0);
                        if (checkBox.isSelected()) {
                            anySelected = true;
                            String bookInfo = (String) checkBox.getUserData();

                            String[] parts = bookInfo.split(" - ");
                            if (parts.length < 7) {
                                showAlert(Alert.AlertType.ERROR, "Parsing Error", "Invalid book information format.");
                                return;
                            }

                            int bookId = Integer.parseInt(parts[0].trim());
                            double price = Double.parseDouble(parts[5].trim().replace("$", ""));
                            String sellerId = "DIVOLO2311";

                            String insertTransaction = "INSERT INTO transactions (book_id, sale_price, buyer_id, seller_id, transaction_date) VALUES (?, ?, ?, ?, ?)";
                            try (PreparedStatement pstmt = conn.prepareStatement(insertTransaction)) {
                                pstmt.setInt(1, bookId);
                                pstmt.setDouble(2, price);
                                pstmt.setString(3, this.userId);
                                pstmt.setString(4, sellerId);
                                pstmt.setDate(5, Date.valueOf(LocalDate.now()));
                                pstmt.executeUpdate();
                            }

                            orderDetails.append(String.format("Book ID: %d, Name: %s, Price: $%.2f\n", bookId, parts[2], price));
                        }
                    }
                }
            }

            if (!anySelected) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select at least one book to create an order.");
                return;
            }

            conn.commit();
            showAlert(Alert.AlertType.INFORMATION, "Order Created", "Your order has been successfully created:\n" + orderDetails.toString());

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error creating order: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addBookToList(String bookInfo) {
        CheckBox bookCheckBox = new CheckBox(bookInfo);
        bookCheckBox.setWrapText(true);
        bookCheckBox.setUserData(bookInfo);
        HBox bookBox = new HBox(bookCheckBox);
        bookBox.setAlignment(Pos.CENTER_LEFT);
        bookList.getChildren().add(bookBox);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void openSettingsWindow(Stage primaryStage) {
        Stage settingsStage = new Stage();
        settingsStage.setTitle("Settings");

        VBox settingsLayout = new VBox(10);
        settingsLayout.setPadding(new Insets(20));
        settingsLayout.setAlignment(Pos.CENTER);

        Button switchToSellerButton = new Button("Switch to Seller View");
        switchToSellerButton.setOnAction(e -> {
            switchToSeller(primaryStage);
            settingsStage.close();
        });

        Button displayUserInfoButton = new Button("Display User Info");
        displayUserInfoButton.setOnAction(e -> displayUserInfo());

        Button changePasswordButton = new Button("Change Password");
        changePasswordButton.setOnAction(e -> changePassword());

        settingsLayout.getChildren().addAll(switchToSellerButton, displayUserInfoButton, changePasswordButton);

        Scene settingsScene = new Scene(settingsLayout, 300, 200);
        settingsStage.setScene(settingsScene);
        settingsStage.initOwner(primaryStage);
        settingsStage.show();
    }

    private void switchToSeller(Stage currentStage) {
        String updateRoleQuery = "UPDATE users SET role = 'Seller' WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(updateRoleQuery)) {
            stmt.setString(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error switching to Seller: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        currentStage.close();
        SellerView sellerView = new SellerView(userId, username);
        sellerView.start(new Stage());
    }

    private void displayUserInfo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("User Info");
        alert.setHeaderText("Your Information");
        alert.setContentText("First Name: " + firstName + "\n" +
                "Last Name: " + lastName + "\n" +
                "Username: " + username + "\n" +
                "User ID: " + userId + "\n" +
                "Role: Buyer");
        alert.showAndWait();
    }

    private void changePassword() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Enter a new password:");
        dialog.setContentText("New Password:");

        dialog.showAndWait().ifPresent(newPassword -> {
            if (newPassword.trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Password cannot be empty.", "");
                return;
            }

            String query = "UPDATE users SET password = ? WHERE user_id = ?";
            try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, newPassword);
                stmt.setString(2, userId);
                stmt.executeUpdate();
                showAlert(Alert.AlertType.INFORMATION, "Password Updated", "Your password has been successfully updated.");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Error updating password: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
