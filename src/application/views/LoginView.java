package application.views;

import application.database.DatabaseConnection;
import application.database.UserDAO;
import application.models.User;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class LoginView {
    private Stage primaryStage;
    private User currentUser;

    public LoginView(Stage primaryStage) {
        this.primaryStage = primaryStage;
        DatabaseConnection.initializeDatabase();
    }

    public Scene createLoginScene() {
        // Main container with Mughal theme
        VBox mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.setPadding(new Insets(40));
        mainContainer.getStyleClass().add("dashboard-layout");
        
        // Decorative header
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setSpacing(15);
        
        // Logo
        ImageView logoView = null;
        try {
            java.net.URL logoUrl = getClass().getResource("/application/resources/ShahiHaveli_Logo.png");
            if (logoUrl != null) {
                Image logo = new Image(logoUrl.toExternalForm());
                logoView = new ImageView(logo);
                logoView.setFitWidth(180);
                logoView.setFitHeight(180);
                logoView.setPreserveRatio(true);
                logoView.setEffect(new javafx.scene.effect.DropShadow(20, Color.web("#000000")));
                logoView.getStyleClass().add("logo-image");
                System.out.println("Logo loaded successfully from: " + logoUrl);
            } else {
                System.err.println("Logo URL is null - checking path");
            }
        } catch (Exception e) {
            System.err.println("Error loading logo: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Title with Mughal styling
        VBox titleBox = new VBox(5);
        Label mainTitle = new Label("Shahi Haveli");
        mainTitle.setFont(new Font("Georgia", 48));
        mainTitle.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold;");
        mainTitle.setEffect(new javafx.scene.effect.DropShadow(20, Color.web("#000000")));
        
        Label subtitle = new Label("Royal Mughal Cuisine");
        subtitle.setFont(new Font("Georgia", 18));
        subtitle.setStyle("-fx-text-fill: #ffffff; -fx-font-style: italic;");
        subtitle.setTextAlignment(TextAlignment.CENTER);
        
        titleBox.getChildren().addAll(mainTitle, subtitle);
        titleBox.setAlignment(Pos.CENTER);
        
        if (logoView != null) {
            headerBox.getChildren().add(logoView);
            System.out.println("Logo added to header");
        } else {
            headerBox.getChildren().add(titleBox);
            System.out.println("Logo not available, using title only");
        }
        
        // Welcome message
        Label welcomeLabel = new Label("Welcome, Esteemed Guest");
        welcomeLabel.setFont(new Font("Georgia", 20));
        welcomeLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-style: italic;");
        welcomeLabel.setPadding(new Insets(10, 0, 20, 0));
        
        // Login form container
        VBox formContainer = new VBox(20);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setPadding(new Insets(40, 50, 40, 50));
        formContainer.setMaxWidth(450);
        formContainer.getStyleClass().add("form-layout");
        
        Label formTitle = new Label("Enter the Royal Court");
        formTitle.setFont(new Font("Georgia", 24));
        formTitle.setStyle("-fx-text-fill: #1a1a1a; -fx-font-weight: bold;");
        formTitle.setPadding(new Insets(0, 0, 20, 0));
        
        // Username field
        VBox usernameBox = new VBox(8);
        Label usernameLabel = new Label("👤 Username:");
        usernameLabel.setFont(new Font("Georgia", 14));
        usernameLabel.setStyle("-fx-text-fill: #1a1a1a; -fx-font-weight: bold;");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.setPrefWidth(350);
        usernameField.setPrefHeight(40);
        usernameBox.getChildren().addAll(usernameLabel, usernameField);
        
        // Password field
        VBox passwordBox = new VBox(8);
        Label passwordLabel = new Label("🔒 Password:");
        passwordLabel.setFont(new Font("Georgia", 14));
        passwordLabel.setStyle("-fx-text-fill: #1a1a1a; -fx-font-weight: bold;");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setPrefWidth(350);
        passwordField.setPrefHeight(40);
        passwordBox.getChildren().addAll(passwordLabel, passwordField);
        
        // Role selection
        VBox roleBox = new VBox(8);
        Label roleLabel = new Label("👑 Your Role:");
        roleLabel.setFont(new Font("Georgia", 14));
        roleLabel.setStyle("-fx-text-fill: #1a1a1a; -fx-font-weight: bold;");
        ComboBox<String> roleDropdown = new ComboBox<>();
        roleDropdown.getItems().addAll("Admin", "Employee", "Customer");
        roleDropdown.setPromptText("Select your role");
        roleDropdown.setPrefWidth(350);
        roleDropdown.setPrefHeight(40);
        roleBox.getChildren().addAll(roleLabel, roleDropdown);
        
        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        
        Button loginButton = new Button("Log in");
        loginButton.setPrefWidth(200);
        loginButton.setPrefHeight(50);
        loginButton.setFont(new Font("Georgia", 16));
        loginButton.getStyleClass().add("login-button");
        
        Button registerButton = new Button("Register");
        registerButton.setPrefWidth(200);
        registerButton.setPrefHeight(50);
        registerButton.setFont(new Font("Georgia", 16));
        registerButton.getStyleClass().add("register-button");
        
        buttonBox.getChildren().addAll(loginButton, registerButton);
        
        // Message label
        Label messageLabel = new Label();
        messageLabel.setFont(new Font("Georgia", 13));
        messageLabel.setPadding(new Insets(10, 0, 0, 0));
        messageLabel.setMaxWidth(400);
        messageLabel.setWrapText(true);
        messageLabel.setTextAlignment(TextAlignment.CENTER);
        messageLabel.getStyleClass().add("message-label");
        
        // Add all to form container
        formContainer.getChildren().addAll(
            formTitle,
            usernameBox,
            passwordBox,
            roleBox,
            buttonBox,
            messageLabel
        );
        
        // Add to main container
        mainContainer.getChildren().addAll(headerBox, welcomeLabel, formContainer);
        
        // Login action
        loginButton.setOnAction(e -> {
            String user = usernameField.getText().trim();
            String pass = passwordField.getText();
            String role = roleDropdown.getValue();
            
            // Clear previous messages
            messageLabel.getStyleClass().removeAll("success", "error");
            
            if (user.isEmpty() || pass.isEmpty() || role == null) {
                messageLabel.setText("⚠ Please fill all fields to enter.");
                messageLabel.getStyleClass().add("error");
                return;
            }
            
            User authenticatedUser = UserDAO.authenticate(user, pass, role);
            if (authenticatedUser != null) {
                currentUser = authenticatedUser;
                messageLabel.setText("✓ Access Granted! Welcome to Shahi Haveli!");
                messageLabel.getStyleClass().add("success");
                
                // Small delay before navigation for better UX
                javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(0.5));
                pause.setOnFinished(ev -> {
                    // Navigate to appropriate dashboard
                    if ("Admin".equals(role)) {
                        AdminDashboardView adminView = new AdminDashboardView(primaryStage, currentUser);
                        primaryStage.setScene(adminView.createAdminDashboard());
                    } else if ("Customer".equals(role)) {
                        CustomerDashboardView customerView = new CustomerDashboardView(primaryStage, currentUser);
                        primaryStage.setScene(customerView.createCustomerDashboard());
                    } else if ("Employee".equals(role)) {
                        EmployeeDashboardView employeeView = new EmployeeDashboardView(primaryStage, currentUser);
                        primaryStage.setScene(employeeView.createEmployeeDashboard());
                    }
                });
                pause.play();
            } else {
                messageLabel.setText("✗ Invalid credentials or role. Please check and try again.");
                messageLabel.getStyleClass().add("error");
            }
        });
        
        // Register action
        registerButton.setOnAction(e -> {
            RegisterView registerView = new RegisterView(primaryStage);
            primaryStage.setScene(registerView.createRegisterScene());
        });
        
     // --- New: Wrap mainContainer in a ScrollPane ---
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(mainContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // Give the ScrollPane a unique ID for CSS to target
        scrollPane.setId("root-scroll-pane");

        // Scene setup
        // Use the scrollPane as the root
        Scene scene = new Scene(scrollPane, 800, 700); 

        // Load your CSS file as normal
        java.net.URL cssUrl = getClass().getResource("/application/resources/application.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
            System.out.println("CSS loaded from: " + cssUrl);
        } else {
            System.err.println("CSS file not found!");
        }

        return scene;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
}
