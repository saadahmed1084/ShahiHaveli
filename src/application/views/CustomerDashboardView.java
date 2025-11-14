package application.views;

import application.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class CustomerDashboardView {
    private Stage primaryStage;
    private User currentUser;

    public CustomerDashboardView(Stage primaryStage, User currentUser) {
        this.primaryStage = primaryStage;
        this.currentUser = currentUser;
    }

    public Scene createCustomerDashboard() {
        // Main container
        VBox mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(30));
        mainContainer.getStyleClass().add("dashboard-layout");
        
        // Header section with logo
        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(0, 0, 30, 0));
        
        // Logo
        ImageView logoView = null;
        try {
            java.net.URL logoUrl = getClass().getResource("/application/resources/ShahiHaveli_Logo.png");
            if (logoUrl != null) {
                Image logo = new Image(logoUrl.toExternalForm());
                logoView = new ImageView(logo);
                logoView.setFitWidth(120);
                logoView.setFitHeight(120);
                logoView.setPreserveRatio(true);
                logoView.setEffect(new javafx.scene.effect.DropShadow(15, Color.web("#000000")));
                logoView.getStyleClass().add("logo-image");
            }
        } catch (Exception e) {
            System.err.println("Error loading logo: " + e.getMessage());
        }
        
        Label welcomeLabel = new Label("Welcome, " + currentUser.getUsername() + "!");
        welcomeLabel.setFont(new Font("Georgia", 20));
        welcomeLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-style: italic;");
        
        Label title = new Label("Shahi Haveli - Customer Dashboard");
        title.setFont(new Font("Georgia", 36));
        title.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold;");
        title.setEffect(new javafx.scene.effect.DropShadow(15, Color.web("#000000")));
        
        Label subtitle = new Label("Experience Royal Mughal Cuisine");
        subtitle.setFont(new Font("Georgia", 16));
        subtitle.setStyle("-fx-text-fill: #ffffff; -fx-font-style: italic;");
        
        if (logoView != null && logoView.getImage() != null) {
            headerBox.getChildren().addAll(logoView, welcomeLabel, title, subtitle);
        } else {
            headerBox.getChildren().addAll(welcomeLabel, title, subtitle);
        }
        
        // Menu container with royal styling
        VBox menuContainer = new VBox(20);
        menuContainer.setAlignment(Pos.CENTER);
        menuContainer.setPadding(new Insets(40, 50, 40, 50));
        menuContainer.setMaxWidth(600);
        menuContainer.getStyleClass().add("form-layout");
        
        Label menuTitle = new Label("What would you like to do?");
        menuTitle.setFont(new Font("Georgia", 22));
        menuTitle.setStyle("-fx-text-fill: #1a1a1a; -fx-font-weight: bold;");
        menuTitle.setPadding(new Insets(0, 0, 20, 0));
        
        // Buttons with icons
        Button placeOrderButton = new Button("🍽️ Place Order");
        placeOrderButton.setFont(new Font("Georgia", 16));
        placeOrderButton.setPrefWidth(350);
        placeOrderButton.setPrefHeight(55);
        placeOrderButton.getStyleClass().add("login-button");
        
        Button trackOrderButton = new Button("📍 Track Order Status");
        trackOrderButton.setFont(new Font("Georgia", 16));
        trackOrderButton.setPrefWidth(350);
        trackOrderButton.setPrefHeight(55);
        trackOrderButton.getStyleClass().add("login-button");
        
        Button viewOrdersButton = new Button("📋 View My Orders");
        viewOrdersButton.setFont(new Font("Georgia", 16));
        viewOrdersButton.setPrefWidth(350);
        viewOrdersButton.setPrefHeight(55);
        viewOrdersButton.getStyleClass().add("login-button");
        
        Button submitReviewButton = new Button("⭐ Submit Review");
        submitReviewButton.setFont(new Font("Georgia", 16));
        submitReviewButton.setPrefWidth(350);
        submitReviewButton.setPrefHeight(55);
        submitReviewButton.getStyleClass().add("login-button");
        
        Button logoutButton = new Button("🚪 Logout");
        logoutButton.setFont(new Font("Georgia", 16));
        logoutButton.setPrefWidth(350);
        logoutButton.setPrefHeight(55);
        logoutButton.getStyleClass().add("register-button");
        
        menuContainer.getChildren().addAll(
            menuTitle,
            placeOrderButton,
            trackOrderButton,
            viewOrdersButton,
            submitReviewButton,
            logoutButton
        );
        
        // Add to main container
        mainContainer.getChildren().addAll(headerBox, menuContainer);
        
        // Button actions
        placeOrderButton.setOnAction(e -> {
            try {
                PlaceOrderView placeOrderView = new PlaceOrderView(primaryStage, currentUser);
                Scene placeOrderScene = placeOrderView.createPlaceOrderScene();
                primaryStage.setScene(placeOrderScene);
                primaryStage.setTitle("Shahi Haveli - Place Order");
                primaryStage.show();
                primaryStage.toFront();
            } catch (Exception ex) {
                System.err.println("Error opening Place Order view: " + ex.getMessage());
                ex.printStackTrace();
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to open Place Order");
                alert.setContentText("Error: " + ex.getMessage() + "\n\nPlease check console for details.");
                alert.showAndWait();
            }
        });

        trackOrderButton.setOnAction(e -> {
            TrackOrderView trackOrderView = new TrackOrderView(primaryStage, currentUser);
            primaryStage.setScene(trackOrderView.createTrackOrderScene());
        });

        viewOrdersButton.setOnAction(e -> {
            ViewOrdersView viewOrdersView = new ViewOrdersView(primaryStage, currentUser);
            primaryStage.setScene(viewOrdersView.createViewOrdersScene());
        });
        
        submitReviewButton.setOnAction(e -> {
            // Navigate to ViewOrdersView where customer can select a completed order to review
            ViewOrdersView viewOrdersView = new ViewOrdersView(primaryStage, currentUser);
            primaryStage.setScene(viewOrdersView.createViewOrdersScene());
        });

        logoutButton.setOnAction(e -> {
            LoginView loginView = new LoginView(primaryStage);
            primaryStage.setScene(loginView.createLoginScene());
        });

        Scene scene = new Scene(mainContainer, 900, 700);
        scene.setFill(javafx.scene.paint.Color.web("#d4af37")); // Gold background
        java.net.URL cssUrl = getClass().getResource("/application/resources/application.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }
        return scene;
    }
}
