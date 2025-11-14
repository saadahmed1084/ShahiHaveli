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

public class AdminDashboardView {
    private Stage primaryStage;
    private User currentUser;

    public AdminDashboardView(Stage primaryStage, User currentUser) {
        this.primaryStage = primaryStage;
        this.currentUser = currentUser;
    }

    public Scene createAdminDashboard() {
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
        
        Label title = new Label("Shahi Haveli - Admin Dashboard");
        title.setFont(new Font("Georgia", 36));
        title.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold;");
        title.setEffect(new javafx.scene.effect.DropShadow(15, Color.web("#000000")));
        
        Label subtitle = new Label("Manage the Royal Court");
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
        
        Label menuTitle = new Label("Administrative Functions");
        menuTitle.setFont(new Font("Georgia", 22));
        menuTitle.setStyle("-fx-text-fill: #1a1a1a; -fx-font-weight: bold;");
        menuTitle.setPadding(new Insets(0, 0, 20, 0));
        
        // Buttons with icons
        Button manageMenuButton = new Button("📜 Manage Menu Items");
        manageMenuButton.setFont(new Font("Georgia", 16));
        manageMenuButton.setPrefWidth(350);
        manageMenuButton.setPrefHeight(55);
        manageMenuButton.getStyleClass().add("login-button");
        
        Button manageReservationsButton = new Button("📅 Manage Reservations");
        manageReservationsButton.setFont(new Font("Georgia", 16));
        manageReservationsButton.setPrefWidth(350);
        manageReservationsButton.setPrefHeight(55);
        manageReservationsButton.getStyleClass().add("login-button");
        
        Button acceptOrdersButton = new Button("✅ Accept Orders");
        acceptOrdersButton.setFont(new Font("Georgia", 16));
        acceptOrdersButton.setPrefWidth(350);
        acceptOrdersButton.setPrefHeight(55);
        acceptOrdersButton.getStyleClass().add("login-button");
        
        Button viewOrdersButton = new Button("📋 View All Orders");
        viewOrdersButton.setFont(new Font("Georgia", 16));
        viewOrdersButton.setPrefWidth(350);
        viewOrdersButton.setPrefHeight(55);
        viewOrdersButton.getStyleClass().add("login-button");
        
        Button viewReviewsButton = new Button("⭐ View Reviews");
        viewReviewsButton.setFont(new Font("Georgia", 16));
        viewReviewsButton.setPrefWidth(350);
        viewReviewsButton.setPrefHeight(55);
        viewReviewsButton.getStyleClass().add("login-button");
        
        Button logoutButton = new Button("🚪 Logout");
        logoutButton.setFont(new Font("Georgia", 16));
        logoutButton.setPrefWidth(350);
        logoutButton.setPrefHeight(55);
        logoutButton.getStyleClass().add("register-button");
        
        menuContainer.getChildren().addAll(
            menuTitle,
            manageMenuButton,
            manageReservationsButton,
            acceptOrdersButton,
            viewOrdersButton,
            viewReviewsButton,
            logoutButton
        );
        
        // Add to main container
        mainContainer.getChildren().addAll(headerBox, menuContainer);
        
        // Button actions
        manageMenuButton.setOnAction(e -> {
            ManageMenuView manageMenuView = new ManageMenuView(primaryStage, currentUser);
            primaryStage.setScene(manageMenuView.createManageMenuScene());
        });

        manageReservationsButton.setOnAction(e -> {
            ManageReservationsView manageReservationsView = new ManageReservationsView(primaryStage, currentUser);
            primaryStage.setScene(manageReservationsView.createManageReservationsScene());
        });
        
        acceptOrdersButton.setOnAction(e -> {
            // Show a view to select an order first, or create a list view
            // For now, navigate to ViewOrdersView where admin can select orders
            ViewOrdersView viewOrdersView = new ViewOrdersView(primaryStage, currentUser);
            primaryStage.setScene(viewOrdersView.createViewOrdersScene());
        });
        
        viewOrdersButton.setOnAction(e -> {
            ViewOrdersView viewOrdersView = new ViewOrdersView(primaryStage, currentUser);
            primaryStage.setScene(viewOrdersView.createViewOrdersScene());
        });

        viewReviewsButton.setOnAction(e -> {
            ViewReviewsView viewReviewsView = new ViewReviewsView(primaryStage, currentUser);
            primaryStage.setScene(viewReviewsView.createViewReviewsScene());
        });

        logoutButton.setOnAction(e -> {
            LoginView loginView = new LoginView(primaryStage);
            primaryStage.setScene(loginView.createLoginScene());
        });

        Scene scene = new Scene(mainContainer, 900, 750);
        scene.setFill(javafx.scene.paint.Color.web("#d4af37")); // Gold background
        java.net.URL cssUrl = getClass().getResource("/application/resources/application.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }
        return scene;
    }
}
