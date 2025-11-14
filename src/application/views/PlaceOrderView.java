package application.views;

import application.database.MenuDAO;
import application.database.OrderDAO;
import application.database.ReservationDAO;
import application.models.MenuItem;
import application.models.Order;
import application.models.OrderItem;
import application.models.Reservation;
import application.models.User;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.*;

public class PlaceOrderView {
    private Stage primaryStage;
    private User currentUser;
    private String orderType;
    private Map<MenuItem, Integer> cart = new HashMap<>();
    private List<MenuItem> menuItems = new ArrayList<>();

    public PlaceOrderView(Stage primaryStage, User currentUser) {
        this.primaryStage = primaryStage;
        this.currentUser = currentUser;
    }

    public Scene createPlaceOrderScene() {
        try {
            // Main container - BorderPane for clean layout
            BorderPane root = new BorderPane();
            root.setStyle("-fx-background-color: #d4af37;");
            
            // ========== TOP SECTION: Header ==========
            VBox topSection = new VBox(10);
            topSection.setAlignment(Pos.CENTER);
            topSection.setPadding(new Insets(15, 20, 15, 20));
            topSection.setStyle("-fx-background-color: transparent;");
            
            // Logo and Title
            HBox headerBox = new HBox(15);
            headerBox.setAlignment(Pos.CENTER);
            
            ImageView logoView = null;
            try {
                java.io.InputStream logoStream = getClass().getResourceAsStream("/application/resources/ShahiHaveli_Logo.png");
                if (logoStream != null) {
                    Image logo = new Image(logoStream);
                    logoView = new ImageView(logo);
                    logoView.setFitWidth(80);
                    logoView.setFitHeight(80);
                    logoView.setPreserveRatio(true);
                    logoView.setEffect(new javafx.scene.effect.DropShadow(8, javafx.scene.paint.Color.web("#000000")));
                }
            } catch (Exception e) {
                System.err.println("Logo not found: " + e.getMessage());
            }
            
            Label title = new Label("🍽️ Place Your Royal Order");
            title.setFont(new Font("Georgia", 32));
            title.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold;");
            title.setEffect(new javafx.scene.effect.DropShadow(12, javafx.scene.paint.Color.web("#000000")));
            
            if (logoView != null) {
                headerBox.getChildren().addAll(logoView, title);
            } else {
                headerBox.getChildren().add(title);
            }
            
            // Order Type Selection
            VBox orderTypeBox = new VBox(10);
            orderTypeBox.setAlignment(Pos.CENTER);
            orderTypeBox.setPadding(new Insets(10, 20, 10, 20));
            orderTypeBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 10; -fx-border-color: #ffffff; -fx-border-width: 2; -fx-border-radius: 10;");
            
            Label orderTypeLabel = new Label("👑 Select Order Type");
            orderTypeLabel.setFont(new Font("Georgia", 16));
            orderTypeLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold;");
            
            ToggleGroup orderTypeGroup = new ToggleGroup();
            RadioButton dineIn = new RadioButton("🏰 Dine-In");
            dineIn.setFont(new Font("Georgia", 14));
            dineIn.setStyle("-fx-text-fill: #ffffff;");
            RadioButton takeaway = new RadioButton("📦 Takeaway");
            takeaway.setFont(new Font("Georgia", 14));
            takeaway.setStyle("-fx-text-fill: #ffffff;");
            RadioButton delivery = new RadioButton("🚚 Delivery");
            delivery.setFont(new Font("Georgia", 14));
            delivery.setStyle("-fx-text-fill: #ffffff;");
            
            dineIn.setToggleGroup(orderTypeGroup);
            takeaway.setToggleGroup(orderTypeGroup);
            delivery.setToggleGroup(orderTypeGroup);
            
            HBox radioBox = new HBox(25);
            radioBox.setAlignment(Pos.CENTER);
            radioBox.getChildren().addAll(dineIn, takeaway, delivery);
            
            orderTypeBox.getChildren().addAll(orderTypeLabel, radioBox);
            
            // Reservation and Delivery Fields
            HBox extraFieldsBox = new HBox(20);
            extraFieldsBox.setAlignment(Pos.CENTER);
            extraFieldsBox.setPadding(new Insets(10, 0, 0, 0));
            
            Label reservationLabel = new Label("Reservation ID:");
            reservationLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold;");
            TextField reservationField = new TextField();
            reservationField.setPromptText("Enter reservation ID");
            reservationField.setPrefWidth(200);
            reservationField.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #1a1a1a;");
            
            HBox reservationBox = new HBox(10);
            reservationBox.setAlignment(Pos.CENTER);
            reservationBox.getChildren().addAll(reservationLabel, reservationField);
            reservationBox.setVisible(false);
            
            VBox deliveryBox = new VBox(8);
            deliveryBox.setAlignment(Pos.CENTER);
            Label addressLabel = new Label("Delivery Address:");
            addressLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold;");
            TextArea addressField = new TextArea();
            addressField.setPromptText("Enter delivery address");
            addressField.setPrefRowCount(2);
            addressField.setPrefWidth(250);
            addressField.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #1a1a1a;");
            Label phoneLabel = new Label("Phone:");
            phoneLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold;");
            TextField phoneField = new TextField();
            phoneField.setPromptText("Enter phone number");
            phoneField.setPrefWidth(250);
            phoneField.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #1a1a1a;");
            deliveryBox.getChildren().addAll(addressLabel, addressField, phoneLabel, phoneField);
            deliveryBox.setVisible(false);
            
            extraFieldsBox.getChildren().addAll(reservationBox, deliveryBox);
            
            dineIn.setOnAction(e -> {
                orderType = "Dine-In";
                reservationBox.setVisible(true);
                deliveryBox.setVisible(false);
            });
            takeaway.setOnAction(e -> {
                orderType = "Takeaway";
                reservationBox.setVisible(false);
                deliveryBox.setVisible(false);
            });
            delivery.setOnAction(e -> {
                orderType = "Delivery";
                reservationBox.setVisible(false);
                deliveryBox.setVisible(true);
            });
            
            topSection.getChildren().addAll(headerBox, orderTypeBox, extraFieldsBox);
            
            // ========== CENTER SECTION: Menu and Cart ==========
            HBox centerContent = new HBox(30);
            centerContent.setAlignment(Pos.CENTER);
            centerContent.setPadding(new Insets(20));
            
            // Menu Section
            VBox menuSection = new VBox(10);
            menuSection.setAlignment(Pos.TOP_CENTER);
            menuSection.setPrefWidth(750);
            menuSection.setMinWidth(750);
            menuSection.setMaxWidth(750);
            
            Label menuTitle = new Label("📜 Royal Menu");
            menuTitle.setFont(new Font("Georgia", 24));
            menuTitle.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold;");
            menuTitle.setPadding(new Insets(0, 0, 10, 0));
            
            ScrollPane menuScrollPane = new ScrollPane();
            menuScrollPane.setPrefWidth(750);
            menuScrollPane.setPrefHeight(550);
            menuScrollPane.setMinWidth(750);
            menuScrollPane.setMaxWidth(750);
            menuScrollPane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.95); -fx-border-color: #ffffff; -fx-border-width: 2; -fx-border-radius: 10;");
            menuScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            menuScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            menuScrollPane.setFitToWidth(true);
            
            VBox menuContainer = new VBox(15);
            menuContainer.setPadding(new Insets(20));
            menuContainer.setAlignment(Pos.TOP_CENTER);
            menuContainer.setPrefWidth(710);
            
            // Load menu items
            menuItems = MenuDAO.getAllMenuItems();
            if (menuItems.isEmpty()) {
                Label noItems = new Label("⚠ No menu items available.\n\nPlease run:\njava -cp \"bin;lib/*\" application.database.InitializeMenuData");
                noItems.setFont(new Font("Georgia", 16));
                noItems.setStyle("-fx-text-fill: #ff6b6b; -fx-font-weight: bold; -fx-text-alignment: center;");
                noItems.setWrapText(true);
                noItems.setPadding(new Insets(30));
                menuContainer.getChildren().add(noItems);
            } else {
                // Sort options
                Label sortLabel = new Label("Sort by:");
                sortLabel.setStyle("-fx-text-fill: #1a1a1a; -fx-font-weight: bold;");
                ComboBox<String> sortCombo = new ComboBox<>(FXCollections.observableArrayList(
                        "Category", "Price (Low to High)", "Price (High to Low)"
                ));
                sortCombo.setValue("Category");
                sortCombo.setStyle("-fx-background-color: #ffffff;");
                HBox sortBox = new HBox(10);
                sortBox.setAlignment(Pos.CENTER);
                sortBox.getChildren().addAll(sortLabel, sortCombo);
                
                menuContainer.getChildren().add(sortBox);
                
                // Group by category
                Map<String, List<MenuItem>> categoryMap = new LinkedHashMap<>();
                for (MenuItem item : menuItems) {
                    categoryMap.computeIfAbsent(item.getCategory(), k -> new ArrayList<>()).add(item);
                }
                
                // Display items
                for (Map.Entry<String, List<MenuItem>> entry : categoryMap.entrySet()) {
                    Label categoryTitle = new Label("━━━ " + entry.getKey() + " ━━━");
                    categoryTitle.setFont(new Font("Georgia", 18));
                    categoryTitle.setStyle("-fx-text-fill: #b8860b; -fx-font-weight: bold;");
                    categoryTitle.setPadding(new Insets(15, 0, 10, 0));
                    menuContainer.getChildren().add(categoryTitle);
                    
                    for (MenuItem item : entry.getValue()) {
                        HBox itemBox = createMenuItemBox(item, null);
                        menuContainer.getChildren().add(itemBox);
                    }
                }
                
                // Sort functionality
                sortCombo.setOnAction(e -> {
                    String sortType = sortCombo.getValue();
                    menuContainer.getChildren().clear();
                    menuContainer.getChildren().add(sortBox);
                    
                    List<MenuItem> sortedItems;
                    if ("Price (Low to High)".equals(sortType)) {
                        sortedItems = MenuDAO.getMenuItemsSortedByPrice(true);
                    } else if ("Price (High to Low)".equals(sortType)) {
                        sortedItems = MenuDAO.getMenuItemsSortedByPrice(false);
                    } else {
                        sortedItems = MenuDAO.getAllMenuItems();
                    }
                    
                    Map<String, List<MenuItem>> sortedCategoryMap = new LinkedHashMap<>();
                    for (MenuItem item : sortedItems) {
                        sortedCategoryMap.computeIfAbsent(item.getCategory(), k -> new ArrayList<>()).add(item);
                    }
                    
                    for (Map.Entry<String, List<MenuItem>> catEntry : sortedCategoryMap.entrySet()) {
                        Label catTitle = new Label("━━━ " + catEntry.getKey() + " ━━━");
                        catTitle.setFont(new Font("Georgia", 18));
                        catTitle.setStyle("-fx-text-fill: #b8860b; -fx-font-weight: bold;");
                        catTitle.setPadding(new Insets(15, 0, 10, 0));
                        menuContainer.getChildren().add(catTitle);
                        
                        for (MenuItem item : catEntry.getValue()) {
                            HBox itemBox = createMenuItemBox(item, null);
                            menuContainer.getChildren().add(itemBox);
                        }
                    }
                });
            }
            
            menuScrollPane.setContent(menuContainer);
            menuSection.getChildren().addAll(menuTitle, menuScrollPane);
            
            // Cart Section
            VBox cartSection = new VBox(15);
            cartSection.setAlignment(Pos.TOP_CENTER);
            cartSection.setPrefWidth(450);
            cartSection.setMinWidth(450);
            cartSection.setMaxWidth(450);
            cartSection.setPadding(new Insets(20));
            cartSection.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 10; -fx-border-color: #ffffff; -fx-border-width: 2; -fx-border-radius: 10;");
            
            Label cartTitle = new Label("🛒 Your Royal Cart");
            cartTitle.setFont(new Font("Georgia", 20));
            cartTitle.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold;");
            
            VBox cartItems = new VBox(8);
            cartItems.setPrefHeight(200);
            ScrollPane cartScrollPane = new ScrollPane(cartItems);
            cartScrollPane.setPrefHeight(200);
            cartScrollPane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-background-radius: 5;");
            cartScrollPane.setFitToWidth(true);
            cartScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            
            Label cartEmpty = new Label("Cart is empty");
            cartEmpty.setStyle("-fx-text-fill: #666666; -fx-font-style: italic;");
            cartItems.getChildren().add(cartEmpty);
            
            TextArea specialRequirements = new TextArea();
            specialRequirements.setPromptText("Special requirements (optional)");
            specialRequirements.setPrefRowCount(3);
            specialRequirements.setPrefWidth(400);
            specialRequirements.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #1a1a1a;");
            
            Label totalLabel = new Label("Total: Rs. 0.00");
            totalLabel.setFont(new Font("Georgia", 18));
            totalLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold;");
            
            Button confirmOrderButton = new Button("✨ Confirm Order");
            confirmOrderButton.setFont(new Font("Georgia", 16));
            confirmOrderButton.setPrefWidth(400);
            confirmOrderButton.setPrefHeight(45);
            confirmOrderButton.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #b8860b; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-color: #b8860b; -fx-border-width: 2; -fx-border-radius: 8;");
            confirmOrderButton.setDisable(true);
            confirmOrderButton.setOnMouseEntered(e -> confirmOrderButton.setStyle("-fx-background-color: #f5f5f5; -fx-text-fill: #b8860b; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-color: #ffd700; -fx-border-width: 2; -fx-border-radius: 8;"));
            confirmOrderButton.setOnMouseExited(e -> confirmOrderButton.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #b8860b; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-color: #b8860b; -fx-border-width: 2; -fx-border-radius: 8;"));
            
            Button backButton = new Button("← Back to Dashboard");
            backButton.setFont(new Font("Georgia", 14));
            backButton.setPrefWidth(400);
            backButton.setPrefHeight(40);
            backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #ffffff; -fx-border-color: #ffffff; -fx-border-width: 2; -fx-border-radius: 8;");
            backButton.setOnMouseEntered(e -> backButton.setStyle("-fx-background-color: rgba(255, 255, 255, 0.2); -fx-text-fill: #ffffff; -fx-border-color: #ffd700; -fx-border-width: 2; -fx-border-radius: 8;"));
            backButton.setOnMouseExited(e -> backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #ffffff; -fx-border-color: #ffffff; -fx-border-width: 2; -fx-border-radius: 8;"));
            backButton.setOnAction(e -> {
                CustomerDashboardView dashboard = new CustomerDashboardView(primaryStage, currentUser);
                primaryStage.setScene(dashboard.createCustomerDashboard());
            });
            
            // Update cart function
            final Runnable[] updateCartRef = new Runnable[1];
            Runnable updateCart = () -> {
                cartItems.getChildren().clear();
                double total = 0;
                if (cart.isEmpty()) {
                    Label emptyLabel = new Label("Cart is empty");
                    emptyLabel.setStyle("-fx-text-fill: #666666; -fx-font-style: italic;");
                    cartItems.getChildren().add(emptyLabel);
                    confirmOrderButton.setDisable(true);
                } else {
                    for (Map.Entry<MenuItem, Integer> entry : cart.entrySet()) {
                        MenuItem item = entry.getKey();
                        int qty = entry.getValue();
                        HBox cartItemBox = new HBox(10);
                        cartItemBox.setPadding(new Insets(5));
                        cartItemBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.5); -fx-background-radius: 5;");
                        
                        VBox itemInfo = new VBox(2);
                        Label itemName = new Label(item.getName() + " x" + qty);
                        itemName.setStyle("-fx-text-fill: #1a1a1a; -fx-font-weight: bold;");
                        Label itemPrice = new Label("Rs. " + String.format("%.2f", item.getPrice() * qty));
                        itemPrice.setStyle("-fx-text-fill: #b8860b;");
                        itemInfo.getChildren().addAll(itemName, itemPrice);
                        
                        Button removeBtn = new Button("✕");
                        removeBtn.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: #ffffff; -fx-background-radius: 5;");
                        removeBtn.setPrefWidth(30);
                        removeBtn.setOnAction(ev -> {
                            cart.remove(item);
                            updateCartRef[0].run();
                        });
                        
                        cartItemBox.getChildren().addAll(itemInfo, removeBtn);
                        HBox.setHgrow(itemInfo, Priority.ALWAYS);
                        cartItems.getChildren().add(cartItemBox);
                        total += item.getPrice() * qty;
                    }
                    confirmOrderButton.setDisable(false);
                }
                totalLabel.setText("Total: Rs. " + String.format("%.2f", total));
            };
            updateCartRef[0] = updateCart;
            
            cartSection.getChildren().addAll(cartTitle, cartScrollPane, specialRequirements, totalLabel, confirmOrderButton, backButton);
            
            // Update menu items to use updateCart
            if (!menuItems.isEmpty()) {
                VBox newMenuContainer = new VBox(15);
                newMenuContainer.setPadding(new Insets(20));
                newMenuContainer.setAlignment(Pos.TOP_CENTER);
                newMenuContainer.setPrefWidth(710);
                
                Label sortLabel = new Label("Sort by:");
                sortLabel.setStyle("-fx-text-fill: #1a1a1a; -fx-font-weight: bold;");
                ComboBox<String> sortCombo = new ComboBox<>(FXCollections.observableArrayList(
                        "Category", "Price (Low to High)", "Price (High to Low)"
                ));
                sortCombo.setValue("Category");
                sortCombo.setStyle("-fx-background-color: #ffffff;");
                HBox sortBox = new HBox(10);
                sortBox.setAlignment(Pos.CENTER);
                sortBox.getChildren().addAll(sortLabel, sortCombo);
                newMenuContainer.getChildren().add(sortBox);
                
                Map<String, List<MenuItem>> categoryMap = new LinkedHashMap<>();
                for (MenuItem item : menuItems) {
                    categoryMap.computeIfAbsent(item.getCategory(), k -> new ArrayList<>()).add(item);
                }
                
                for (Map.Entry<String, List<MenuItem>> entry : categoryMap.entrySet()) {
                    Label categoryTitle = new Label("━━━ " + entry.getKey() + " ━━━");
                    categoryTitle.setFont(new Font("Georgia", 18));
                    categoryTitle.setStyle("-fx-text-fill: #b8860b; -fx-font-weight: bold;");
                    categoryTitle.setPadding(new Insets(15, 0, 10, 0));
                    newMenuContainer.getChildren().add(categoryTitle);
                    
                    for (MenuItem item : entry.getValue()) {
                        HBox itemBox = createMenuItemBox(item, updateCart);
                        newMenuContainer.getChildren().add(itemBox);
                    }
                }
                
                sortCombo.setOnAction(e -> {
                    String sortType = sortCombo.getValue();
                    newMenuContainer.getChildren().clear();
                    newMenuContainer.getChildren().add(sortBox);
                    
                    List<MenuItem> sortedItems;
                    if ("Price (Low to High)".equals(sortType)) {
                        sortedItems = MenuDAO.getMenuItemsSortedByPrice(true);
                    } else if ("Price (High to Low)".equals(sortType)) {
                        sortedItems = MenuDAO.getMenuItemsSortedByPrice(false);
                    } else {
                        sortedItems = MenuDAO.getAllMenuItems();
                    }
                    
                    Map<String, List<MenuItem>> sortedCategoryMap = new LinkedHashMap<>();
                    for (MenuItem item : sortedItems) {
                        sortedCategoryMap.computeIfAbsent(item.getCategory(), k -> new ArrayList<>()).add(item);
                    }
                    
                    for (Map.Entry<String, List<MenuItem>> catEntry : sortedCategoryMap.entrySet()) {
                        Label catTitle = new Label("━━━ " + catEntry.getKey() + " ━━━");
                        catTitle.setFont(new Font("Georgia", 18));
                        catTitle.setStyle("-fx-text-fill: #b8860b; -fx-font-weight: bold;");
                        catTitle.setPadding(new Insets(15, 0, 10, 0));
                        newMenuContainer.getChildren().add(catTitle);
                        
                        for (MenuItem item : catEntry.getValue()) {
                            HBox itemBox = createMenuItemBox(item, updateCart);
                            newMenuContainer.getChildren().add(itemBox);
                        }
                    }
                });
                
                menuScrollPane.setContent(newMenuContainer);
            }
            
            centerContent.getChildren().addAll(menuSection, cartSection);
            
            // Order confirmation handler
            confirmOrderButton.setOnAction(e -> {
                if (cart.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Empty Cart");
                    alert.setHeaderText(null);
                    alert.setContentText("Please add items to your cart before confirming.");
                    alert.showAndWait();
                    return;
                }
                
                if (orderType == null) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Order Type Required");
                    alert.setHeaderText(null);
                    alert.setContentText("Please select an order type.");
                    alert.showAndWait();
                    return;
                }
                
                if ("Dine-In".equals(orderType)) {
                    String resIdStr = reservationField.getText();
                    if (resIdStr.isEmpty()) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Reservation Required");
                        alert.setHeaderText(null);
                        alert.setContentText("Please enter your reservation ID for dine-in orders.");
                        alert.showAndWait();
                        return;
                    }
                    try {
                        int resId = Integer.parseInt(resIdStr);
                        if (ReservationDAO.getReservationById(resId) == null) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Invalid Reservation");
                            alert.setHeaderText(null);
                            alert.setContentText("Reservation ID not found.");
                            alert.showAndWait();
                            return;
                        }
                    } catch (NumberFormatException ex) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid Input");
                        alert.setHeaderText(null);
                        alert.setContentText("Please enter a valid reservation ID.");
                        alert.showAndWait();
                        return;
                    }
                }
                
