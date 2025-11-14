package application.views;

import application.database.OrderDAO;
import application.models.Order;
import application.models.User;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;

public class TrackOrderView {
    private Stage primaryStage;
    private User currentUser;
    private Timeline updateTimeline;

    public TrackOrderView(Stage primaryStage, User currentUser) {
        this.primaryStage = primaryStage;
        this.currentUser = currentUser;
    }

    public Scene createTrackOrderScene() {
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Track Your Order");
        title.getStyleClass().add("title");

        // Get user's orders
        List<Order> orders = OrderDAO.getOrdersByUserId(currentUser.getUserId());
        if (orders.isEmpty()) {
            Label noOrders = new Label("No orders found.");
            Button backButton = new Button("Back to Dashboard");
            backButton.setOnAction(e -> {
                CustomerDashboardView dashboard = new CustomerDashboardView(primaryStage, currentUser);
                primaryStage.setScene(dashboard.createCustomerDashboard());
            });
            mainLayout.getChildren().addAll(noOrders, backButton);
        } else {
            // Show most recent order
            Order currentOrder = orders.get(0);
            VBox orderBox = createOrderStatusBox(currentOrder);
            mainLayout.getChildren().addAll(title, orderBox);

            // Auto-refresh every 3 seconds
            updateTimeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
                Order updatedOrder = OrderDAO.getOrderById(currentOrder.getOrderId());
                if (updatedOrder != null) {
                    mainLayout.getChildren().clear();
                    mainLayout.getChildren().addAll(title, createOrderStatusBox(updatedOrder));
                }
            }));
            updateTimeline.setCycleCount(Timeline.INDEFINITE);
            updateTimeline.play();
        }

        Button backButton = new Button("← Back to Dashboard");
        backButton.setFont(new javafx.scene.text.Font("Georgia", 14));
        backButton.setPrefWidth(250);
        backButton.getStyleClass().add("register-button");
        backButton.setOnAction(e -> {
            if (updateTimeline != null) updateTimeline.stop();
            navigateToDashboard();
        });
        
        // Add navigation helper method
        mainLayout.getStyleClass().add("dashboard-layout");
        mainLayout.getChildren().add(backButton);

        Scene scene = new Scene(mainLayout, 700, 600);
        scene.setFill(javafx.scene.paint.Color.web("#d4af37")); // Gold background
        scene.getStylesheets().add(getClass().getResource("/application/resources/application.css").toExternalForm());
        return scene;
    }

    private VBox createOrderStatusBox(Order order) {
        VBox orderBox = new VBox(15);
        orderBox.setPadding(new Insets(20));
        orderBox.setStyle("-fx-background-color: rgba(212, 175, 55, 0.1); -fx-border-color: #d4af37; -fx-border-width: 2;");

        Label orderIdLabel = new Label("Order ID: #" + order.getOrderId());
        orderIdLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Order status progression
        String[] statuses = {
            "Receiving Order",
            "Sourcing Ingredients",
            "Cooking Meal",
            "Packaging Meal",
            "Delivering Package",
            "Your Meal is Ready"
        };

        VBox statusBox = new VBox(10);
        for (String status : statuses) {
            HBox statusRow = new HBox(10);
            Label statusLabel = new Label(status);
            Label checkLabel = new Label("○");
            
            if (order.getOrderStatus().equals(status) || isStatusCompleted(order.getOrderStatus(), status, statuses)) {
                checkLabel.setText("✓");
                checkLabel.setStyle("-fx-text-fill: lightgreen; -fx-font-size: 16px;");
                statusLabel.setStyle("-fx-text-fill: lightgreen;");
            } else {
                checkLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 16px;");
                statusLabel.setStyle("-fx-text-fill: #888;");
            }
            
            statusRow.getChildren().addAll(checkLabel, statusLabel);
            statusBox.getChildren().add(statusRow);
        }

        Label currentStatusLabel = new Label("Current Status: " + order.getOrderStatus());
        currentStatusLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #d4af37;");

        // Cancel order button (if order not completed)
        if (!order.getOrderStatus().equals("Your Meal is Ready") && !order.getOrderStatus().equals("Cancelled")) {
            Button cancelButton = new Button("Cancel Order");
            cancelButton.getStyleClass().add("register-button");
            cancelButton.setOnAction(e -> {
                CancelOrderView cancelView = new CancelOrderView(primaryStage, currentUser, order.getOrderId());
                primaryStage.setScene(cancelView.createCancelOrderScene());
            });
            orderBox.getChildren().add(cancelButton);
        }

        // Accept order button (if ready)
        if (order.getOrderStatus().equals("Your Meal is Ready")) {
            Button acceptButton = new Button("Accept Order");
            acceptButton.getStyleClass().add("login-button");
            acceptButton.setOnAction(e -> {
                AcceptOrderView acceptView = new AcceptOrderView(primaryStage, currentUser, order.getOrderId());
                primaryStage.setScene(acceptView.createAcceptOrderScene());
            });
            orderBox.getChildren().add(acceptButton);
        }

        orderBox.getChildren().addAll(orderIdLabel, currentStatusLabel, statusBox);
        return orderBox;
    }

    private boolean isStatusCompleted(String currentStatus, String checkStatus, String[] allStatuses) {
        for (String status : allStatuses) {
            if (status.equals(currentStatus)) {
                return false;
            }
            if (status.equals(checkStatus)) {
                return true;
            }
        }
        return false;
    }
    
    private void navigateToDashboard() {
        if ("Admin".equals(currentUser.getRole())) {
            AdminDashboardView dashboard = new AdminDashboardView(primaryStage, currentUser);
            primaryStage.setScene(dashboard.createAdminDashboard());
        } else if ("Employee".equals(currentUser.getRole())) {
            EmployeeDashboardView dashboard = new EmployeeDashboardView(primaryStage, currentUser);
            primaryStage.setScene(dashboard.createEmployeeDashboard());
        } else {
            CustomerDashboardView dashboard = new CustomerDashboardView(primaryStage, currentUser);
            primaryStage.setScene(dashboard.createCustomerDashboard());
        }
    }
}



