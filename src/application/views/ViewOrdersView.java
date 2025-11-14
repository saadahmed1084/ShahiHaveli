package application.views;

import application.database.OrderDAO;
import application.models.Order;
import application.models.User;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class ViewOrdersView {
    private Stage primaryStage;
    private User currentUser;

    public ViewOrdersView(Stage primaryStage, User currentUser) {
        this.primaryStage = primaryStage;
        this.currentUser = currentUser;
    }

    public Scene createViewOrdersScene() {
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("My Orders");
        if ("Admin".equals(currentUser.getRole()) || "Employee".equals(currentUser.getRole())) {
            title.setText("All Orders");
        }
        title.getStyleClass().add("title");

        List<Order> orders;
        if ("Admin".equals(currentUser.getRole()) || "Employee".equals(currentUser.getRole())) {
            orders = OrderDAO.getAllOrders();
        } else {
            orders = OrderDAO.getOrdersByUserId(currentUser.getUserId());
        }

        if (orders.isEmpty()) {
            Label noOrders = new Label("No orders found.");
            noOrders.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 16px;");
            Button backButton = new Button("Back to Dashboard");
            backButton.getStyleClass().add("register-button");
            backButton.setOnAction(e -> {
                navigateToDashboard();
            });
            mainLayout.getChildren().addAll(noOrders, backButton);
        } else {
            TableView<Order> ordersTable = new TableView<>();
            ordersTable.setItems(FXCollections.observableArrayList(orders));

            TableColumn<Order, Integer> orderIdCol = new TableColumn<>("Order ID");
            orderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));

            TableColumn<Order, String> typeCol = new TableColumn<>("Type");
            typeCol.setCellValueFactory(new PropertyValueFactory<>("orderType"));

            TableColumn<Order, String> statusCol = new TableColumn<>("Status");
            statusCol.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));

            TableColumn<Order, Double> amountCol = new TableColumn<>("Amount");
            amountCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

            TableColumn<Order, String> paymentCol = new TableColumn<>("Payment");
            paymentCol.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));

            ordersTable.getColumns().addAll(orderIdCol, typeCol, statusCol, amountCol, paymentCol);
            ordersTable.setPrefHeight(400);

            HBox buttonBox = new HBox(15);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setPadding(new Insets(10, 0, 0, 0));
            
            Button trackButton = new Button("📍 Track Selected Order");
            trackButton.setFont(new javafx.scene.text.Font("Georgia", 14));
            trackButton.setPrefWidth(200);
            trackButton.getStyleClass().add("login-button");
            trackButton.setOnAction(e -> {
                Order selected = ordersTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    TrackOrderView trackView = new TrackOrderView(primaryStage, currentUser);
                    primaryStage.setScene(trackView.createTrackOrderScene());
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("No Selection");
                    alert.setHeaderText(null);
                    alert.setContentText("Please select an order to track.");
                    alert.showAndWait();
                }
            });
            
            // Add Accept Order button for Admin/Employee
            if ("Admin".equals(currentUser.getRole()) || "Employee".equals(currentUser.getRole())) {
                Button acceptButton = new Button("✅ Accept Selected Order");
                acceptButton.setFont(new javafx.scene.text.Font("Georgia", 14));
                acceptButton.setPrefWidth(200);
                acceptButton.getStyleClass().add("login-button");
                acceptButton.setOnAction(e -> {
                    Order selected = ordersTable.getSelectionModel().getSelectedItem();
                    if (selected != null) {
                        AcceptOrderView acceptView = new AcceptOrderView(primaryStage, currentUser, selected.getOrderId());
                        primaryStage.setScene(acceptView.createAcceptOrderScene());
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("No Selection");
                        alert.setHeaderText(null);
                        alert.setContentText("Please select an order to accept.");
                        alert.showAndWait();
                    }
                });
                buttonBox.getChildren().addAll(trackButton, acceptButton);
            } else {
                buttonBox.getChildren().add(trackButton);
            }

            mainLayout.getChildren().addAll(title, ordersTable, buttonBox);
        }

        Button backButton = new Button("← Back to Dashboard");
        backButton.setFont(new javafx.scene.text.Font("Georgia", 14));
        backButton.setPrefWidth(250);
        backButton.getStyleClass().add("register-button");
        backButton.setOnAction(e -> {
            navigateToDashboard();
        });
        mainLayout.getChildren().add(backButton);
        
        // Enhance styling
        mainLayout.getStyleClass().add("dashboard-layout");
        title.setFont(new javafx.scene.text.Font("Georgia", 32));

        Scene scene = new Scene(mainLayout, 1000, 700);
        scene.setFill(javafx.scene.paint.Color.web("#d4af37")); // Gold background
        java.net.URL cssUrl = getClass().getResource("/application/resources/application.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }
        return scene;
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