                if ("Delivery".equals(orderType)) {
                    if (addressField.getText().isEmpty() || phoneField.getText().isEmpty()) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Delivery Details Required");
                        alert.setHeaderText(null);
                        alert.setContentText("Please fill in delivery address and phone number.");
                        alert.showAndWait();
                        return;
                    }
                }
                
                // Create order
                Order order = new Order();
                order.setUserId(currentUser.getUserId());
                if ("Dine-In".equals(orderType) && !reservationField.getText().isEmpty()) {
                    order.setReservationId(Integer.parseInt(reservationField.getText()));
                }
                order.setOrderType(orderType);
                order.setOrderStatus("Receiving Order");
                double total = Double.parseDouble(totalLabel.getText().replace("Total: Rs. ", ""));
                order.setTotalAmount(total);
                order.setPaymentStatus("pending");
                order.setSpecialRequirements(specialRequirements.getText());
                if ("Delivery".equals(orderType)) {
                    order.setDeliveryAddress(addressField.getText());
                    order.setDeliveryPhone(phoneField.getText());
                }
                if ("Takeaway".equals(orderType)) {
                    order.setToken(OrderDAO.generateToken());
                }
                
                int orderId = OrderDAO.createOrder(order);
                if (orderId > 0) {
                    // Add order items
                    for (Map.Entry<MenuItem, Integer> entry : cart.entrySet()) {
                        OrderItem orderItem = new OrderItem();
                        orderItem.setOrderId(orderId);
                        orderItem.setItemId(entry.getKey().getItemId());
                        orderItem.setQuantity(entry.getValue());
                        orderItem.setPrice(entry.getKey().getPrice());
                        orderItem.setItemStatus("pending");
                        OrderDAO.addOrderItem(orderItem);
                    }
                    
                    if ("Takeaway".equals(orderType)) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Order Confirmed");
                        alert.setHeaderText("Your Token: " + order.getToken());
                        alert.setContentText("Please keep this token for order pickup.");
                        alert.showAndWait();
                    }
                    
                    // Navigate to payment
                    PaymentView paymentView = new PaymentView(primaryStage, currentUser, orderId);
                    primaryStage.setScene(paymentView.createPaymentScene());
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Failed to create order. Please try again.");
                    alert.showAndWait();
                }
            });
            
            // Set layout
            root.setTop(topSection);
            root.setCenter(centerContent);
            BorderPane.setAlignment(topSection, Pos.TOP_CENTER);
            BorderPane.setAlignment(centerContent, Pos.CENTER);
            
            Scene scene = new Scene(root, 1400, 900);
            scene.setFill(javafx.scene.paint.Color.web("#d4af37"));
            
            java.net.URL cssUrl = getClass().getResource("/application/resources/application.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }
            
            return scene;
        } catch (Exception e) {
            System.err.println("Error creating Place Order scene: " + e.getMessage());
            e.printStackTrace();
            VBox errorLayout = new VBox(20);
            errorLayout.setAlignment(Pos.CENTER);
            errorLayout.setPadding(new Insets(50));
            Label errorLabel = new Label("Error loading Place Order screen.\n\n" + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 16px;");
            Button backButton = new Button("← Back to Dashboard");
            backButton.setOnAction(ev -> {
                CustomerDashboardView dashboard = new CustomerDashboardView(primaryStage, currentUser);
                primaryStage.setScene(dashboard.createCustomerDashboard());
            });
            errorLayout.getChildren().addAll(errorLabel, backButton);
            Scene errorScene = new Scene(errorLayout, 600, 400);
            errorScene.setFill(javafx.scene.paint.Color.web("#d4af37"));
            return errorScene;
        }
    }
    
    private HBox createMenuItemBox(MenuItem item, Runnable updateCart) {
        HBox itemBox = new HBox(15);
        itemBox.setPadding(new Insets(12));
        itemBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-background-radius: 8; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 8;");
        itemBox.setOnMouseEntered(e -> itemBox.setStyle("-fx-background-color: rgba(255, 255, 255, 1.0); -fx-background-radius: 8; -fx-border-color: #b8860b; -fx-border-width: 2; -fx-border-radius: 8;"));
        itemBox.setOnMouseExited(e -> itemBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-background-radius: 8; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 8;"));
        
        VBox itemInfo = new VBox(5);
        Label itemName = new Label(item.getName());
        itemName.setFont(new Font("Georgia", 16));
        itemName.setStyle("-fx-text-fill: #b8860b; -fx-font-weight: bold;");
        
        Label itemDesc = new Label(item.getDescription());
        itemDesc.setFont(new Font("Georgia", 12));
        itemDesc.setWrapText(true);
        itemDesc.setStyle("-fx-text-fill: #333333;");
        
        Label itemPrice = new Label("Rs. " + String.format("%.2f", item.getPrice()));
        itemPrice.setFont(new Font("Georgia", 14));
        itemPrice.setStyle("-fx-text-fill: #b8860b; -fx-font-weight: bold;");
        
        itemInfo.getChildren().addAll(itemName, itemDesc, itemPrice);
        HBox.setHgrow(itemInfo, Priority.ALWAYS);
        
        HBox quantityBox = new HBox(8);
        quantityBox.setAlignment(Pos.CENTER_RIGHT);
        
        Label qtyLabel = new Label("Qty:");
        qtyLabel.setStyle("-fx-text-fill: #1a1a1a; -fx-font-weight: bold;");
        
        Spinner<Integer> qtySpinner = new Spinner<>(0, 10, 0);
        qtySpinner.setPrefWidth(60);
        qtySpinner.setStyle("-fx-background-color: #ffffff;");
        
        Button addToCartBtn = new Button("➕ Add");
        addToCartBtn.setFont(new Font("Georgia", 12));
        addToCartBtn.setPrefWidth(80);
        addToCartBtn.setPrefHeight(30);
        addToCartBtn.setStyle("-fx-background-color: #b8860b; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-background-radius: 5;");
        addToCartBtn.setOnMouseEntered(e -> addToCartBtn.setStyle("-fx-background-color: #ffd700; -fx-text-fill: #1a1a1a; -fx-font-weight: bold; -fx-background-radius: 5;"));
        addToCartBtn.setOnMouseExited(e -> addToCartBtn.setStyle("-fx-background-color: #b8860b; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-background-radius: 5;"));
        
        addToCartBtn.setOnAction(e -> {
            int qty = qtySpinner.getValue();
            if (qty > 0) {
                cart.put(item, cart.getOrDefault(item, 0) + qty);
                qtySpinner.getValueFactory().setValue(0);
                if (updateCart != null) {
                    updateCart.run();
                }
            }
        });
        
        quantityBox.getChildren().addAll(qtyLabel, qtySpinner, addToCartBtn);
        itemBox.getChildren().addAll(itemInfo, quantityBox);
        
        return itemBox;
    }
}
