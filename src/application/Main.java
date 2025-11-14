package application;

import application.views.LoginView;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            LoginView loginView = new LoginView(primaryStage);
            primaryStage.setTitle("Shahi Haveli Restaurant Management System");
            primaryStage.setScene(loginView.createLoginScene());
            primaryStage.setResizable(true);
            primaryStage.setAlwaysOnTop(true); // Bring to front
            primaryStage.show();
            primaryStage.setAlwaysOnTop(false); // Remove after showing
            primaryStage.toFront(); // Ensure it's in front
            System.out.println("Login window should be visible now!");
        } catch (Exception e) {
            System.err.println("Error starting application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

