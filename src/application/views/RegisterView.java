package application.views;

import application.database.UserDAO;
import application.models.User;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class RegisterView {
    private Stage primaryStage;

    public RegisterView(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Scene createRegisterScene() {
        // Main container with Mughal theme
        VBox mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(30));
        mainContainer.getStyleClass().add("dashboard-layout");
        
        // Header
        Label headerTitle = new Label("Join the Royal Court");
        headerTitle.setFont(new Font("Georgia", 36));
        headerTitle.setStyle("-fx-text-fill: linear-gradient(to right, #d4af37, #ffd700, #d4af37); -fx-font-weight: bold;");
        headerTitle.setEffect(new javafx.scene.effect.DropShadow(15, Color.web("#d4af37")));
        headerTitle.setPadding(new Insets(0, 0, 10, 0));
        
        Label subtitle = new Label("Create Your Account");
        subtitle.setFont(new Font("Georgia", 18));
        subtitle.setStyle("-fx-text-fill: #d4af37; -fx-font-style: italic;");
        subtitle.setPadding(new Insets(0, 0, 30, 0));
        
        // Registration form container
        VBox formContainer = new VBox(20);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setPadding(new Insets(40, 50, 40, 50));
        formContainer.setMaxWidth(500);
        formContainer.getStyleClass().add("form-layout");
        
        // Username field
        VBox usernameBox = new VBox(8);
        Label usernameLabel = new Label("👤 Username:");
        usernameLabel.setFont(new Font("Georgia", 14));
        TextField usernameField = new TextField();
        usernameField.setPromptText("Choose a unique username");
        usernameField.setPrefWidth(400);
        usernameField.setPrefHeight(40);
        usernameBox.getChildren().addAll(usernameLabel, usernameField);
        
        // Password field
        VBox passwordBox = new VBox(8);
        Label passwordLabel = new Label("🔒 Password:");
        passwordLabel.setFont(new Font("Georgia", 14));
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter a secure password");
        passwordField.setPrefWidth(400);
        passwordField.setPrefHeight(40);
        passwordBox.getChildren().addAll(passwordLabel, passwordField);
        
        // Email field
        VBox emailBox = new VBox(8);
        Label emailLabel = new Label("📧 Email:");
        emailLabel.setFont(new Font("Georgia", 14));
        TextField emailField = new TextField();
        emailField.setPromptText("your.email@example.com");
        emailField.setPrefWidth(400);
        emailField.setPrefHeight(40);
        emailBox.getChildren().addAll(emailLabel, emailField);
        
        // Phone field
        VBox phoneBox = new VBox(8);
        Label phoneLabel = new Label("📱 Phone:");
        phoneLabel.setFont(new Font("Georgia", 14));
        TextField phoneField = new TextField();
        phoneField.setPromptText("+92 XXX XXXXXXX");
        phoneField.setPrefWidth(400);
        phoneField.setPrefHeight(40);
        phoneBox.getChildren().addAll(phoneLabel, phoneField);
        
        // Role selection
        VBox roleBox = new VBox(8);
        Label roleLabel = new Label("👑 Role:");
        roleLabel.setFont(new Font("Georgia", 14));
        ComboBox<String> roleDropdown = new ComboBox<>();
        roleDropdown.getItems().addAll("Customer", "Employee", "Admin");
        roleDropdown.setValue("Customer");
        roleDropdown.setPromptText("Select your role");
        roleDropdown.setPrefWidth(400);
        roleDropdown.setPrefHeight(40);
        roleBox.getChildren().addAll(roleLabel, roleDropdown);
        
        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        
        Button registerButton = new Button("Register Account");
        registerButton.setPrefWidth(200);
        registerButton.setPrefHeight(50);
        registerButton.setFont(new Font("Georgia", 16));
        registerButton.getStyleClass().add("login-button");
        
        Button backButton = new Button("Back to Login");
        backButton.setPrefWidth(200);
        backButton.setPrefHeight(50);
        backButton.setFont(new Font("Georgia", 16));
        backButton.getStyleClass().add("register-button");
        
        buttonBox.getChildren().addAll(registerButton, backButton);
        
        // Message label
        Label messageLabel = new Label();
        messageLabel.setFont(new Font("Georgia", 13));
        messageLabel.setPadding(new Insets(15, 0, 0, 0));
        messageLabel.setMaxWidth(450);
        messageLabel.setWrapText(true);
        messageLabel.setTextAlignment(TextAlignment.CENTER);
        messageLabel.getStyleClass().add("message-label");
        
        // Add all to form container
        formContainer.getChildren().addAll(
            usernameBox,
            passwordBox,
            emailBox,
            phoneBox,
            roleBox,
            buttonBox,
            messageLabel
        );
        
        // Add to main container
        mainContainer.getChildren().addAll(headerTitle, subtitle, formContainer);
        
        // Register action
        registerButton.setOnAction(e -> {
            String user = usernameField.getText().trim();
            String pass = passwordField.getText();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String role = roleDropdown.getValue();
            
            // Clear previous messages
            messageLabel.getStyleClass().removeAll("success", "error");
            
            // Validation
            if (user.isEmpty() || pass.isEmpty() || email.isEmpty() || phone.isEmpty() || role == null) {
                messageLabel.setText("⚠ Please fill all fields to register.");
                messageLabel.getStyleClass().add("error");
                return;
            }
            
            if (pass.length() < 3) {
                messageLabel.setText("⚠ Password must be at least 3 characters long.");
                messageLabel.getStyleClass().add("error");
                return;
            }
            
            // Create user object
            User newUser = new User();
            newUser.setUsername(user);
            newUser.setPassword(pass);
            newUser.setEmail(email);
            newUser.setPhone(phone);
            newUser.setRole(role);
            
            // Register user
            boolean success = UserDAO.register(newUser);
            if (success) {
                messageLabel.setText("✓ Registration successful! Your account has been created. Redirecting to login...");
                messageLabel.getStyleClass().add("success");
                
                // Clear fields
                usernameField.clear();
                passwordField.clear();
                emailField.clear();
                phoneField.clear();
                roleDropdown.setValue("Customer");
                
                // Redirect to login after delay
                javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2));
                pause.setOnFinished(ev -> {
                    LoginView loginView = new LoginView(primaryStage);
                    primaryStage.setScene(loginView.createLoginScene());
                });
                pause.play();
            } else {
                messageLabel.setText("✗ Registration failed! Username may already exist. Please try a different username.");
                messageLabel.getStyleClass().add("error");
            }
        });
        
        // Back to login action
        backButton.setOnAction(e -> {
            LoginView loginView = new LoginView(primaryStage);
            primaryStage.setScene(loginView.createLoginScene());
        });
        
        // Scene setup
        Scene scene = new Scene(mainContainer, 900, 750);
        scene.setFill(javafx.scene.paint.Color.web("#d4af37")); // Gold background
        java.net.URL cssUrl = getClass().getResource("/application/resources/application.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }
        
        return scene;
    }
}
