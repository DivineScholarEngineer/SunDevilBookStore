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

public class BuyerView extends Application {
    
    private String firstName;
    private String lastName;
    private String username;
    private String userId;

    // do we even need userId?
    public BuyerView(String firstName, String lastName, String username, String userId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.userId = userId;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Buyer View");
        BorderPane mainLayout = new BorderPane();
        
        // apply this styling EVERYWHERE
        mainLayout.setStyle("-fx-background-color: white; -fx-border-color: #FFC627; -fx-border-width: 3px; -fx-border-style: solid;");
        mainLayout.setPadding(new Insets(20));

        HBox topSection = new HBox();
        topSection.setAlignment(Pos.CENTER);

        ImageView logoView = new ImageView();
        Image logo = new Image(getClass().getResourceAsStream("sundevilbooks.png"));
        logoView.setImage(logo);
        logoView.setFitWidth(200);  
        logoView.setFitHeight(100); 
        logoView.setPreserveRatio(true); 

        // User Info
        Button logoutButton = Utils.createStyledButton("Log Out");
        logoutButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Logged out successfully!", ButtonType.OK);
            alert.showAndWait();

            SplashScreenView splashScreenView = new SplashScreenView();
            splashScreenView.start(primaryStage); // Go back to splash screen
        });
        
        VBox userInfoBox = new VBox(5);
        userInfoBox.getChildren().addAll(
            new Label("User: " + username),
            new Label("Role: Buyer"),
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
        
        HBox scienceBox = new HBox(new CheckBox("Science"));
        HBox engineeringBox = new HBox(new CheckBox("Engineering"));
        HBox mathBox = new HBox(new CheckBox("Mathematics"));
        
        scienceBox.setAlignment(Pos.CENTER_LEFT);
        engineeringBox.setAlignment(Pos.CENTER_LEFT);
        mathBox.setAlignment(Pos.CENTER_LEFT);
        
        categoryFilters.getChildren().addAll(scienceBox, engineeringBox, mathBox);
        categoryFilters.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
        categoryFilters.setPrefSize(180, 200); 
        categorySection.getChildren().addAll(categoryLabel, categoryFilters);

        // Condition Filters
        VBox conditionSection = new VBox(10);
        conditionSection.setAlignment(Pos.TOP_CENTER);
        Label conditionLabel = Utils.createStyledLabel("Condition");
        
        conditionLabel.setAlignment(Pos.CENTER);
        
        VBox conditionFilters = new VBox(5);
        conditionFilters.setPadding(new Insets(10));
        conditionFilters.setAlignment(Pos.TOP_LEFT);
        
        HBox newBox = new HBox(new CheckBox("New"));
        HBox usedBox = new HBox(new CheckBox("Used"));
        HBox heavilyUsedBox = new HBox(new CheckBox("Heavily Used"));
        
        newBox.setAlignment(Pos.CENTER_LEFT);
        usedBox.setAlignment(Pos.CENTER_LEFT);
        heavilyUsedBox.setAlignment(Pos.CENTER_LEFT);
        
        conditionFilters.getChildren().addAll(newBox, usedBox, heavilyUsedBox);
        conditionFilters.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
        conditionFilters.setPrefSize(180, 200);
        conditionSection.getChildren().addAll(conditionLabel, conditionFilters);

        filterBoxes.getChildren().addAll(categorySection, conditionSection);
        
        Button applyFiltersButton = Utils.createStyledButton("Apply Filters");

        HBox applyFilterButtonBox = new HBox(applyFiltersButton);
        applyFilterButtonBox.setAlignment(Pos.CENTER);
        filtersSection.getChildren().addAll(filterBoxes, applyFilterButtonBox);

        VBox bookListSection = new VBox(10);
        bookListSection.setAlignment(Pos.TOP_CENTER);
        
        Label bookListLabel = Utils.createStyledLabel("Book List");
        VBox bookList = new VBox(5);
        bookList.setPadding(new Insets(10));
        bookList.setAlignment(Pos.TOP_LEFT);
        
        HBox book1 = new HBox(new CheckBox("Book 1 - Science - $50 - New"));
        HBox book2 = new HBox(new CheckBox("Book 2 - Engineering - $45 - Used"));
        HBox book3 = new HBox(new CheckBox("Book 3 - Mathematics - $60 - New"));
        HBox book4 = new HBox(new CheckBox("Book 4 - Literature - $30 - Used"));
        
        book1.setAlignment(Pos.CENTER_LEFT);
        book2.setAlignment(Pos.CENTER_LEFT);
        book3.setAlignment(Pos.CENTER_LEFT);
        book4.setAlignment(Pos.CENTER_LEFT);
        
        bookList.getChildren().addAll(book1, book2, book3, book4);
        bookList.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
        bookList.setPrefSize(500, 400);

        HBox bottomControls = new HBox(10);
        bottomControls.setAlignment(Pos.CENTER);
        
        TextField searchField = new TextField();
        searchField.setPromptText("Search for a specific book");
        searchField.setPrefWidth(300);
        
        Button createOrderButton = Utils.createStyledButton("Create Order");

        bottomControls.getChildren().addAll(searchField, createOrderButton);
        bookListSection.getChildren().addAll(bookListLabel, bookList, bottomControls);
        mainContent.getChildren().addAll(filtersSection, bookListSection);

        mainLayout.setTop(topSection);
        mainLayout.setCenter(mainContent);

        Scene scene = new Scene(mainLayout, 900, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}