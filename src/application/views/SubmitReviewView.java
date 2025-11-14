package application.views;

import application.database.ReviewDAO;
import application.models.Review;
import application.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class SubmitReviewView {
    private Stage primaryStage;
    private User currentUser;
    private int orderId;

    public SubmitReviewView(Stage primaryStage, User currentUser, int orderId) {
        this.primaryStage = primaryStage;
        this.currentUser = currentUser;
        this.orderId = orderId;
    }

    public Scene createSubmitReviewScene() {
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.CENTER);

        Label title = new Label("Rate Your Experience");
        title.getStyleClass().add("title");

        Label question = new Label("Would you like to rate your experience?");
        question.setStyle("-fx-font-size: 16px;");

        // Contact details (if not in profile)
        VBox contactBox = new VBox(10);
        Label contactLabel = new Label("Contact Details (optional):");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone Number");
        phoneField.setMaxWidth(300);
        TextField emailField = new TextField();
        emailField.setPromptText("Email Address");
        emailField.setMaxWidth(300);
        contactBox.getChildren().addAll(contactLabel, phoneField, emailField);

        // Ratings
        VBox ratingsBox = new VBox(15);
        ratingsBox.setPadding(new Insets(15));
        ratingsBox.setStyle("-fx-background-color: rgba(212, 175, 55, 0.1); -fx-border-color: #d4af37; -fx-border-width: 2;");

        Label ambianceLabel = new Label("Ambiance:");
        ComboBox<Integer> ambianceRating = new ComboBox<>();
        ambianceRating.getItems().addAll(1, 2, 3, 4, 5);
        ambianceRating.setPromptText("Select rating");

        Label foodLabel = new Label("Food Quality:");
        ComboBox<Integer> foodRating = new ComboBox<>();
        foodRating.getItems().addAll(1, 2, 3, 4, 5);
        foodRating.setPromptText("Select rating");

        Label serviceLabel = new Label("Customer Service:");
        ComboBox<Integer> serviceRating = new ComboBox<>();
        serviceRating.getItems().addAll(1, 2, 3, 4, 5);
        serviceRating.setPromptText("Select rating");

        ratingsBox.getChildren().addAll(ambianceLabel, ambianceRating, foodLabel, foodRating, serviceLabel, serviceRating);

        // Written feedback
        Label feedbackLabel = new Label("Written Feedback (optional):");
        TextArea feedbackField = new TextArea();
        feedbackField.setPromptText("Share your thoughts...");
        feedbackField.setPrefRowCount(4);
        feedbackField.setMaxWidth(400);

        Button submitButton = new Button("Submit Review");
        submitButton.getStyleClass().add("login-button");

        Button skipButton = new Button("Skip");
        skipButton.getStyleClass().add("register-button");

        Label messageLabel = new Label();

        submitButton.setOnAction(e -> {
            if (ambianceRating.getValue() == null || foodRating.getValue() == null || serviceRating.getValue() == null) {
                messageLabel.setText("Please provide all ratings");
                messageLabel.setStyle("-fx-text-fill: #ff4c4c;");
                return;
            }

            double overall = (ambianceRating.getValue() + foodRating.getValue() + serviceRating.getValue()) / 3.0;

            Review review = new Review();
            review.setOrderId(orderId);
            review.setUserId(currentUser.getUserId());
            review.setCustomerName(currentUser.getUsername());
            review.setContactPhone(phoneField.getText().isEmpty() ? currentUser.getPhone() : phoneField.getText());
            review.setContactEmail(emailField.getText().isEmpty() ? currentUser.getEmail() : emailField.getText());
            review.setAmbianceRating(ambianceRating.getValue());
            review.setFoodRating(foodRating.getValue());
            review.setServiceRating(serviceRating.getValue());
            review.setOverallRating(overall);
            review.setWrittenFeedback(feedbackField.getText());

            if (ReviewDAO.createReview(review)) {
                messageLabel.setText("Thank you for your feedback!");
                messageLabel.setStyle("-fx-text-fill: lightgreen;");
                submitButton.setDisable(true);
                skipButton.setDisable(true);

                javafx.application.Platform.runLater(() -> {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    javafx.application.Platform.runLater(() -> {
                        CustomerDashboardView dashboard = new CustomerDashboardView(primaryStage, currentUser);
                        primaryStage.setScene(dashboard.createCustomerDashboard());
                    });
                });
            } else {
                messageLabel.setText("Failed to submit review. Please try again.");
                messageLabel.setStyle("-fx-text-fill: #ff4c4c;");
            }
        });

        skipButton.setOnAction(e -> {
            CustomerDashboardView dashboard = new CustomerDashboardView(primaryStage, currentUser);
            primaryStage.setScene(dashboard.createCustomerDashboard());
        });

        mainLayout.getChildren().addAll(title, question, contactBox, ratingsBox, feedbackLabel, feedbackField,
                submitButton, skipButton, messageLabel);

        Scene scene = new Scene(mainLayout, 600, 700);
        scene.setFill(javafx.scene.paint.Color.web("#d4af37")); // Gold background
        scene.getStylesheets().add(getClass().getResource("/application/resources/application.css").toExternalForm());
        return scene;
    }
}



