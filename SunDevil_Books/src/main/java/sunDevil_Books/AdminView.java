package sunDevil_Books; // Replace with your actual package name

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDate;

import sunDevil_Books.DatabaseConfig;


public class AdminView extends Application {

    private ImageView logoImageView;
    
    private String firstName;
    private String lastName;
    private String username;
    private String userId;

    public AdminView(String firstName, String lastName, String username, String userId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.userId = userId;
    }


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Admin Management");

        // Initialize Database (Create tables if not exist)
        initializeDatabase();

        // Layout for the top (logo, admin details, log out button)
        HBox topBar = createTopBar(primaryStage);
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-border-color: gold; -fx-border-width: 0 0 3 0; -fx-background-color: #F8F8F8;");

        // Tabs for managing books, users, and reports
        TabPane tabPane = new TabPane();
        Tab manageBooksTab = new Tab("Manage Books");
        Tab manageUsersTab = new Tab("Manage Users");
        Tab reportsTab = new Tab("Reports");

        // Manage Books Tab
        HBox booksPane = createManageBooksPane();
        manageBooksTab.setContent(booksPane);
        manageBooksTab.setClosable(false);

        // Manage Users Tab
        HBox usersPane = createManageUsersPane();
        manageUsersTab.setContent(usersPane);
        manageUsersTab.setClosable(false);

        // Reports Tab
        VBox reportsBox = createReportsPane();
        reportsTab.setContent(reportsBox);
        reportsTab.setClosable(false);

        // Add all tabs to the tab pane
        tabPane.getTabs().addAll(manageBooksTab, manageUsersTab, reportsTab);

        // Set up the main layout
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(topBar);
        borderPane.setCenter(tabPane);
        borderPane.setPadding(new Insets(20));

        Scene scene = new Scene(borderPane, 1400, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Function to create the top bar with logo, admin info, and log out button
    private HBox createTopBar(Stage stage) {
        // Logo section
        File file = new File("C:\\Users\\divin\\eclipse-workspace\\SunDevil_Books\\src\\sunDevil_Books\\sundevilbooks.jpg");
        Image logoImage;
        if (file.exists()) {
            logoImage = new Image(file.toURI().toString());
        } else {
            InputStream logoStream = getClass().getResourceAsStream("/sunDevil_Books/sundevilbooks.jpg");
            if (logoStream != null) {
                logoImage = new Image(logoStream);
            } else {
                logoImage = null;
                showAlert(Alert.AlertType.ERROR, "Default logo image not found.");
            }
        }

        logoImageView = new ImageView(logoImage);
        logoImageView.setFitWidth(100);
        logoImageView.setFitHeight(100);

        Button uploadLogoButton = new Button("Change Logo");
        uploadLogoButton.setOnAction(e -> uploadLogo(stage));

        VBox logoBox = new VBox(10, logoImageView, uploadLogoButton);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.setPadding(new Insets(10));

        // Admin details
        Label userLabel = new Label("User: " + firstName + " " + lastName + " (" + username + " - " + userId + ")");
        Label roleLabel = new Label("Role: Admin");

        VBox userInfoBox = new VBox(5, userLabel, roleLabel);
        userInfoBox.setAlignment(Pos.CENTER_RIGHT);
        userInfoBox.setPadding(new Insets(10));


        // Log out button
        Button logoutButton = new Button("Log Out");
        logoutButton.setStyle("-fx-background-color: #800020; -fx-text-fill: white;");
        logoutButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Logged out successfully!", ButtonType.OK);
            alert.showAndWait();

            // Redirect to the splash screen instead of exiting the application
            SplashScreenView splashScreenView = new SplashScreenView();
            splashScreenView.start(stage); // Go back to splash screen
        });
//        logoutButton.setOnAction(e -> {
//            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Logged out successfully!", ButtonType.OK);
//            alert.showAndWait();
//            Platform.exit(); // Exits the entire application
//        });

        VBox logoutBox = new VBox(logoutButton);
        logoutBox.setAlignment(Pos.CENTER_RIGHT);
        logoutBox.setPadding(new Insets(10));

        HBox topBar = new HBox(20, logoBox, userInfoBox, logoutBox);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(userInfoBox, Priority.ALWAYS);

