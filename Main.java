package application;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
    	// Logo
    	Image logo = new Image(getClass().getResourceAsStream("ShahiHaveli_Logo.png"));
    	ImageView logoView = new ImageView(logo);
    	logoView.setFitWidth(120);
    	logoView.setPreserveRatio(true);
    	
    	// Let the logo resize dynamically with the window height
    	logoView.fitWidthProperty().bind(primaryStage.heightProperty().multiply(0.25)); 
    	// (25% of window height)
    	
        // Labels & Inputs
        Label title = new Label("Login");
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        Label roleLabel = new Label("Role:");
        ComboBox<String> roleDropdown = new ComboBox<>();
        roleDropdown.getItems().addAll("Admin", "Employee", "Customer");
        roleDropdown.setPromptText("Select Role");
        
        usernameField.setMaxWidth(300);
        passwordField.setMaxWidth(300);
        roleDropdown.setMaxWidth(150);

        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");
        Label messageLabel = new Label();

        // Layout
        VBox formLayout = new VBox(logoView,
                title,
                usernameLabel, usernameField,
                passwordLabel, passwordField,
                roleLabel, roleDropdown,
                loginButton, registerButton,
                messageLabel);
        formLayout.setAlignment(Pos.CENTER);
        formLayout.setPadding(new Insets(30));

        // Style classes
        formLayout.getStyleClass().add("form-layout");
        title.getStyleClass().add("title");
        loginButton.getStyleClass().add("login-button");
        registerButton.getStyleClass().add("register-button");
        messageLabel.getStyleClass().add("message-label");

        // Login action
        loginButton.setOnAction(e -> {
            String user = usernameField.getText();
            String pass = passwordField.getText();
            String role = roleDropdown.getValue();

            if (user.isEmpty() || pass.isEmpty() || role == null) {
                messageLabel.setText("Please fill all fields.");
                messageLabel.getStyleClass().add("error");
                return;
            }

            if ("admin".equals(user) && "1234".equals(pass) && "Admin".equals(role)) {
                messageLabel.setText("Access Granted as Admin");
                messageLabel.getStyleClass().add("success");
            } else {
                messageLabel.setText("Invalid credentials or role");
                messageLabel.getStyleClass().add("error");
            }
        });

        // Register action
        registerButton.setOnAction(e -> {
            messageLabel.setText("Redirecting to registration...");
            messageLabel.getStyleClass().add("success");
            // TODO: Open registration window or scene here
        });

        Scene scene = new Scene(formLayout, 420, 450);
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

        primaryStage.setTitle("Shahi Haveli Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

