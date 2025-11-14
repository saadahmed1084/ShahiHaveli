package application.views;

import application.database.OrderDAO;
import application.database.PaymentDAO;
import application.database.ReviewDAO;
import application.models.Order;
import application.models.OrderItem;
import application.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class AcceptOrderView {
    private Stage primaryStage;
    private User currentUser;
    private int orderId;

    public AcceptOrderView(Stage primaryStage, User currentUser, int orderId) {
        this.primaryStage = primaryStage;
        this.currentUser = currentUser;
        this.orderId = orderId;
    }

    public Scene createAcceptOrderScene() {
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Accept Your Order");
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

        Label orderLabel = new Label("Order ID: #" + orderId);
        orderLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        VBox itemsBox = new VBox(10);
        itemsBox.setPadding(new Insets(15));
        itemsBox.setStyle("-fx-background-color: rgba(212, 175, 55, 0.1); -fx-border-color: #d4af37; -fx-border-width: 2;");

        List<OrderItem> orderItems = order.getOrderItems();
        boolean allAccepted = true;

        for (OrderItem item : orderItems) {
            HBox itemBox = new HBox(15);
            itemBox.setPadding(new Insets(10));

            Label itemName = new Label(item.getItemName() + " x" + item.getQuantity());
            itemName.setStyle("-fx-font-size: 14px;");

            ToggleGroup actionGroup = new ToggleGroup();
            RadioButton accept = new RadioButton("Accept");
            RadioButton redo = new RadioButton("Redo");
            RadioButton reject = new RadioButton("Reject");
            accept.setToggleGroup(actionGroup);
            redo.setToggleGroup(actionGroup);
            reject.setToggleGroup(actionGroup);

            if ("accepted".equals(item.getItemStatus())) {
                accept.setSelected(true);
            } else if ("rejected".equals(item.getItemStatus())) {
                reject.setSelected(true);
            } else if ("redone".equals(item.getItemStatus())) {
                redo.setSelected(true);
            } else {
                accept.setSelected(true);
            }

            TextArea reasonField = new TextArea();
            reasonField.setPromptText("Reason (required if rejected/redone)");
            reasonField.setPrefRowCount(2);
            reasonField.setMaxWidth(300);
            reasonField.setVisible(false);

            redo.setOnAction(e -> reasonField.setVisible(true));
            reject.setOnAction(e -> reasonField.setVisible(true));
            accept.setOnAction(e -> reasonField.setVisible(false));

            VBox actionBox = new VBox(5, new HBox(10, accept, redo, reject), reasonField);
            itemBox.getChildren().addAll(itemName, actionBox);
            itemsBox.getChildren().add(itemBox);
        }

        Button submitButton = new Button("Submit");
        submitButton.getStyleClass().add("login-button");

        Label messageLabel = new Label();

        submitButton.setOnAction(e -> {
            boolean hasRejected = false;
            boolean hasRedone = false;

            for (int i = 0; i < orderItems.size(); i++) {
                OrderItem item = orderItems.get(i);
                VBox itemContainer = (VBox) itemsBox.getChildren().get(i);
                HBox actionContainer = (HBox) ((VBox) itemContainer.getChildren().get(1)).getChildren().get(0);
                RadioButton accept = (RadioButton) actionContainer.getChildren().get(0);
                RadioButton redo = (RadioButton) actionContainer.getChildren().get(1);
                RadioButton reject = (RadioButton) actionContainer.getChildren().get(2);
                TextArea reasonField = (TextArea) ((VBox) itemContainer.getChildren().get(1)).getChildren().get(1);

                if (reject.isSelected()) {
                    if (reasonField.getText().isEmpty()) {
                        messageLabel.setText("Please provide reason for rejected items");
                        messageLabel.setStyle("-fx-text-fill: #ff4c4c;");
                        return;
                    }
                    OrderDAO.updateOrderItemStatus(item.getOrderItemId(), "rejected");
                    PaymentDAO.refundPayment(orderId, item.getPrice() * item.getQuantity());
                    hasRejected = true;
                } else if (redo.isSelected()) {
                    if (reasonField.getText().isEmpty()) {
                        messageLabel.setText("Please provide reason for items to be redone");
                        messageLabel.setStyle("-fx-text-fill: #ff4c4c;");
                        return;
                    }
                    OrderDAO.updateOrderItemStatus(item.getOrderItemId(), "redone");
                    hasRedone = true;
                } else {
                    OrderDAO.updateOrderItemStatus(item.getOrderItemId(), "accepted");
                }
            }

            // Clear payment data if all accepted
            boolean allItemsAccepted = !hasRejected && !hasRedone;
            if (allItemsAccepted) {
                PaymentDAO.clearPaymentData(orderId);
                OrderDAO.updateOrderStatus(orderId, "Completed");
                messageLabel.setText("Order accepted! Payment data cleared.");
                messageLabel.setStyle("-fx-text-fill: lightgreen;");

                // Check if review already exists
                if (!ReviewDAO.hasReviewForOrder(orderId)) {
                    javafx.application.Platform.runLater(() -> {
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        javafx.application.Platform.runLater(() -> {
                            SubmitReviewView reviewView = new SubmitReviewView(primaryStage, currentUser, orderId);
                            primaryStage.setScene(reviewView.createSubmitReviewScene());
                        });
                    });
                } else {
                    CustomerDashboardView dashboard = new CustomerDashboardView(primaryStage, currentUser);
                    primaryStage.setScene(dashboard.createCustomerDashboard());
                }
            } else if (hasRedone) {
                messageLabel.setText("Items marked for redo. Redirecting to track order...");
                messageLabel.setStyle("-fx-text-fill: #d4af37;");
                javafx.application.Platform.runLater(() -> {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    javafx.application.Platform.runLater(() -> {
                        TrackOrderView trackView = new TrackOrderView(primaryStage, currentUser);
                        primaryStage.setScene(trackView.createTrackOrderScene());
                    });
                });
            } else {
                messageLabel.setText("Rejected items refunded. Thank you for your feedback.");
                messageLabel.setStyle("-fx-text-fill: lightgreen;");
            }
        });

        Button backButton = new Button("← Back");
        backButton.setFont(new javafx.scene.text.Font("Georgia", 14));
        backButton.setPrefWidth(200);
        backButton.getStyleClass().add("register-button");
        backButton.setOnAction(e -> {
            // Navigate back based on user role
            if ("Admin".equals(currentUser.getRole()) || "Employee".equals(currentUser.getRole())) {
                ViewOrdersView viewOrdersView = new ViewOrdersView(primaryStage, currentUser);
                primaryStage.setScene(viewOrdersView.createViewOrdersScene());
            } else {
                TrackOrderView trackView = new TrackOrderView(primaryStage, currentUser);
                primaryStage.setScene(trackView.createTrackOrderScene());
            }
        });

        mainLayout.getChildren().addAll(title, orderLabel, itemsBox, submitButton, messageLabel, backButton);

        Scene scene = new Scene(mainLayout, 700, 600);
        scene.setFill(javafx.scene.paint.Color.web("#d4af37")); // Gold background
        scene.getStylesheets().add(getClass().getResource("/application/resources/application.css").toExternalForm());
        return scene;
    }
}



