package application.views;

import application.database.OrderDAO;
import application.database.PaymentDAO;
import application.models.Order;
import application.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class CancelOrderView {
    private Stage primaryStage;
    private User currentUser;
    private int orderId;

    public CancelOrderView(Stage primaryStage, User currentUser, int orderId) {
        this.primaryStage = primaryStage;
        this.currentUser = currentUser;
        this.orderId = orderId;
    }

    public Scene createCancelOrderScene() {
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.CENTER);

        Label title = new Label("Cancel Order");
        title.getStyleClass().add("title");

        Order order = OrderDAO.getOrderById(orderId);
        if (order == null) {
            Label error = new Label("Order not found!");
            mainLayout.getChildren().add(error);
            Scene scene = new Scene(mainLayout, 600, 400);
            scene.setFill(javafx.scene.paint.Color.web("#d4af37")); // Gold background
            scene.getStylesheets().add(getClass().getResource("/application/resources/application.css").toExternalForm());
            return scene;
        }

        // Check if order is already complete
        if (order.getOrderStatus().equals("Your Meal is Ready") || order.getOrderStatus().equals("Completed")) {
            Label error = new Label("Cannot cancel order. Order is already complete.");
            error.setStyle("-fx-text-fill: #ff4c4c;");
            Button backButton = new Button("Back");
            backButton.setOnAction(e -> {
                TrackOrderView trackView = new TrackOrderView(primaryStage, currentUser);
                primaryStage.setScene(trackView.createTrackOrderScene());
            });
            mainLayout.getChildren().addAll(error, backButton);
            Scene scene = new Scene(mainLayout, 600, 400);
            scene.setFill(javafx.scene.paint.Color.web("#d4af37")); // Gold background
            scene.getStylesheets().add(getClass().getResource("/application/resources/application.css").toExternalForm());
            return scene;
        }

        Label orderLabel = new Label("Order ID: #" + orderId);
        Label reasonLabel = new Label("Select reason for cancellation:");

        ToggleGroup reasonGroup = new ToggleGroup();
        RadioButton reason1 = new RadioButton("I changed my mind.");
        RadioButton reason2 = new RadioButton("I don't have sufficient cash.");
        RadioButton reason3 = new RadioButton("I accidentally ordered the wrong items.");
        RadioButton reason4 = new RadioButton("I have to leave because of an emergency.");
        RadioButton reason5 = new RadioButton("My order is taking too long.");

        reason1.setToggleGroup(reasonGroup);
        reason2.setToggleGroup(reasonGroup);
        reason3.setToggleGroup(reasonGroup);
        reason4.setToggleGroup(reasonGroup);
        reason5.setToggleGroup(reasonGroup);

        VBox reasonsBox = new VBox(10, reason1, reason2, reason3, reason4, reason5);

        Label messageLabel = new Label();
        Button cancelButton = new Button("Confirm Cancellation");
        cancelButton.getStyleClass().add("login-button");

        cancelButton.setOnAction(e -> {
            RadioButton selected = (RadioButton) reasonGroup.getSelectedToggle();
            if (selected == null) {
                messageLabel.setText("Please select a reason");
                messageLabel.setStyle("-fx-text-fill: #ff4c4c;");
                return;
            }

            String reason = selected.getText();

            // Check if "taking too long" before standard prep time
            if (reason5.isSelected()) {
                // Simple check - can be enhanced with actual time calculation
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Wait Confirmation");
                alert.setHeaderText(null);
                alert.setContentText("Standard preparation time is 30 minutes. Would you like to wait?");
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        TrackOrderView trackView = new TrackOrderView(primaryStage, currentUser);
                        primaryStage.setScene(trackView.createTrackOrderScene());
                        return;
                    }
                });
            }

            // Process cancellation
            if (OrderDAO.cancelOrder(orderId, reason)) {
                // Refund if payment was made
                if ("Paid".equals(order.getPaymentStatus())) {
                    PaymentDAO.refundPayment(orderId, order.getTotalAmount());
                }

                messageLabel.setText("Order cancelled successfully. Refund processed if applicable.");
                messageLabel.setStyle("-fx-text-fill: lightgreen;");

                // Check if reorder option
                if (reason2.isSelected() || reason3.isSelected()) {
                    Alert reorderAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    reorderAlert.setTitle("Reorder");
                    reorderAlert.setHeaderText(null);
                    reorderAlert.setContentText("Would you like to place a new order?");
                    reorderAlert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            PlaceOrderView placeOrderView = new PlaceOrderView(primaryStage, currentUser);
                            primaryStage.setScene(placeOrderView.createPlaceOrderScene());
                        } else {
                            CustomerDashboardView dashboard = new CustomerDashboardView(primaryStage, currentUser);
                            primaryStage.setScene(dashboard.createCustomerDashboard());
                        }
                    });
                } else {
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
                }
            } else {
                messageLabel.setText("Failed to cancel order. Please try again.");
                messageLabel.setStyle("-fx-text-fill: #ff4c4c;");
            }
        });

        Button backButton = new Button("Back");
        backButton.getStyleClass().add("register-button");
        backButton.setOnAction(e -> {
            TrackOrderView trackView = new TrackOrderView(primaryStage, currentUser);
            primaryStage.setScene(trackView.createTrackOrderScene());
        });

        mainLayout.getChildren().addAll(title, orderLabel, reasonLabel, reasonsBox, cancelButton, messageLabel, backButton);

        Scene scene = new Scene(mainLayout, 600, 500);
        scene.setFill(javafx.scene.paint.Color.web("#d4af37")); // Gold background
        scene.getStylesheets().add(getClass().getResource("/application/resources/application.css").toExternalForm());
        return scene;
    }
}