        return topBar;
    }

    // Function to handle logo upload
    private void uploadLogo(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Logo Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            Image newLogo = new Image(selectedFile.toURI().toString());
            logoImageView.setImage(newLogo);
        }
    }

    // Manage Books Pane
    private HBox createManageBooksPane() {
        VBox addBooksPane = new VBox(10);
        addBooksPane.setAlignment(Pos.CENTER);
        addBooksPane.setPadding(new Insets(10));

        Label addBookTitle = new Label("Add Books");
        addBookTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: gold;");

        ComboBox<String> bookCategoryComboBox = new ComboBox<>();
        bookCategoryComboBox.getItems().addAll("Natural Science", "Engineering", "Literature", "Other");
        bookCategoryComboBox.setPromptText("Select Category");

        TextField bookNameField = new TextField();
        bookNameField.setPromptText("Enter Book Name");

        TextField bookAuthorField = new TextField();
        bookAuthorField.setPromptText("Enter Book Author");

        TextField yearField = new TextField();
        yearField.setPromptText("Enter Publishing Year");

        TextField priceField = new TextField();
        priceField.setPromptText("Enter Price");

        ComboBox<String> conditionComboBox = new ComboBox<>();
        conditionComboBox.getItems().addAll("New", "Used", "Heavily Used");
        conditionComboBox.setPromptText("Select Condition");

        Button addBookButton = new Button("Add Book");
        addBookButton.setStyle("-fx-background-color: #800020; -fx-text-fill: white;");

        addBookButton.setOnAction(e -> {
            String category = bookCategoryComboBox.getValue();
            String name = bookNameField.getText().trim();
            String author = bookAuthorField.getText().trim();
            String yearStr = yearField.getText().trim();
            String priceStr = priceField.getText().trim();
            String condition = conditionComboBox.getValue();

            if (category == null || name.isEmpty() || author.isEmpty() ||
                    yearStr.isEmpty() || priceStr.isEmpty() || condition == null) {
                showAlert(Alert.AlertType.WARNING, "Please fill in all fields to add a book.");
                return;
            }

            int year;
            double price;
            try {
                year = Integer.parseInt(yearStr);
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid year or price format.");
                return;
            }

            addBook(category, name, author, year, price, condition);
            // Clear fields after adding
            bookCategoryComboBox.setValue(null);
            bookNameField.clear();
            bookAuthorField.clear();
            yearField.clear();
            priceField.clear();
            conditionComboBox.setValue(null);
        });

        addBooksPane.getChildren().addAll(
                addBookTitle,
                new Label("Book Category:"), bookCategoryComboBox,
                new Label("Book Name:"), bookNameField,
                new Label("Book Author:"), bookAuthorField,
                new Label("Publishing Year:"), yearField,
                new Label("Price:"), priceField,
                new Label("Condition:"), conditionComboBox,
                addBookButton
        );

        VBox editBooksPane = createEditBooksPane();
        VBox removeBooksPane = createRemoveBooksPane();

        HBox booksPane = new HBox(50, addBooksPane, editBooksPane, removeBooksPane);
        booksPane.setPadding(new Insets(20));

        return booksPane;
    }

    // Function to create Edit Books Pane
    private VBox createEditBooksPane() {
        VBox editBooksPane = new VBox(10);
        editBooksPane.setAlignment(Pos.CENTER);
        editBooksPane.setPadding(new Insets(10));

        Label editBookTitle = new Label("Edit Books");
        editBookTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: gold;");

        TextField bookIdField = new TextField();
        bookIdField.setPromptText("Enter Book ID");

        TextField editBookNameField = new TextField();
        editBookNameField.setPromptText("Enter Book Name");

        TextField editBookAuthorField = new TextField();
        editBookAuthorField.setPromptText("Enter Book Author");

        TextField editYearField = new TextField();
        editYearField.setPromptText("Enter Publishing Year");

        TextField editPriceField = new TextField();
        editPriceField.setPromptText("Enter Price");

        ComboBox<String> editConditionComboBox = new ComboBox<>();
        editConditionComboBox.getItems().addAll("New", "Used", "Heavily Used");
        editConditionComboBox.setPromptText("Select Condition");

        ComboBox<String> editBookCategoryComboBox = new ComboBox<>();
        editBookCategoryComboBox.getItems().addAll("Natural Science", "Engineering", "Literature", "Other");
        editBookCategoryComboBox.setPromptText("Select Category");

        Button editBookButton = new Button("Edit Book");
        editBookButton.setStyle("-fx-background-color: #800020; -fx-text-fill: white;");

        editBookButton.setOnAction(e -> {
            String bookIdStr = bookIdField.getText().trim();
            String category = editBookCategoryComboBox.getValue();
            String name = editBookNameField.getText().trim();
            String author = editBookAuthorField.getText().trim();
            String yearStr = editYearField.getText().trim();
            String priceStr = editPriceField.getText().trim();
            String condition = editConditionComboBox.getValue();

            if (bookIdStr.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Please enter Book ID to edit.");
                return;
            }

            int bookId;
            try {
                bookId = Integer.parseInt(bookIdStr);
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid Book ID format.");
                return;
            }

            if (category == null && name.isEmpty() && author.isEmpty() &&
                    yearStr.isEmpty() && priceStr.isEmpty() && condition == null) {
                showAlert(Alert.AlertType.WARNING, "Please fill in at least one field to update.");
                return;
            }

            Integer year = null;
            if (!yearStr.isEmpty()) {
                try {
                    year = Integer.parseInt(yearStr);
                } catch (NumberFormatException ex) {
                    showAlert(Alert.AlertType.ERROR, "Invalid year format.");
                    return;
                }
            }

            Double price = null;
            if (!priceStr.isEmpty()) {
                try {
                    price = Double.parseDouble(priceStr);
                } catch (NumberFormatException ex) {
                    showAlert(Alert.AlertType.ERROR, "Invalid price format.");
                    return;
                }
            }

            editBook(bookId, category, name, author, year, price, condition);
            bookIdField.clear();
            editBookCategoryComboBox.setValue(null);
            editBookNameField.clear();
            editBookAuthorField.clear();
            editYearField.clear();
            editPriceField.clear();
            editConditionComboBox.setValue(null);
        });

        editBooksPane.getChildren().addAll(
                editBookTitle,
                new Label("Book ID:"), bookIdField,
                new Label("Book Name:"), editBookNameField,
                new Label("Book Author:"), editBookAuthorField,
                new Label("Publishing Year:"), editYearField,
                new Label("Price:"), editPriceField,
                new Label("Condition:"), editConditionComboBox,
                new Label("Book Category:"), editBookCategoryComboBox,
                editBookButton
        );

        return editBooksPane;
    }

    // Function to create Remove Books Pane
    private VBox createRemoveBooksPane() {
        VBox removeBooksPane = new VBox(10);
        removeBooksPane.setAlignment(Pos.CENTER);
        removeBooksPane.setPadding(new Insets(10));

        Label removeBookTitle = new Label("Remove Books");
        removeBookTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: gold;");

        TextField removeBookIdField = new TextField();
        removeBookIdField.setPromptText("Enter Book ID");

        Button removeBookButton = new Button("Remove Book");
        removeBookButton.setStyle("-fx-background-color: #800020; -fx-text-fill: white;");

        removeBookButton.setOnAction(e -> {
            String bookIdStr = removeBookIdField.getText().trim();
            if (bookIdStr.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Please enter Book ID to remove.");
                return;
            }

            int bookId;
            try {
                bookId = Integer.parseInt(bookIdStr);
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid Book ID format.");
                return;
            }

            removeBook(bookId);
            removeBookIdField.clear();
        });

        removeBooksPane.getChildren().addAll(
                removeBookTitle,
                new Label("Book ID:"), removeBookIdField,
                removeBookButton
        );

        return removeBooksPane;
    }

    // Manage Users Pane
    private HBox createManageUsersPane() {
        // Add Users section
        VBox addUserPane = new VBox(10);
        addUserPane.setAlignment(Pos.CENTER);
        addUserPane.setPadding(new Insets(10));

        Label addUserTitle = new Label("Add Users");
        addUserTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: gold;");

        ComboBox<String> userRoleComboBox = new ComboBox<>();
        userRoleComboBox.getItems().addAll("Buyer", "Seller", "Admin");
        userRoleComboBox.setPromptText("Select Role");

        TextField userIdField = new TextField();
        userIdField.setPromptText("Enter an ASU ID");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter a Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter a password");

        Button addUserButton = new Button("Add User");
        addUserButton.setStyle("-fx-background-color: #800020; -fx-text-fill: white;");

        addUserButton.setOnAction(e -> {
            String role = userRoleComboBox.getValue();
            String userId = userIdField.getText().trim();
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (role == null || userId.isEmpty() || username.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Please fill in all fields to add a user.");
                return;
            }

            addUser(role, userId, username, password);
            userRoleComboBox.setValue(null);
            userIdField.clear();
            usernameField.clear();
            passwordField.clear();
        });

        addUserPane.getChildren().addAll(
                addUserTitle,
                new Label("Enter User Role:"), userRoleComboBox,
                new Label("User ID:"), userIdField,
                new Label("Username:"), usernameField,
                new Label("Password:"), passwordField,
                addUserButton
        );

        VBox editUserPane = createEditUserPane();
        VBox removeUserPane = createRemoveUserPane();

        HBox usersPane = new HBox(50, addUserPane, editUserPane, removeUserPane);
        usersPane.setPadding(new Insets(20));

        return usersPane;
    }

    // Function to create Edit Users Pane
    private VBox createEditUserPane() {
        VBox editUserPane = new VBox(10);
        editUserPane.setAlignment(Pos.CENTER);
        editUserPane.setPadding(new Insets(10));

        Label editUserTitle = new Label("Edit Users");
        editUserTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: gold;");

        TextField editUserIdField = new TextField();
        editUserIdField.setPromptText("Enter an ASU ID");

        TextField editUsernameField = new TextField();
        editUsernameField.setPromptText("Enter a New Username");

        ComboBox<String> editUserRoleComboBox = new ComboBox<>();
        editUserRoleComboBox.getItems().addAll("Buyer", "Seller", "Admin");
        editUserRoleComboBox.setPromptText("Select New Role");

        Button editUserButton = new Button("Edit User");
        editUserButton.setStyle("-fx-background-color: #800020; -fx-text-fill: white;");

        editUserButton.setOnAction(e -> {
            String userId = editUserIdField.getText().trim();
            String newRole = editUserRoleComboBox.getValue();
            String newUsername = editUsernameField.getText().trim();

            if (userId.isEmpty() || (newRole == null && newUsername.isEmpty())) {
                showAlert(Alert.AlertType.WARNING, "Please enter User ID and select a new role or username.");
                return;
            }

            editUser(userId, newRole, newUsername);
            editUserIdField.clear();
            editUsernameField.clear();
            editUserRoleComboBox.setValue(null);
        });

        editUserPane.getChildren().addAll(
                editUserTitle,
                new Label("User ID:"), editUserIdField,
                new Label("Update Username:"), editUsernameField,
                new Label("Update Role:"), editUserRoleComboBox,
                editUserButton
        );

        return editUserPane;
    }

    // Function to create Remove Users Pane
    private VBox createRemoveUserPane() {
        VBox removeUserPane = new VBox(10);
        removeUserPane.setAlignment(Pos.CENTER);
        removeUserPane.setPadding(new Insets(10));

        Label removeUserTitle = new Label("Remove Users");
        removeUserTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: gold;");

        TextField removeUserIdField = new TextField();
        removeUserIdField.setPromptText("Enter an ASU ID");

        Button removeUserButton = new Button("Remove User");
        removeUserButton.setStyle("-fx-background-color: #800020; -fx-text-fill: white;");

        removeUserButton.setOnAction(e -> {
            String userId = removeUserIdField.getText().trim();
            if (userId.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Please enter User ID to remove.");
                return;
            }

            removeUser(userId);
            removeUserIdField.clear();
        });

        removeUserPane.getChildren().addAll(
                removeUserTitle,
                new Label("User ID:"), removeUserIdField,
                removeUserButton
        );

        return removeUserPane;
    }

    // Manage Reports Pane
    private VBox createReportsPane() {
        Label transactionsLabel = new Label("Transactions");
        ListView<String> transactionsList = new ListView<>();
        fetchTransactions(transactionsList);

        Label bookIdsLabel = new Label("Book IDs");
        ListView<String> bookIdsList = new ListView<>();
        fetchBookIds(bookIdsList);

        HBox transactionBookIdsBox = new HBox(20, transactionsList, bookIdsList);

        Label reportCategoryLabel = new Label("Report Category:");
        ComboBox<String> reportCategoryComboBox = new ComboBox<>();
        reportCategoryComboBox.getItems().addAll("Books and Sales", "Users", "Transactions", "Inventory", "Revenue");
        reportCategoryComboBox.setPromptText("Select Category");

        Button generateReportButton = new Button("Generate Report");
        generateReportButton.setStyle("-fx-background-color: #800020; -fx-text-fill: white;");
        generateReportButton.setOnAction(e -> generateReport(reportCategoryComboBox.getValue()));

        VBox reportsBox = new VBox(15, transactionsLabel, transactionBookIdsBox, reportCategoryLabel, reportCategoryComboBox, generateReportButton);
        reportsBox.setAlignment(Pos.CENTER);
        reportsBox.setPadding(new Insets(20));

        return reportsBox;
    }

    // Function to initialize the database (execute SQL from file)
 // Function to initialize the database (execute SQL from file)
 // Only connect to the database; don't re-run the SQL file
    private void initializeDatabase() {
        try {
            // Load JDBC Driver
            Class.forName(DatabaseConfig.JDBC_DRIVER);
            // Connect to the database (DB_URL should point to the database directly)
            try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD)) {
                System.out.println("Connected to the database successfully.");
                // You can add more logic here if needed, but avoid re-running the SQL file.
            }
        } catch (ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database connection error: " + e.getMessage());
        }
    }

