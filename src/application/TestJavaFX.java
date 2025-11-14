package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Simple test application to verify JavaFX is working
 * Run this to test if JavaFX is properly installed
 */
public class TestJavaFX extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        Label testLabel = new Label("✓ JavaFX is working correctly!");
        testLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: green;");
        
        VBox root = new VBox(20, testLabel);
        root.setStyle("-fx-alignment: center; -fx-padding: 50;");
        
        Scene scene = new Scene(root, 400, 200);
        primaryStage.setTitle("JavaFX Test - Success!");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        System.out.println("JavaFX test window opened successfully!");
        System.out.println("If you see this window, JavaFX is properly installed!");
    }
    
    public static void main(String[] args) {
        System.out.println("Starting JavaFX test...");
        System.out.println("If JavaFX is installed correctly, a window will open.");
        launch(args);
    }
}


