package application.views;

import application.database.ReservationDAO;
import application.models.Reservation;
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

public class ManageReservationsView {
    private Stage primaryStage;
    private User currentUser;

    public ManageReservationsView(Stage primaryStage, User currentUser) {
        this.primaryStage = primaryStage;
        this.currentUser = currentUser;
    }

    public Scene createManageReservationsScene() {
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Manage Reservations");
        title.getStyleClass().add("title");

        // Pending reservations
        Label pendingLabel = new Label("Pending Reservations");
        pendingLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        List<Reservation> pendingReservations = ReservationDAO.getPendingReservations();
        TableView<Reservation> pendingTable = new TableView<>();
        pendingTable.setItems(FXCollections.observableArrayList(pendingReservations));

        TableColumn<Reservation, Integer> resIdCol = new TableColumn<>("Reservation ID");
        resIdCol.setCellValueFactory(new PropertyValueFactory<>("reservationId"));

        TableColumn<Reservation, String> nameCol = new TableColumn<>("Customer Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));

        TableColumn<Reservation, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<Reservation, java.sql.Date> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("reservationDate"));

        TableColumn<Reservation, java.sql.Time> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("reservationTime"));

        TableColumn<Reservation, Integer> partyCol = new TableColumn<>("Party Size");
        partyCol.setCellValueFactory(new PropertyValueFactory<>("partySize"));

        pendingTable.getColumns().addAll(resIdCol, nameCol, phoneCol, dateCol, timeCol, partyCol);
        pendingTable.setPrefHeight(200);

        HBox pendingActions = new HBox(10);
        Button approveButton = new Button("Approve Selected");
        approveButton.getStyleClass().add("login-button");
        Button declineButton = new Button("Decline Selected");
        declineButton.getStyleClass().add("register-button");

        approveButton.setOnAction(e -> {
            Reservation selected = pendingTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                if (ReservationDAO.updateReservationStatus(selected.getReservationId(), "approved")) {
                    pendingTable.getItems().remove(selected);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Reservation Approved");
                    alert.setHeaderText(null);
                    alert.setContentText("Reservation #" + selected.getReservationId() + " has been approved.");
                    alert.showAndWait();
                }
            }
        });

        declineButton.setOnAction(e -> {
            Reservation selected = pendingTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Confirm Decline");
                confirm.setHeaderText(null);
                confirm.setContentText("Declining will issue a full refund. Continue?");
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        if (ReservationDAO.updateReservationStatus(selected.getReservationId(), "declined")) {
                            pendingTable.getItems().remove(selected);
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Reservation Declined");
                            alert.setHeaderText(null);
                            alert.setContentText("Reservation declined. Full refund issued.");
                            alert.showAndWait();
                        }
                    }
                });
            }
        });

        pendingActions.getChildren().addAll(approveButton, declineButton);

        // All reservations
        Label allLabel = new Label("All Reservations");
        allLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        List<Reservation> allReservations = ReservationDAO.getAllReservations();
        TableView<Reservation> allTable = new TableView<>();
        allTable.setItems(FXCollections.observableArrayList(allReservations));

        TableColumn<Reservation, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        allTable.getColumns().addAll(resIdCol, nameCol, phoneCol, dateCol, timeCol, partyCol, statusCol);
        allTable.setPrefHeight(300);

        HBox allActions = new HBox(10);
        Button deleteButton = new Button("Delete Selected");
        deleteButton.getStyleClass().add("register-button");

        deleteButton.setOnAction(e -> {
            Reservation selected = allTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Confirm Delete");
                confirm.setHeaderText(null);
                confirm.setContentText("Are you sure you want to delete this reservation?");
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        if (ReservationDAO.deleteReservation(selected.getReservationId())) {
                            allTable.getItems().remove(selected);
                        }
                    }
                });
            }
        });

        allActions.getChildren().add(deleteButton);

        Button backButton = new Button("← Back to Dashboard");
        backButton.setFont(new javafx.scene.text.Font("Georgia", 14));
        backButton.setPrefWidth(250);
        backButton.getStyleClass().add("register-button");
        backButton.setOnAction(e -> {
            navigateToDashboard();
        });
        
        // Enhance styling
        mainLayout.getStyleClass().add("dashboard-layout");
        title.setFont(new javafx.scene.text.Font("Georgia", 32));

        mainLayout.getChildren().addAll(title, pendingLabel, pendingTable, pendingActions,
                allLabel, allTable, allActions, backButton);

        Scene scene = new Scene(mainLayout, 1000, 800);
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



