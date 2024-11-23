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

    // Constructors
    public BuyerView() {
        // Default constructor required by JavaFX
    }

    // Parameterized constructor
    public BuyerView(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }
    
    // Setter methods to set userId and username after instantiation
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
        
        // Apply consistent styling
        mainLayout.setStyle("-fx-background-color: white; -fx-border-color: #FFC627; -fx-border-width: 3px; -fx-border-style: solid;");
        mainLayout.setPadding(new Insets(20));

        // Top Section with Logo and User Info
        HBox topSection = new HBox();
        topSection.setAlignment(Pos.CENTER_RIGHT);
        topSection.setSpacing(10);
        topSection.setPadding(new Insets(10));

        ImageView logoView = new ImageView();
        Image logo;
        try {
            logo = new Image(getClass().getResourceAsStream("sundevilbooks.png"));
        } catch (Exception e) {
            // If the image is not found, use a placeholder
            logo = new Image("https://via.placeholder.com/200x100.png?text=SunDevil+Books");
        }
        logoView.setImage(logo);
        logoView.setFitWidth(200);  
        logoView.setFitHeight(100); 
        logoView.setPreserveRatio(true); 

        // Settings Button
        Button settingsButton = Utils.createStyledButton("Settings");
        settingsButton.setOnAction(e -> openSettingsWindow(primaryStage));

        // Logout Button
        Button logoutButton = Utils.createStyledButton("Log Out");
        logoutButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Logged out successfully!", ButtonType.OK);
            alert.showAndWait();

            // Assuming SplashScreenView has a start method that accepts Stage
            SplashScreenView splashScreenView = new SplashScreenView();
            splashScreenView.start(primaryStage); // Go back to splash screen
        });
        
        // User Info Box
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

        // Main Content: Filters and Book List
        HBox mainContent = new HBox(30);
        mainContent.setAlignment(Pos.CENTER);

        // Filters Section
        VBox filtersSection = new VBox(20);
        filtersSection.setAlignment(Pos.TOP_LEFT);
        
        HBox filterBoxes = new HBox(20);
        
        // Category Filters
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

        // Condition Filters
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
        
        // Apply Filters Button
        Button applyFiltersButton = Utils.createStyledButton("Apply Filters");
        applyFiltersButton.setOnAction(e -> applyFilters());

        HBox applyFilterButtonBox = new HBox(applyFiltersButton);
        applyFilterButtonBox.setAlignment(Pos.CENTER);
        filtersSection.getChildren().addAll(filterBoxes, applyFilterButtonBox);

        // Book List Section
        VBox bookListSection = new VBox(10);
        bookListSection.setAlignment(Pos.TOP_CENTER);
        
        Label bookListLabel = Utils.createStyledLabel("Book List");
        bookList = new VBox(5);
        bookList.setPadding(new Insets(10));
        bookList.setAlignment(Pos.TOP_LEFT);
        
        // Initially populate with some books (can be removed if fetching from DB)
        populateInitialBooks();
        bookList.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
        bookList.setPrefSize(500, 400);

        // Bottom Controls: Search and Create Order
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

    // Method to populate initial books (can be removed if fetching from DB)
    private void populateInitialBooks() {
        bookList.getChildren().clear();
        // Sample books with assumed book_ids
        addBookToList("1 - Science - Introduction to Algorithms - Thomas H. Cormen - 2009 - $89.99 - New");
        addBookToList("2 - Engineering - Effective Java - Joshua Bloch - 2018 - $45.50 - Used");
        addBookToList("3 - Mathematics - Calculus Made Easy - Silvanus P. Thompson - 1910 - $30.00 - New");
        addBookToList("4 - Literature - The Great Gatsby - F. Scott Fitzgerald - 1925 - $25.75 - Used");
    }

    // Helper method to add a book to the bookList VBox
    private void addBookToList(String bookInfo) {
        CheckBox bookCheckBox = new CheckBox(bookInfo);
        bookCheckBox.setWrapText(true);
        bookCheckBox.setUserData(bookInfo); // Store bookInfo for later retrieval
        HBox bookBox = new HBox(bookCheckBox);
        bookBox.setAlignment(Pos.CENTER_LEFT);
        bookList.getChildren().add(bookBox);
    }

    // Apply Filters Method
    public void applyFilters() {
        // Retrieve selected categories
        StringBuilder categoryBuilder = new StringBuilder();
        if (scienceCheckBox.isSelected()) {
            categoryBuilder.append("Science,");
        }
        if (engineeringCheckBox.isSelected()) {
            categoryBuilder.append("Engineering,");
        }
        if (mathCheckBox.isSelected()) {
            categoryBuilder.append("Mathematics,");
        }
        String categories = categoryBuilder.toString();
        if (!categories.isEmpty()) {
            categories = categories.substring(0, categories.length() - 1); // Remove trailing comma
        }

        // Retrieve selected conditions
        StringBuilder conditionBuilder = new StringBuilder();
        if (newCheckBox.isSelected()) {
            conditionBuilder.append("New,");
        }
        if (usedCheckBox.isSelected()) {
            conditionBuilder.append("Used,");
        }
        if (heavilyUsedCheckBox.isSelected()) {
            conditionBuilder.append("Heavily Used,");
        }
        String conditions = conditionBuilder.toString();
        if (!conditions.isEmpty()) {
            conditions = conditions.substring(0, conditions.length() - 1); // Remove trailing comma
        }

        // Construct SQL query based on selected filters
        StringBuilder queryBuilder = new StringBuilder("SELECT name, author, price, category, book_condition, book_id, publishing_year FROM books WHERE 1=1");

        if (!categories.isEmpty()) {
            String[] categoryArray = categories.split(",");
            queryBuilder.append(" AND category IN (");
            for (int i = 0; i < categoryArray.length; i++) {
                queryBuilder.append("?");
                if (i < categoryArray.length - 1) {
                    queryBuilder.append(",");
                }
            }
            queryBuilder.append(")");
        }

        if (!conditions.isEmpty()) {
            String[] conditionArray = conditions.split(",");
            queryBuilder.append(" AND book_condition IN (");
            for (int i = 0; i < conditionArray.length; i++) {
                queryBuilder.append("?");
                if (i < conditionArray.length - 1) {
                    queryBuilder.append(",");
                }
            }
            queryBuilder.append(")");
        }

        // Execute the query and update the book list
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString())) {

            int paramIndex = 1;

            // Set category parameters
            if (!categories.isEmpty()) {
                String[] categoryArray = categories.split(",");
                for (String category : categoryArray) {
                    stmt.setString(paramIndex++, category.trim());
                }
            }

            // Set condition parameters
            if (!conditions.isEmpty()) {
                String[] conditionArray = conditions.split(",");
                for (String condition : conditionArray) {
                    stmt.setString(paramIndex++, condition.trim());
                }
            }

            ResultSet rs = stmt.executeQuery();

            // Clear existing books in the list
            bookList.getChildren().clear();

            boolean hasResults = false;

            while (rs.next()) {
                hasResults = true;
                String name = rs.getString("name");
                String author = rs.getString("author");
                double price = rs.getDouble("price");
                String category = rs.getString("category");
                String condition = rs.getString("book_condition");
                int bookId = rs.getInt("book_id");
                int publishingYear = rs.getInt("publishing_year");

                // Format: "book_id - category - name - author - publishing_year - $price - condition"
                String bookInfo = String.format("%d - %s - %s - %s - %d - $%.2f - %s",
                        bookId, category, name, author, publishingYear, price, condition);
                addBookToList(bookInfo);
            }

            if (!hasResults) {
                Label noBooksLabel = new Label("No books match the selected filters.");
                noBooksLabel.setStyle("-fx-text-fill: red;");
                bookList.getChildren().add(noBooksLabel);
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error applying filters: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Create Order Method
    private void createOrder() {
        // Collect selected books
        VBox selectedBooksBox = bookList;
        boolean anySelected = false;
        StringBuilder orderDetails = new StringBuilder();
        
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD)) {
            conn.setAutoCommit(false); // Begin transaction

            for (javafx.scene.Node node : selectedBooksBox.getChildren()) {
                if (node instanceof HBox) {
                    HBox hBox = (HBox) node;
                    if (hBox.getChildren().get(0) instanceof CheckBox) {
                        CheckBox checkBox = (CheckBox) hBox.getChildren().get(0);
                        if (checkBox.isSelected()) {
                            anySelected = true;
                            String bookInfo = (String) checkBox.getUserData();
                            // Parse bookInfo to extract book_id and price
                            // Expected format: "book_id - category - name - author - publishing_year - $price - condition"
                            String[] parts = bookInfo.split(" - ");
                            if (parts.length < 7) {
                                showAlert(Alert.AlertType.ERROR, "Parsing Error", "Invalid book information format.");
                                return;
                            }

                            int bookId = Integer.parseInt(parts[0].trim());
                            String category = parts[1].trim();
                            String name = parts[2].trim();
                            String author = parts[3].trim();
                            int publishingYear = Integer.parseInt(parts[4].trim());
                            String priceStr = parts[5].trim().replace("$", "");
                            double price = Double.parseDouble(priceStr);
                            String condition = parts[6].trim();

                            // Insert into transactions table
                            String insertTransaction = "INSERT INTO transactions (book_id, sale_price, buyer_id, seller_id, transaction_date) VALUES (?, ?, ?, ?, ?)";
                            try (PreparedStatement pstmt = conn.prepareStatement(insertTransaction)) {
                                pstmt.setInt(1, bookId);
                                pstmt.setDouble(2, price);
                                pstmt.setString(3, this.userId);
                                pstmt.setString(4, "DIVOLO2311"); // Assuming 'DIVOLO2311' is a default seller
                                pstmt.setDate(5, Date.valueOf(LocalDate.now()));
                                pstmt.executeUpdate();
                            }

                            orderDetails.append(String.format("Book ID: %d, Name: %s, Price: $%.2f\n", bookId, name, price));
                        }
                    }
                }
            }

            if (!anySelected) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select at least one book to create an order.");
                return;
            }

            conn.commit(); // Commit transaction

            showAlert(Alert.AlertType.INFORMATION, "Order Created", "Your order has been successfully created:\n" + orderDetails.toString());

            // Optionally, refresh the book list or perform other actions
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error creating order: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Helper method to show alerts
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    // Settings Window Method
    private void openSettingsWindow(Stage primaryStage) {
        Stage settingsStage = new Stage();
        settingsStage.setTitle("Settings");

        VBox settingsLayout = new VBox(10);
        settingsLayout.setPadding(new Insets(20));
        settingsLayout.setAlignment(Pos.CENTER);

        Button switchToSellerButton = Utils.createStyledButton("Switch to Seller View");
        Button displayUserInfoButton = Utils.createStyledButton("Display User Info");
        Button changePasswordButton = Utils.createStyledButton("Change Password");

        // Switch to Seller View Action
        switchToSellerButton.setOnAction(e -> {
            switchToSeller(primaryStage);
            settingsStage.close();
        });

        // Display User Info Action
        displayUserInfoButton.setOnAction(e -> displayUserInfo());

        // Change Password Action
        changePasswordButton.setOnAction(e -> changePassword());

        settingsLayout.getChildren().addAll(switchToSellerButton, displayUserInfoButton, changePasswordButton);

        Scene settingsScene = new Scene(settingsLayout, 300, 200);
        settingsStage.setScene(settingsScene);
        settingsStage.initOwner(primaryStage); // Set owner to primaryStage
        settingsStage.show();
    }

    // Switch to Seller View Method
    private void switchToSeller(Stage currentStage) {
        currentStage.close();

        // Assuming SellerView has a constructor that accepts userId and username
        SellerView sellerView = new SellerView(userId, username);
        sellerView.start(new Stage());
    }

    // Display User Information Method
    private void displayUserInfo() {
        // For demonstration purposes, we'll show a simple alert with user info
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

    // Placeholder for Change Password Implementation
    private void changePassword() {
        // Implement change password functionality here
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Change Password");
        alert.setHeaderText(null);
        alert.setContentText("Change Password functionality to be implemented.");
        alert.showAndWait();
    }
}
