package sunDevil_Books;

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
import java.sql.*;

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

        DatabaseOperations.initializeDatabase();

        HBox topBar = createTopBar(primaryStage);
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-border-color: gold; -fx-border-width: 0 0 3 0; -fx-background-color: #F8F8F8;");

        TabPane tabPane = new TabPane();
        Tab manageBooksTab = new Tab("Manage Books");
        Tab manageUsersTab = new Tab("Manage Users");
        Tab reportsTab = new Tab("Reports");

        HBox booksPane = createManageBooksPane();
        manageBooksTab.setContent(booksPane);
        manageBooksTab.setClosable(false);

        HBox usersPane = createManageUsersPane();
        manageUsersTab.setContent(usersPane);
        manageUsersTab.setClosable(false);

        VBox reportsBox = createReportsPane();
        reportsTab.setContent(reportsBox);
        reportsTab.setClosable(false);

        tabPane.getTabs().addAll(manageBooksTab, manageUsersTab, reportsTab);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(topBar);
        borderPane.setCenter(tabPane);
        borderPane.setPadding(new Insets(20));

        Scene scene = new Scene(borderPane, 1400, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox createTopBar(Stage stage) {
        Image logoImage;
        InputStream logoStream = getClass().getResourceAsStream("sundevilbooks.png");
        if (logoStream != null) {
            logoImage = new Image(logoStream);
        } else {
            logoImage = null;
            Utils.showAlert(Alert.AlertType.ERROR, "Default logo image not found.");
        }

        logoImageView = new ImageView(logoImage);
        logoImageView.setFitWidth(200);
        logoImageView.setFitHeight(100);

        Button uploadLogoButton = Utils.createStyledButton("Change Logo");
        uploadLogoButton.setOnAction(e -> uploadLogo(stage));

        VBox logoBox = new VBox(10, logoImageView, uploadLogoButton);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.setPadding(new Insets(10));

        Label userLabel = new Label("User: " + firstName + " " + lastName + " (" + username + " - " + userId + ")");
        Label roleLabel = new Label("Role: Admin");

        VBox userInfoBox = new VBox(5, userLabel, roleLabel);
        userInfoBox.setAlignment(Pos.CENTER_RIGHT);
        userInfoBox.setPadding(new Insets(10));

        Button logoutButton = Utils.createStyledButton("Log Out");
        logoutButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Logged out successfully!", ButtonType.OK);
            alert.showAndWait();

            SplashScreenView splashScreenView = new SplashScreenView();
            splashScreenView.start(stage);
        });

        VBox logoutBox = new VBox(logoutButton);
        logoutBox.setAlignment(Pos.CENTER_RIGHT);
        logoutBox.setPadding(new Insets(10));

        HBox topBar = new HBox(20, logoBox, userInfoBox, logoutBox);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(userInfoBox, Priority.ALWAYS);

        return topBar;
    }

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

    private HBox createManageBooksPane() {
        VBox addBooksPane = new VBox(10);
        addBooksPane.setAlignment(Pos.CENTER);
        addBooksPane.setPadding(new Insets(10));

        Label addBookTitle = Utils.createStyledLabel("Add Books");

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

        Button addBookButton = Utils.createStyledButton("Add Book");

        addBookButton.setOnAction(e -> {
            String category = bookCategoryComboBox.getValue();
            String name = bookNameField.getText().trim();
            String author = bookAuthorField.getText().trim();
            String yearStr = yearField.getText().trim();
            String priceStr = priceField.getText().trim();
            String condition = conditionComboBox.getValue();

            if (category == null || name.isEmpty() || author.isEmpty() ||
                    yearStr.isEmpty() || priceStr.isEmpty() || condition == null) {
                Utils.showAlert(Alert.AlertType.WARNING, "Please fill in all fields to add a book.");
                return;
            }

            int year;
            double price;
            try {
                year = Integer.parseInt(yearStr);
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException ex) {
                Utils.showAlert(Alert.AlertType.ERROR, "Invalid year or price format.");
                return;
            }

            addBook(category, name, author, year, price, condition);

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

    private VBox createEditBooksPane() {
        VBox editBooksPane = new VBox(10);
        editBooksPane.setAlignment(Pos.CENTER);
        editBooksPane.setPadding(new Insets(10));

        Label editBookTitle = Utils.createStyledLabel("Edit Books");

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

        Button editBookButton = Utils.createStyledButton("Edit Book");

        editBookButton.setOnAction(e -> {
            String bookIdStr = bookIdField.getText().trim();
            String category = editBookCategoryComboBox.getValue();
            String name = editBookNameField.getText().trim();
            String author = editBookAuthorField.getText().trim();
            String yearStr = editYearField.getText().trim();
            String priceStr = editPriceField.getText().trim();
            String condition = editConditionComboBox.getValue();

            if (bookIdStr.isEmpty()) {
                Utils.showAlert(Alert.AlertType.WARNING, "Please enter Book ID to edit.");
                return;
            }

            int bookId;
            try {
                bookId = Integer.parseInt(bookIdStr);
            } catch (NumberFormatException ex) {
                Utils.showAlert(Alert.AlertType.ERROR, "Invalid Book ID format.");
                return;
            }

            if (category == null && name.isEmpty() && author.isEmpty() &&
                    yearStr.isEmpty() && priceStr.isEmpty() && condition == null) {
                Utils.showAlert(Alert.AlertType.WARNING, "Please fill in at least one field to update.");
                return;
            }

            Integer year = null;
            if (!yearStr.isEmpty()) {
                try {
                    year = Integer.parseInt(yearStr);
                } catch (NumberFormatException ex) {
                    Utils.showAlert(Alert.AlertType.ERROR, "Invalid year format.");
                    return;
                }
            }

            Double price = null;
            if (!priceStr.isEmpty()) {
                try {
                    price = Double.parseDouble(priceStr);
                } catch (NumberFormatException ex) {
                    Utils.showAlert(Alert.AlertType.ERROR, "Invalid price format.");
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

    private VBox createRemoveBooksPane() {
        VBox removeBooksPane = new VBox(10);
        removeBooksPane.setAlignment(Pos.CENTER);
        removeBooksPane.setPadding(new Insets(10));

        Label removeBookTitle = Utils.createStyledLabel("Remove Books");

        TextField removeBookIdField = new TextField();
        removeBookIdField.setPromptText("Enter Book ID");

        Button removeBookButton = Utils.createStyledButton("Remove Book");

        removeBookButton.setOnAction(e -> {
            String bookIdStr = removeBookIdField.getText().trim();
            if (bookIdStr.isEmpty()) {
                Utils.showAlert(Alert.AlertType.WARNING, "Please enter Book ID to remove.");
                return;
            }

            int bookId;
            try {
                bookId = Integer.parseInt(bookIdStr);
            } catch (NumberFormatException ex) {
                Utils.showAlert(Alert.AlertType.ERROR, "Invalid Book ID format.");
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

    private HBox createManageUsersPane() {
        VBox addUserPane = new VBox(10);
        addUserPane.setAlignment(Pos.CENTER);
        addUserPane.setPadding(new Insets(10));

        Label addUserTitle = Utils.createStyledLabel("Add Users");

        ComboBox<String> userRoleComboBox = new ComboBox<>();
        userRoleComboBox.getItems().addAll("Buyer", "Seller", "Admin");
        userRoleComboBox.setPromptText("Select Role");

        TextField userIdField = new TextField();
        userIdField.setPromptText("Enter User ID");

        TextField firstNameField = new TextField();
        firstNameField.setPromptText("Enter First Name");

        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Enter Last Name");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");

        Button addUserButton = Utils.createStyledButton("Add User");

        addUserButton.setOnAction(e -> {
            String role = userRoleComboBox.getValue();
            String userId = userIdField.getText().trim();
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (role == null || userId.isEmpty() || firstName.isEmpty() || lastName.isEmpty() ||
                    username.isEmpty() || password.isEmpty()) {
                Utils.showAlert(Alert.AlertType.WARNING, "Please fill in all fields to add a user.");
                return;
            }

            DatabaseOperations.addUser(role, userId, firstName, lastName, username, password);
            userRoleComboBox.setValue(null);
            userIdField.clear();
            firstNameField.clear();
            lastNameField.clear();
            usernameField.clear();
            passwordField.clear();
        });

        addUserPane.getChildren().addAll(
                addUserTitle,
                new Label("User Role:"), userRoleComboBox,
                new Label("User ID:"), userIdField,
                new Label("First Name:"), firstNameField,
                new Label("Last Name:"), lastNameField,
                new Label("Username:"), usernameField,
                new Label("Password:"), passwordField,
                addUserButton
        );

        VBox editUserPane = createEditUserPane();
        VBox removeUserPane = createRemoveUserPane();
        VBox roleChangeRequestsPane = createRoleChangeRequestsPane();

        HBox usersPane = new HBox(30, addUserPane, editUserPane, removeUserPane, roleChangeRequestsPane);
        usersPane.setPadding(new Insets(20));

        return usersPane;
    }

    private VBox createEditUserPane() {
        VBox editUserPane = new VBox(10);
        editUserPane.setAlignment(Pos.CENTER);
        editUserPane.setPadding(new Insets(10));

        Label editUserTitle = Utils.createStyledLabel("Edit Users");

        TextField editUserIdField = new TextField();
        editUserIdField.setPromptText("Enter User ID");

        TextField editUsernameField = new TextField();
        editUsernameField.setPromptText("Enter New Username");

        TextField editFirstNameField = new TextField();
        editFirstNameField.setPromptText("Enter New First Name");

        TextField editLastNameField = new TextField();
        editLastNameField.setPromptText("Enter New Last Name");

        ComboBox<String> editUserRoleComboBox = new ComboBox<>();
        editUserRoleComboBox.getItems().addAll("Buyer", "Seller", "Admin", "BuyerSeller");
        editUserRoleComboBox.setPromptText("Select New Role");

        Button editUserButton = Utils.createStyledButton("Edit User");

        editUserButton.setOnAction(e -> {
            String userId = editUserIdField.getText().trim();
            String newUsername = editUsernameField.getText().trim();
            String newFirstName = editFirstNameField.getText().trim();
            String newLastName = editLastNameField.getText().trim();
            String newRole = editUserRoleComboBox.getValue();

            if (userId.isEmpty() || (newUsername.isEmpty() && newFirstName.isEmpty() &&
                    newLastName.isEmpty() && newRole == null)) {
                Utils.showAlert(Alert.AlertType.WARNING, "Please enter User ID and at least one field to update.");
                return;
            }

            editUser(userId, newUsername, newFirstName, newLastName, newRole);
            editUserIdField.clear();
            editUsernameField.clear();
            editFirstNameField.clear();
            editLastNameField.clear();
            editUserRoleComboBox.setValue(null);
        });

        editUserPane.getChildren().addAll(
                editUserTitle,
                new Label("User ID:"), editUserIdField,
                new Label("Update Username:"), editUsernameField,
                new Label("Update First Name:"), editFirstNameField,
                new Label("Update Last Name:"), editLastNameField,
                new Label("Update Role:"), editUserRoleComboBox,
                editUserButton
        );

        return editUserPane;
    }

    private VBox createRemoveUserPane() {
        VBox removeUserPane = new VBox(10);
        removeUserPane.setAlignment(Pos.CENTER);
        removeUserPane.setPadding(new Insets(10));

        Label removeUserTitle = Utils.createStyledLabel("Remove Users");

        TextField removeUserIdField = new TextField();
        removeUserIdField.setPromptText("Enter User ID");

        Button removeUserButton = Utils.createStyledButton("Remove User");

        removeUserButton.setOnAction(e -> {
            String userId = removeUserIdField.getText().trim();
            if (userId.isEmpty()) {
                Utils.showAlert(Alert.AlertType.WARNING, "Please enter User ID to remove.");
                return;
            }

            DatabaseOperations.removeUser(userId);
            removeUserIdField.clear();
        });

        removeUserPane.getChildren().addAll(
                removeUserTitle,
                new Label("User ID:"), removeUserIdField,
                removeUserButton
        );

        return removeUserPane;
    }

    private VBox createRoleChangeRequestsPane() {
        VBox roleChangePane = new VBox(10);
        roleChangePane.setAlignment(Pos.CENTER);
        roleChangePane.setPadding(new Insets(10));

        Label roleChangeTitle = Utils.createStyledLabel("Role Change Requests");

        ListView<String> requestsListView = new ListView<>();
        fetchRoleChangeRequests(requestsListView);

        Button approveButton = new Button("Approve");
        approveButton.setStyle("-fx-background-color: #008000; -fx-text-fill: white;");
        Button rejectButton = new Button("Reject");
        rejectButton.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white;");

        approveButton.setOnAction(e -> {
            String selectedRequest = requestsListView.getSelectionModel().getSelectedItem();
            if (selectedRequest == null) {
                Utils.showAlert(Alert.AlertType.WARNING, "Please select a request to approve.");
                return;
            }
            int requestId = Integer.parseInt(selectedRequest.split(" - ")[0]);
            handleRoleChangeRequest(requestId, "Approved");
            fetchRoleChangeRequests(requestsListView);
        });

        rejectButton.setOnAction(e -> {
            String selectedRequest = requestsListView.getSelectionModel().getSelectedItem();
            if (selectedRequest == null) {
                Utils.showAlert(Alert.AlertType.WARNING, "Please select a request to reject.");
                return;
            }
            int requestId = Integer.parseInt(selectedRequest.split(" - ")[0]);
            handleRoleChangeRequest(requestId, "Rejected");
            fetchRoleChangeRequests(requestsListView);
        });

        HBox buttonsBox = new HBox(10, approveButton, rejectButton);
        buttonsBox.setAlignment(Pos.CENTER);

        roleChangePane.getChildren().addAll(roleChangeTitle, requestsListView, buttonsBox);

        return roleChangePane;
    }

    private void fetchRoleChangeRequests(ListView<String> requestsListView) {
        String query = "SELECT r.request_id, u.user_id, u.first_name, u.last_name, r.requested_role, r.status, r.request_date " +
                "FROM role_change_requests r JOIN users u ON r.user_id = u.user_id WHERE r.status = 'Pending'";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            requestsListView.getItems().clear();
            while (rs.next()) {
                int requestId = rs.getInt("request_id");
                String userId = rs.getString("user_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String requestedRole = rs.getString("requested_role");
                String requestDate = rs.getTimestamp("request_date").toString();

                String request = requestId + " - " + userId + " (" + firstName + " " + lastName + ") requests role: " + requestedRole + " on " + requestDate;
                requestsListView.getItems().add(request);
            }
        } catch (SQLException e) {
            Utils.showAlert(Alert.AlertType.ERROR, "Error fetching role change requests: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleRoleChangeRequest(int requestId, String decision) {
        String updateRequestQuery = "UPDATE role_change_requests SET status = ? WHERE request_id = ?";
        String updateUserRoleQuery = "UPDATE users SET role = ? WHERE user_id = (SELECT user_id FROM role_change_requests WHERE request_id = ?)";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement updateRequestStmt = conn.prepareStatement(updateRequestQuery);
             PreparedStatement updateUserRoleStmt = conn.prepareStatement(updateUserRoleQuery)) {

            conn.setAutoCommit(false);

            updateRequestStmt.setString(1, decision);
            updateRequestStmt.setInt(2, requestId);
            updateRequestStmt.executeUpdate();

            if (decision.equals("Approved")) {
                String fetchRequestQuery = "SELECT user_id, requested_role FROM role_change_requests WHERE request_id = ?";
                try (PreparedStatement fetchRequestStmt = conn.prepareStatement(fetchRequestQuery)) {
                    fetchRequestStmt.setInt(1, requestId);
                    ResultSet rs = fetchRequestStmt.executeQuery();
                    if (rs.next()) {
                        String userId = rs.getString("user_id");
                        String requestedRole = rs.getString("requested_role");

                        String currentRole = getCurrentUserRole(userId, conn);
                        String newRole = combineRoles(currentRole, requestedRole);
                        updateUserRoleStmt.setString(1, newRole);
                        updateUserRoleStmt.setInt(2, requestId);
                        updateUserRoleStmt.executeUpdate();
                    }
                }
            }

            conn.commit();
            Utils.showAlert(Alert.AlertType.INFORMATION, "Request " + decision.toLowerCase() + " successfully!");

        } catch (SQLException e) {
            Utils.showAlert(Alert.AlertType.ERROR, "Error processing request: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getCurrentUserRole(String userId, Connection conn) throws SQLException {
        String query = "SELECT role FROM users WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("role");
            }
        }
        return "Buyer";
    }

    private String combineRoles(String currentRole, String requestedRole) {
        if (currentRole.equals(requestedRole) || currentRole.equals("Buyer") || currentRole.equals("Seller") || currentRole.equals("Admin")) {
            return currentRole;
        }
        if ((currentRole.equals("Buyer") && requestedRole.equals("Seller")) ||
            (currentRole.equals("Seller") && requestedRole.equals("Buyer"))) {
            return "BuyerSeller";
        }
        return requestedRole;
    }

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

        Button generateReportButton = Utils.createStyledButton("Generate Report");
        generateReportButton.setOnAction(e -> generateReport(reportCategoryComboBox.getValue()));

        VBox reportsBox = new VBox(15, transactionsLabel, transactionBookIdsBox, reportCategoryLabel, reportCategoryComboBox, generateReportButton);
        reportsBox.setAlignment(Pos.CENTER);
        reportsBox.setPadding(new Insets(20));

        return reportsBox;
    }


    private void editUser(String userId, String newUsername, String newFirstName, String newLastName, String newRole) {
        StringBuilder queryBuilder = new StringBuilder("UPDATE users SET ");
        boolean first = true;

        if (newUsername != null && !newUsername.isEmpty()) {
            queryBuilder.append("username = ?");
            first = false;
        }
        if (newFirstName != null && !newFirstName.isEmpty()) {
            if (!first) queryBuilder.append(", ");
            queryBuilder.append("first_name = ?");
            first = false;
        }
        if (newLastName != null && !newLastName.isEmpty()) {
            if (!first) queryBuilder.append(", ");
            queryBuilder.append("last_name = ?");
            first = false;
        }
        if (newRole != null) {
            if (!first) queryBuilder.append(", ");
            queryBuilder.append("role = ?");
            first = false;
        }

        queryBuilder.append(" WHERE user_id = ?");

        String query = queryBuilder.toString();

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement statement = conn.prepareStatement(query)) {

            int paramIndex = 1;

            if (newUsername != null && !newUsername.isEmpty()) {
                statement.setString(paramIndex++, newUsername);
            }
            if (newFirstName != null && !newFirstName.isEmpty()) {
                statement.setString(paramIndex++, newFirstName);
            }
            if (newLastName != null && !newLastName.isEmpty()) {
                statement.setString(paramIndex++, newLastName);
            }
            if (newRole != null) {
                statement.setString(paramIndex++, newRole);
            }

            statement.setString(paramIndex, userId);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                Utils.showAlert(Alert.AlertType.INFORMATION, "User updated successfully!");
            } else {
                Utils.showAlert(Alert.AlertType.WARNING, "User ID not found.");
            }
        } catch (SQLException e) {
            Utils.showAlert(Alert.AlertType.ERROR, "Error editing user: " + e.getMessage());
        }
    }

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

            Utils.showAlert(Alert.AlertType.INFORMATION, "Book added successfully!");
        } catch (SQLException e) {
            Utils.showAlert(Alert.AlertType.ERROR, "Error adding book: " + e.getMessage());
        }
    }

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
                Utils.showAlert(Alert.AlertType.INFORMATION, "Book updated successfully!");
            } else {
                Utils.showAlert(Alert.AlertType.WARNING, "Book ID not found.");
            }
        } catch (SQLException e) {
            Utils.showAlert(Alert.AlertType.ERROR, "Error editing book: " + e.getMessage());
        }
    }

    private void removeBook(int bookId) {
        String query = "DELETE FROM books WHERE book_id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setInt(1, bookId);
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                Utils.showAlert(Alert.AlertType.INFORMATION, "Book removed successfully!");
            } else {
                Utils.showAlert(Alert.AlertType.WARNING, "Book ID not found.");
            }
        } catch (SQLException e) {
            Utils.showAlert(Alert.AlertType.ERROR, "Error removing book: " + e.getMessage());
        }
    }

    private void generateReport(String category) {
        if (category == null) {
            Utils.showAlert(Alert.AlertType.WARNING, "Please select a report category.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Report");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );
        fileChooser.setInitialFileName(category + "_Report.xlsx");
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                generateExcelReport(file, category);
                Utils.showAlert(Alert.AlertType.INFORMATION, "Report for " + category + " generated successfully!");
            } catch (IOException | SQLException e) {
                Utils.showAlert(Alert.AlertType.ERROR, "Error generating report: " + e.getMessage());
            }
        }
    }

    private void generateExcelReport(File file, String category) throws IOException, SQLException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(category);

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

        for (int i = 0; i < sheet.getRow(0).getPhysicalNumberOfCells(); i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            workbook.write(fileOut);
        } finally {
            workbook.close();
        }
    }

    private void generateBooksAndSalesReport(Sheet sheet) throws SQLException {
        String query = "SELECT b.book_id, b.category, b.name, b.author, b.publishing_year, b.price, b.book_condition, " +
                "t.transaction_id, t.sale_price, t.buyer_id, t.seller_id " +
                "FROM books b LEFT JOIN transactions t ON b.book_id = t.book_id";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

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

    private void generateUsersReport(Sheet sheet) throws SQLException {
        String query = "SELECT user_id, first_name, last_name, username, role FROM users";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("User ID");
            headerRow.createCell(1).setCellValue("First Name");
            headerRow.createCell(2).setCellValue("Last Name");
            headerRow.createCell(3).setCellValue("Username");
            headerRow.createCell(4).setCellValue("Role");

            int rowNum = 1;
            while (rs.next()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(rs.getString("user_id"));
                row.createCell(1).setCellValue(rs.getString("first_name"));
                row.createCell(2).setCellValue(rs.getString("last_name"));
                row.createCell(3).setCellValue(rs.getString("username"));
                row.createCell(4).setCellValue(rs.getString("role"));
            }
        }
    }

    private void generateTransactionsReport(Sheet sheet) throws SQLException {
        String query = "SELECT transaction_id, book_id, sale_price, buyer_id, seller_id, transaction_date FROM transactions";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

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

    private void generateInventoryReport(Sheet sheet) throws SQLException {
        String query = "SELECT book_id, category, name, author, publishing_year, price, book_condition FROM books";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

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

    private void generateRevenueReport(Sheet sheet) throws SQLException {
        String query = "SELECT transaction_id, sale_price, transaction_date FROM transactions";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

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

            Row totalRow = sheet.createRow(rowNum + 1);
            totalRow.createCell(0).setCellValue("Total Revenue:");
            totalRow.createCell(1).setCellValue(totalRevenue);
        }
    }

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
            Utils.showAlert(Alert.AlertType.ERROR, "Error fetching transactions: " + e.getMessage());
        }
    }

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
            Utils.showAlert(Alert.AlertType.ERROR, "Error fetching book IDs: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        AdminView adminView = new AdminView("AdminFirstName", "AdminLastName", "adminUsername", "ADMIN123");
        adminView.launch(args);
    }
}