//    private void initializeDatabase() {
//        try {
//            // Load the JDBC driver
//            Class.forName(DatabaseConfig.JDBC_DRIVER);
//
//            // Step 1: Establish connection to MySQL server without specifying database
//            try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL_NO_DB, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
//                 Statement stmt = conn.createStatement()) {
//
//                // Step 2: Create the database if it does not exist
//                String createDbQuery = "CREATE DATABASE IF NOT EXISTS sunDevilBooks";
//                stmt.executeUpdate(createDbQuery);
//                System.out.println("Database created or already exists.");
//
//                // Step 3: Use the sunDevilBooks database
//                String useDbQuery = "USE sunDevilBooks";
//                stmt.executeUpdate(useDbQuery);
//                System.out.println("Switched to sunDevilBooks database.");
//
//                // Step 4: Verify if the SQL file path is correct and exists
//                String sqlFilePath = "C:\\Users\\divin\\eclipse-workspace\\SunDevil_Books\\src\\main\\java\\sunDevil_Books\\sunDevilBooks.sql";
//
//                if (!Files.exists(Paths.get(sqlFilePath))) {
//                    showAlert(Alert.AlertType.ERROR, "SQL file not found at the specified path: " + sqlFilePath);
//                    return;
//                }
//
//                // Step 5: Read the SQL file
//                String sqlContent = new String(Files.readAllBytes(Paths.get(sqlFilePath)));
//
//                // Step 6: Split SQL statements by semicolon (ensure to handle semicolons inside strings)
//                String[] sqlStatements = sqlContent.split("(?<=;\\s)");
//
//                // Step 7: Execute each SQL statement
//                for (String statement : sqlStatements) {
//                    statement = statement.trim();
//                    if (!statement.isEmpty() && !statement.startsWith("--")) { // Skip empty or commented lines
//                        stmt.execute(statement);
//                    }
//                }
//
//                System.out.println("Database initialized successfully using sunDevilBooks.sql.");
//            }
//        } catch (ClassNotFoundException e) {
//            showAlert(Alert.AlertType.ERROR, "JDBC Driver not found: " + e.getMessage());
//        } catch (SQLException e) {
//            showAlert(Alert.AlertType.ERROR, "Database initialization error: " + e.getMessage());
//        } catch (IOException e) {
//            showAlert(Alert.AlertType.ERROR, "Error reading SQL file: " + e.getMessage());
//        }
//    }



    // Function to add a user
    private void addUser(String role, String userId, String username, String password) {
        String query = "INSERT INTO users (user_id, username, role, password) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, userId);
            statement.setString(2, username);
            statement.setString(3, role);
            statement.setString(4, password);
            statement.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "User added successfully!");
        } catch (SQLIntegrityConstraintViolationException e) {
            showAlert(Alert.AlertType.ERROR, "User ID already exists.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error adding user: " + e.getMessage());
        }
    }

    // Function to edit a user
    private void editUser(String userId, String newRole, String newUsername) {
        String query = "UPDATE users SET ";
        boolean hasUpdates = false;

        if (newUsername != null && !newUsername.isEmpty()) {
            query += "username = ? ";
            hasUpdates = true;
        }

        if (newRole != null) {
            if (hasUpdates) query += ", ";
            query += "role = ? ";
            hasUpdates = true;
        }

        query += "WHERE user_id = ?";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement statement = conn.prepareStatement(query)) {

            int paramIndex = 1;

            if (newUsername != null && !newUsername.isEmpty()) {
                statement.setString(paramIndex++, newUsername);
            }

            if (newRole != null) {
                statement.setString(paramIndex++, newRole);
            }

            statement.setString(paramIndex, userId);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                showAlert(Alert.AlertType.INFORMATION, "User updated successfully!");
            } else {
                showAlert(Alert.AlertType.WARNING, "User ID not found.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error editing user: " + e.getMessage());
        }
    }

    // Function to remove a user
    private void removeUser(String userId) {
        String query = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, userId);
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                showAlert(Alert.AlertType.INFORMATION, "User removed successfully!");
            } else {
                showAlert(Alert.AlertType.WARNING, "User ID not found.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error removing user: " + e.getMessage());
        }
    }

    // Function to add a book
    private void addBook(String category, String name, String author, int publishingYear, double price, String condition) {
        String query = "INSERT INTO books (category, name, author, publishing_year, price, book_condition) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, category);
            statement.setString(2, name);
            statement.setString(3, author);
            statement.setInt(4, publishingYear);
            statement.setDouble(5, price);
            statement.setString(6, condition);
            statement.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Book added successfully!");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error adding book: " + e.getMessage());
        }
    }

    // Function to edit a book
    private void editBook(int bookId, String category, String name, String author, Integer publishingYear, Double price, String condition) {
        StringBuilder queryBuilder = new StringBuilder("UPDATE books SET ");
        boolean first = true;

        if (category != null) {
            queryBuilder.append("category = ?");
            first = false;
        }
        if (name != null && !name.isEmpty()) {
            if (!first) queryBuilder.append(", ");
            queryBuilder.append("name = ?");
            first = false;
        }
        if (author != null && !author.isEmpty()) {
            if (!first) queryBuilder.append(", ");
            queryBuilder.append("author = ?");
            first = false;
        }
        if (publishingYear != null) {
            if (!first) queryBuilder.append(", ");
            queryBuilder.append("publishing_year = ?");
            first = false;
        }
        if (price != null) {
            if (!first) queryBuilder.append(", ");
            queryBuilder.append("price = ?");
            first = false;
        }
        if (condition != null) {
            if (!first) queryBuilder.append(", ");
            queryBuilder.append("book_condition = ?");
            first = false;
        }

        queryBuilder.append(" WHERE book_id = ?");

        String query = queryBuilder.toString();

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement statement = conn.prepareStatement(query)) {

            int paramIndex = 1;

            if (category != null) {
                statement.setString(paramIndex++, category);
            }
            if (name != null && !name.isEmpty()) {
                statement.setString(paramIndex++, name);
            }
            if (author != null && !author.isEmpty()) {
                statement.setString(paramIndex++, author);
            }
            if (publishingYear != null) {
                statement.setInt(paramIndex++, publishingYear);
            }
            if (price != null) {
                statement.setDouble(paramIndex++, price);
            }
            if (condition != null) {
                statement.setString(paramIndex++, condition);
            }

            statement.setInt(paramIndex, bookId);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Book updated successfully!");
            } else {
                showAlert(Alert.AlertType.WARNING, "Book ID not found.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error editing book: " + e.getMessage());
        }
    }

    // Function to remove a book
    private void removeBook(int bookId) {
        String query = "DELETE FROM books WHERE book_id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setInt(1, bookId);
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Book removed successfully!");
            } else {
                showAlert(Alert.AlertType.WARNING, "Book ID not found.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error removing book: " + e.getMessage());
        }
    }

    // Function to generate reports
    private void generateReport(String category) {
        if (category == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a report category.");
            return;
        }

        // Open FileChooser to select the folder and file name
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Report");
        // Set extension filter
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );
        // Suggest a default file name
        fileChooser.setInitialFileName(category + "_Report.xlsx");
        // Show save file dialog
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            // Generate report and save to the selected file
            try {
                generateExcelReport(file, category);
                showAlert(Alert.AlertType.INFORMATION, "Report for " + category + " generated successfully!");
            } catch (IOException | SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error generating report: " + e.getMessage());
            }
        }
    }

    // Function to generate Excel report based on category
    private void generateExcelReport(File file, String category) throws IOException, SQLException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(category);

        // Based on category, fetch data and write to sheet
        switch (category) {
            case "Books and Sales":
                generateBooksAndSalesReport(sheet);
                break;
            case "Users":
                generateUsersReport(sheet);
                break;
            case "Transactions":
                generateTransactionsReport(sheet);
                break;
            case "Inventory":
                generateInventoryReport(sheet);
                break;
            case "Revenue":
                generateRevenueReport(sheet);
                break;
            default:
                throw new IllegalArgumentException("Unknown report category: " + category);
        }

        // Auto-size columns for better readability
        for (int i = 0; i < sheet.getRow(0).getPhysicalNumberOfCells(); i++) {
            sheet.autoSizeColumn(i);
        }

        // Write the output to the file
        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            workbook.write(fileOut);
        } finally {
            workbook.close();
        }
    }

    // Generates Books and Sales Report
    private void generateBooksAndSalesReport(Sheet sheet) throws SQLException {
        String query = "SELECT b.book_id, b.category, b.name, b.author, b.publishing_year, b.price, b.book_condition, " +
                       "t.transaction_id, t.sale_price, t.buyer_id, t.seller_id " +
                       "FROM books b LEFT JOIN transactions t ON b.book_id = t.book_id";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Book ID");
            headerRow.createCell(1).setCellValue("Category");
            headerRow.createCell(2).setCellValue("Name");
            headerRow.createCell(3).setCellValue("Author");
            headerRow.createCell(4).setCellValue("Publishing Year");
            headerRow.createCell(5).setCellValue("Price");
            headerRow.createCell(6).setCellValue("Condition");
            headerRow.createCell(7).setCellValue("Transaction ID");
            headerRow.createCell(8).setCellValue("Sale Price");
            headerRow.createCell(9).setCellValue("Buyer ID");
            headerRow.createCell(10).setCellValue("Seller ID");

            int rowNum = 1;
            while (rs.next()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(rs.getInt("book_id"));
                row.createCell(1).setCellValue(rs.getString("category"));
                row.createCell(2).setCellValue(rs.getString("name"));
                row.createCell(3).setCellValue(rs.getString("author"));
                row.createCell(4).setCellValue(rs.getInt("publishing_year"));
                row.createCell(5).setCellValue(rs.getDouble("price"));
                row.createCell(6).setCellValue(rs.getString("book_condition"));
                row.createCell(7).setCellValue(rs.getInt("transaction_id"));
                row.createCell(8).setCellValue(rs.getDouble("sale_price"));
                row.createCell(9).setCellValue(rs.getString("buyer_id"));
                row.createCell(10).setCellValue(rs.getString("seller_id"));
            }
        }
    }

    // Generates Users Report
    private void generateUsersReport(Sheet sheet) throws SQLException {
        String query = "SELECT user_id, username, role FROM users";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("User ID");
            headerRow.createCell(1).setCellValue("Username");
            headerRow.createCell(2).setCellValue("Role");

            int rowNum = 1;
            while (rs.next()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(rs.getString("user_id"));
                row.createCell(1).setCellValue(rs.getString("username"));
                row.createCell(2).setCellValue(rs.getString("role"));
            }
        }
    }

    // Generates Transactions Report
    private void generateTransactionsReport(Sheet sheet) throws SQLException {
        String query = "SELECT transaction_id, book_id, sale_price, buyer_id, seller_id, transaction_date FROM transactions";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Transaction ID");
            headerRow.createCell(1).setCellValue("Book ID");
            headerRow.createCell(2).setCellValue("Sale Price");
            headerRow.createCell(3).setCellValue("Buyer ID");
            headerRow.createCell(4).setCellValue("Seller ID");
            headerRow.createCell(5).setCellValue("Transaction Date");

            int rowNum = 1;
            while (rs.next()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(rs.getInt("transaction_id"));
                row.createCell(1).setCellValue(rs.getInt("book_id"));
                row.createCell(2).setCellValue(rs.getDouble("sale_price"));
                row.createCell(3).setCellValue(rs.getString("buyer_id"));
                row.createCell(4).setCellValue(rs.getString("seller_id"));
                row.createCell(5).setCellValue(rs.getDate("transaction_date").toString());
            }
        }
    }

    // Generates Inventory Report
    private void generateInventoryReport(Sheet sheet) throws SQLException {
        String query = "SELECT book_id, category, name, author, publishing_year, price, book_condition FROM books";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Book ID");
            headerRow.createCell(1).setCellValue("Category");
            headerRow.createCell(2).setCellValue("Name");
            headerRow.createCell(3).setCellValue("Author");
            headerRow.createCell(4).setCellValue("Publishing Year");
            headerRow.createCell(5).setCellValue("Price");
            headerRow.createCell(6).setCellValue("Condition");

            int rowNum = 1;
            while (rs.next()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(rs.getInt("book_id"));
                row.createCell(1).setCellValue(rs.getString("category"));
                row.createCell(2).setCellValue(rs.getString("name"));
                row.createCell(3).setCellValue(rs.getString("author"));
                row.createCell(4).setCellValue(rs.getInt("publishing_year"));
                row.createCell(5).setCellValue(rs.getDouble("price"));
                row.createCell(6).setCellValue(rs.getString("book_condition"));
            }
        }
    }

    // Generates Revenue Report
    private void generateRevenueReport(Sheet sheet) throws SQLException {
        String query = "SELECT transaction_id, sale_price, transaction_date FROM transactions";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Transaction ID");
            headerRow.createCell(1).setCellValue("Sale Price");
            headerRow.createCell(2).setCellValue("Transaction Date");

            double totalRevenue = 0;
            int rowNum = 1;
            while (rs.next()) {
                double salePrice = rs.getDouble("sale_price");
                totalRevenue += salePrice;

                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(rs.getInt("transaction_id"));
                row.createCell(1).setCellValue(salePrice);
                row.createCell(2).setCellValue(rs.getDate("transaction_date").toString());
            }

            // Add total revenue at the end
            Row totalRow = sheet.createRow(rowNum + 1);
            totalRow.createCell(0).setCellValue("Total Revenue:");
            totalRow.createCell(1).setCellValue(totalRevenue);
        }
    }

    // Function to fetch transactions from the database
    private void fetchTransactions(ListView<String> transactionsList) {
        String query = "SELECT t.transaction_id, b.name, b.publishing_year, b.book_condition, t.sale_price, t.buyer_id, t.seller_id " +
                "FROM transactions t " +
                "JOIN books b ON t.book_id = b.book_id";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            transactionsList.getItems().clear();
            while (rs.next()) {
                String bookName = rs.getString("name");
                int publishingYear = rs.getInt("publishing_year");
                String bookCondition = rs.getString("book_condition");
                double salePrice = rs.getDouble("sale_price");
                String buyerId = rs.getString("buyer_id");
                String sellerId = rs.getString("seller_id");

                String transaction = bookName + " (" + publishingYear + ") (" + bookCondition + ") - $" + salePrice +
                        ", Buyer: " + buyerId + ", Seller: " + sellerId;
                transactionsList.getItems().add(transaction);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error fetching transactions: " + e.getMessage());
        }
    }

    // Function to fetch book IDs from the database
    private void fetchBookIds(ListView<String> bookIdsList) {
        String query = "SELECT book_id FROM books";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            bookIdsList.getItems().clear();
            while (rs.next()) {
                int bookId = rs.getInt("book_id");
                bookIdsList.getItems().add("Book ID: " + bookId);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error fetching book IDs: " + e.getMessage());
        }
    }

    // Utility function to show alerts
    private void showAlert(Alert.AlertType type, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type, message, ButtonType.OK);
            alert.showAndWait();
        });
    }

//    public static void main(String[] args) {
//        launch(args);
//    }
}
