package application.views;

import application.database.MenuDAO;
import application.models.MenuItem;
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

public class ManageMenuView {
    private Stage primaryStage;
    private User currentUser;

    public ManageMenuView(Stage primaryStage, User currentUser) {
        this.primaryStage = primaryStage;
        this.currentUser = currentUser;
    }

    public Scene createManageMenuScene() {
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Manage Menu Items");
        title.getStyleClass().add("title");

        // Add new item form
        VBox addForm = new VBox(10);
        addForm.setPadding(new Insets(15));
        addForm.setStyle("-fx-background-color: rgba(212, 175, 55, 0.1); -fx-border-color: #d4af37; -fx-border-width: 2;");
        Label formTitle = new Label("Add New Menu Item");
        formTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");

        TextField nameField = new TextField();
        nameField.setPromptText("Item Name");
        nameField.setMaxWidth(300);

        TextArea descField = new TextArea();
        descField.setPromptText("Description");
        descField.setPrefRowCount(2);
        descField.setMaxWidth(300);

        TextField categoryField = new TextField();
        categoryField.setPromptText("Category (e.g., Biryani, Karahi, Naan)");
        categoryField.setMaxWidth(300);

        TextField priceField = new TextField();
        priceField.setPromptText("Price");
        priceField.setMaxWidth(300);

        CheckBox availableCheck = new CheckBox("Available");
        availableCheck.setSelected(true);

        Button addButton = new Button("Add Item");
        addButton.getStyleClass().add("login-button");

        Label messageLabel = new Label();

        addButton.setOnAction(e -> {
            if (nameField.getText().isEmpty() || categoryField.getText().isEmpty() || priceField.getText().isEmpty()) {
                messageLabel.setText("Please fill all required fields");
                messageLabel.setStyle("-fx-text-fill: #ff4c4c;");
                return;
            }

            try {
                double price = Double.parseDouble(priceField.getText());
                MenuItem item = new MenuItem();
                item.setName(nameField.getText());
                item.setDescription(descField.getText());
                item.setCategory(categoryField.getText());
                item.setPrice(price);
                item.setAvailable(availableCheck.isSelected());

                if (MenuDAO.addMenuItem(item)) {
                    messageLabel.setText("Item added successfully!");
                    messageLabel.setStyle("-fx-text-fill: lightgreen;");
                    nameField.clear();
                    descField.clear();
                    categoryField.clear();
                    priceField.clear();
                    // Refresh table would go here
                } else {
                    messageLabel.setText("Failed to add item");
                    messageLabel.setStyle("-fx-text-fill: #ff4c4c;");
                }
            } catch (NumberFormatException ex) {
                messageLabel.setText("Invalid price format");
                messageLabel.setStyle("-fx-text-fill: #ff4c4c;");
            }
        });

        addForm.getChildren().addAll(formTitle, nameField, descField, categoryField, priceField, availableCheck, addButton, messageLabel);

        // Menu items table
        List<MenuItem> menuItems = MenuDAO.getAllMenuItems();
        TableView<MenuItem> menuTable = new TableView<>();
        menuTable.setItems(FXCollections.observableArrayList(menuItems));

        TableColumn<MenuItem, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("itemId"));

        TableColumn<MenuItem, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<MenuItem, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<MenuItem, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<MenuItem, Boolean> availableCol = new TableColumn<>("Available");
        availableCol.setCellValueFactory(new PropertyValueFactory<>("available"));

        menuTable.getColumns().addAll(idCol, nameCol, categoryCol, priceCol, availableCol);
        menuTable.setPrefHeight(300);

        // Edit/Delete buttons
        HBox actionBox = new HBox(10);
        Button editButton = new Button("Edit Selected");
        editButton.getStyleClass().add("login-button");
        Button deleteButton = new Button("Delete Selected");
        deleteButton.getStyleClass().add("register-button");

        editButton.setOnAction(e -> {
            MenuItem selected = menuTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                // Simple edit - populate form
                nameField.setText(selected.getName());
                descField.setText(selected.getDescription());
                categoryField.setText(selected.getCategory());
                priceField.setText(String.valueOf(selected.getPrice()));
                availableCheck.setSelected(selected.isAvailable());

                // Change add button to update
                addButton.setText("Update Item");
                addButton.setOnAction(ev -> {
                    selected.setName(nameField.getText());
                    selected.setDescription(descField.getText());
                    selected.setCategory(categoryField.getText());
                    try {
                        selected.setPrice(Double.parseDouble(priceField.getText()));
                        selected.setAvailable(availableCheck.isSelected());
                        if (MenuDAO.updateMenuItem(selected)) {
                            messageLabel.setText("Item updated!");
                            messageLabel.setStyle("-fx-text-fill: lightgreen;");
                            addButton.setText("Add Item");
                            nameField.clear();
                            descField.clear();
                            categoryField.clear();
                            priceField.clear();
                        }
                    } catch (NumberFormatException ex) {
                        messageLabel.setText("Invalid price");
                        messageLabel.setStyle("-fx-text-fill: #ff4c4c;");
                    }
                });
            }
        });

        deleteButton.setOnAction(e -> {
            MenuItem selected = menuTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Confirm Delete");
                confirm.setHeaderText(null);
                confirm.setContentText("Are you sure you want to delete this item?");
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        if (MenuDAO.deleteMenuItem(selected.getItemId())) {
                            menuTable.getItems().remove(selected);
                            messageLabel.setText("Item deleted!");
                            messageLabel.setStyle("-fx-text-fill: lightgreen;");
                        }
                    }
                });
            }
        });

        actionBox.getChildren().addAll(editButton, deleteButton);

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

        mainLayout.getChildren().addAll(title, addForm, menuTable, actionBox, backButton);

        Scene scene = new Scene(mainLayout, 900, 800);
        scene.setFill(javafx.scene.paint.Color.web("#d4af37")); // Gold background
        scene.getStylesheets().add(getClass().getResource("/application/resources/application.css").toExternalForm());
        return scene;
    }
}



