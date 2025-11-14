package application.views;

import application.database.ReviewDAO;
import application.models.Review;
import application.models.User;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class ViewReviewsView {
    private Stage primaryStage;
    private User currentUser;

    public ViewReviewsView(Stage primaryStage, User currentUser) {
        this.primaryStage = primaryStage;
        this.currentUser = currentUser;
    }

    public Scene createViewReviewsScene() {
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("View Reviews");
        title.getStyleClass().add("title");

        // Filter options
        HBox filterBox = new HBox(10);
        Label dateLabel = new Label("Filter by Date Range:");
        dateLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold;");
        DatePicker startDate = new DatePicker();
        DatePicker endDate = new DatePicker();
        Label toLabel = new Label("to");
        toLabel.setStyle("-fx-text-fill: #ffffff;");
        Button filterButton = new Button("Filter");
        filterButton.getStyleClass().add("login-button");
        Button clearFilterButton = new Button("Clear");
        clearFilterButton.getStyleClass().add("register-button");

        filterBox.getChildren().addAll(dateLabel, startDate, toLabel, endDate, filterButton, clearFilterButton);
        filterBox.setAlignment(Pos.CENTER);

        // Reviews table
        List<Review> reviews = ReviewDAO.getAllReviews();
        TableView<Review> reviewsTable = new TableView<>();
        reviewsTable.setItems(FXCollections.observableArrayList(reviews));

        TableColumn<Review, Integer> reviewIdCol = new TableColumn<>("Review ID");
        reviewIdCol.setCellValueFactory(new PropertyValueFactory<>("reviewId"));

        TableColumn<Review, Integer> orderIdCol = new TableColumn<>("Order ID");
        orderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));

        TableColumn<Review, String> customerCol = new TableColumn<>("Customer");
        customerCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));

        TableColumn<Review, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("contactEmail"));

        TableColumn<Review, Integer> ambianceCol = new TableColumn<>("Ambiance");
        ambianceCol.setCellValueFactory(new PropertyValueFactory<>("ambianceRating"));

        TableColumn<Review, Integer> foodCol = new TableColumn<>("Food");
        foodCol.setCellValueFactory(new PropertyValueFactory<>("foodRating"));

        TableColumn<Review, Integer> serviceCol = new TableColumn<>("Service");
        serviceCol.setCellValueFactory(new PropertyValueFactory<>("serviceRating"));

        TableColumn<Review, Double> overallCol = new TableColumn<>("Overall");
        overallCol.setCellValueFactory(new PropertyValueFactory<>("overallRating"));

        TableColumn<Review, String> feedbackCol = new TableColumn<>("Feedback");
        feedbackCol.setCellValueFactory(new PropertyValueFactory<>("writtenFeedback"));
        feedbackCol.setPrefWidth(200);

        reviewsTable.getColumns().addAll(reviewIdCol, orderIdCol, customerCol, emailCol,
                ambianceCol, foodCol, serviceCol, overallCol, feedbackCol);
        reviewsTable.setPrefHeight(400);

        filterButton.setOnAction(e -> {
            if (startDate.getValue() != null && endDate.getValue() != null) {
                List<Review> filtered = ReviewDAO.filterReviewsByDateRange(
                        Date.valueOf(startDate.getValue()),
                        Date.valueOf(endDate.getValue())
                );
                reviewsTable.setItems(FXCollections.observableArrayList(filtered));
            }
        });

        clearFilterButton.setOnAction(e -> {
            startDate.setValue(null);
            endDate.setValue(null);
            reviewsTable.setItems(FXCollections.observableArrayList(ReviewDAO.getAllReviews()));
        });

        // Email copy functionality
        Label emailInfo = new Label("Select a review and copy email from the table above");
        emailInfo.setStyle("-fx-font-size: 12px; -fx-text-fill: #d4af37;");

        Button backButton = new Button("← Back to Dashboard");
        backButton.setFont(new javafx.scene.text.Font("Georgia", 14));
        backButton.setPrefWidth(250);
        backButton.getStyleClass().add("register-button");
        backButton.setOnAction(e -> {
            AdminDashboardView dashboard = new AdminDashboardView(primaryStage, currentUser);
            primaryStage.setScene(dashboard.createAdminDashboard());
        });
        
        // Enhance styling
        mainLayout.getStyleClass().add("dashboard-layout");
        title.setFont(new javafx.scene.text.Font("Georgia", 32));

        mainLayout.getChildren().addAll(title, filterBox, reviewsTable, emailInfo, backButton);

        Scene scene = new Scene(mainLayout, 1200, 700);
        scene.setFill(javafx.scene.paint.Color.web("#d4af37")); // Gold background
        scene.getStylesheets().add(getClass().getResource("/application/resources/application.css").toExternalForm());
        return scene;
    }
}



