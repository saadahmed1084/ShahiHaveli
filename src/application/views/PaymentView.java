package application.views;

import application.database.OrderDAO;
import application.database.PaymentDAO;
import application.database.ReservationDAO;
import application.models.Order;
import application.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class PaymentView {
    private Stage primaryStage;
    private User currentUser;
    private int orderId;

    public PaymentView(Stage primaryStage, User currentUser, int orderId) {
        this.primaryStage = primaryStage;
        this.currentUser = currentUser;
        this.orderId = orderId;
    }

    public Scene createPaymentScene() {
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.CENTER);

        Label title = new Label("Payment Processing");
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

        // Calculate total with discount
        final double originalTotal = order.getTotalAmount();
        double deposit = 0;
        if (order.getReservationId() != null) {
            deposit = PaymentDAO.getReservationDeposit(order.getReservationId());
        }
        final double totalAmount = originalTotal - deposit;

        Label amountLabel = new Label("Total Amount: Rs. " + String.format("%.2f", order.getTotalAmount()));
        if (deposit > 0) {
            Label depositLabel = new Label("Reservation Deposit: -Rs. " + String.format("%.2f", deposit));
            Label finalLabel = new Label("Amount to Pay: Rs. " + String.format("%.2f", totalAmount));
            mainLayout.getChildren().addAll(amountLabel, depositLabel, finalLabel);
        } else {
            mainLayout.getChildren().add(amountLabel);
        }

        // Promo code
        Label promoLabel = new Label("Promo Code (optional):");
        TextField promoField = new TextField();
        promoField.setMaxWidth(200);
        Button applyPromoButton = new Button("Apply");
        HBox promoBox = new HBox(10, promoLabel, promoField, applyPromoButton);
        promoBox.setAlignment(Pos.CENTER);

        java.util.concurrent.atomic.AtomicReference<Double> finalAmountRef = new java.util.concurrent.atomic.AtomicReference<>(totalAmount);
        Label discountLabel = new Label();
        applyPromoButton.setOnAction(e -> {
            String promo = promoField.getText();
            // Simple promo code logic (can be enhanced)
            if ("SHAHI10".equals(promo)) {
                finalAmountRef.set(totalAmount * 0.9);
                discountLabel.setText("Discount Applied: 10%");
                discountLabel.setStyle("-fx-text-fill: lightgreen;");
            } else if (!promo.isEmpty()) {
                discountLabel.setText("Invalid promo code");
                discountLabel.setStyle("-fx-text-fill: #ff4c4c;");
            }
        });

        // Payment method
        Label paymentMethodLabel = new Label("Select Payment Method:");
        ToggleGroup paymentGroup = new ToggleGroup();
        RadioButton cash = new RadioButton("Cash");
        RadioButton card = new RadioButton("Card");
        RadioButton wallet = new RadioButton("E-Wallet");
        cash.setToggleGroup(paymentGroup);
        card.setToggleGroup(paymentGroup);
        wallet.setToggleGroup(paymentGroup);
        cash.setSelected(true);

        VBox paymentMethodBox = new VBox(10, paymentMethodLabel, cash, card, wallet);
        paymentMethodBox.setAlignment(Pos.CENTER_LEFT);

        // Card details
        VBox cardDetails = new VBox(10);
        Label cardNumberLabel = new Label("Card Number:");
        TextField cardNumberField = new TextField();
        cardNumberField.setPromptText("1234 5678 9012 3456");
        cardNumberField.setMaxWidth(250);
        Label cvvLabel = new Label("CVV:");
        TextField cvvField = new TextField();
        cvvField.setPromptText("123");
        cvvField.setMaxWidth(100);
        cardDetails.getChildren().addAll(cardNumberLabel, cardNumberField, cvvLabel, cvvField);
        cardDetails.setVisible(false);

        // Wallet details
        VBox walletDetails = new VBox(10);
        Label walletNumberLabel = new Label("Wallet Number:");
        TextField walletNumberField = new TextField();
        walletNumberField.setPromptText("Enter wallet number");
        walletNumberField.setMaxWidth(250);
        walletDetails.getChildren().addAll(walletNumberLabel, walletNumberField);
        walletDetails.setVisible(false);

        card.setOnAction(e -> {
            cardDetails.setVisible(true);
            walletDetails.setVisible(false);
        });
        wallet.setOnAction(e -> {
            walletDetails.setVisible(true);
            cardDetails.setVisible(false);
        });
        cash.setOnAction(e -> {
            cardDetails.setVisible(false);
            walletDetails.setVisible(false);
        });

        Button processPaymentButton = new Button("Process Payment");
        processPaymentButton.getStyleClass().add("login-button");

        Button printReceiptButton = new Button("Print Receipt");
        printReceiptButton.getStyleClass().add("register-button");
        printReceiptButton.setVisible(false);

        Label messageLabel = new Label();

        processPaymentButton.setOnAction(e -> {
            String paymentMethod = cash.isSelected() ? "Cash" : (card.isSelected() ? "Card" : "E-Wallet");
            String cardData = null;
            String walletData = null;

            if ("Card".equals(paymentMethod)) {
                if (cardNumberField.getText().isEmpty() || cvvField.getText().isEmpty()) {
                    messageLabel.setText("Please fill in card details");
                    messageLabel.setStyle("-fx-text-fill: #ff4c4c;");
                    return;
                }
                cardData = cardNumberField.getText() + "|" + cvvField.getText();
            } else if ("E-Wallet".equals(paymentMethod)) {
                if (walletNumberField.getText().isEmpty()) {
                    messageLabel.setText("Please enter wallet number");
                    messageLabel.setStyle("-fx-text-fill: #ff4c4c;");
                    return;
                }
                walletData = walletNumberField.getText();
            }

            // Process payment
            String promoCode = promoField.getText().isEmpty() ? null : promoField.getText();
            double finalAmount = finalAmountRef.get();
            double discount = totalAmount - finalAmount;
            if (PaymentDAO.processPayment(orderId, finalAmount, paymentMethod, cardData, walletData, promoCode, discount)) {
                messageLabel.setText("Payment successful! Order confirmed.");
                messageLabel.setStyle("-fx-text-fill: lightgreen;");
                processPaymentButton.setDisable(true);
                printReceiptButton.setVisible(true);

                // Navigate to track order after a delay
                javafx.application.Platform.runLater(() -> {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    javafx.application.Platform.runLater(() -> {
                        TrackOrderView trackOrderView = new TrackOrderView(primaryStage, currentUser);
                        primaryStage.setScene(trackOrderView.createTrackOrderScene());
                    });
                });
            } else {
                messageLabel.setText("Payment failed. Please try again.");
                messageLabel.setStyle("-fx-text-fill: #ff4c4c;");
            }
        });

        printReceiptButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Receipt");
            alert.setHeaderText("Order #" + orderId);
            double finalAmount = finalAmountRef.get();
            alert.setContentText("Amount Paid: Rs. " + String.format("%.2f", finalAmount) + "\n" +
                    "Payment Method: " + (cash.isSelected() ? "Cash" : (card.isSelected() ? "Card" : "E-Wallet")));
            alert.showAndWait();
        });

        mainLayout.getChildren().addAll(title, promoBox, discountLabel, paymentMethodBox,
                cardDetails, walletDetails, processPaymentButton, printReceiptButton, messageLabel);

        Scene scene = new Scene(mainLayout, 600, 700);
        scene.setFill(javafx.scene.paint.Color.web("#d4af37")); // Gold background
        scene.getStylesheets().add(getClass().getResource("/application/resources/application.css").toExternalForm());
        return scene;
    }
}



