package application.database;

import application.utils.EncryptionUtil;
import java.sql.*;

public class PaymentDAO {
    public static boolean processPayment(int orderId, double amount, String paymentMethod, 
                                        String cardData, String walletData, String promoCode, double discount) {
        String sql = "INSERT INTO payments (order_id, amount, payment_method, encrypted_card_data, " +
                    "encrypted_wallet_data, promo_code, discount, status) VALUES (?, ?, ?, ?, ?, ?, ?, 'completed')";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, paymentMethod);
            pstmt.setString(4, cardData != null ? EncryptionUtil.encrypt(cardData) : null);
            pstmt.setString(5, walletData != null ? EncryptionUtil.encrypt(walletData) : null);
            pstmt.setString(6, promoCode);
            pstmt.setDouble(7, discount);
            pstmt.executeUpdate();
            
            // Update order payment status
            String updateOrder = "UPDATE orders SET payment_status = 'Paid', payment_method = ? WHERE order_id = ?";
            PreparedStatement pstmt2 = conn.prepareStatement(updateOrder);
            pstmt2.setString(1, paymentMethod);
            pstmt2.setInt(2, orderId);
            pstmt2.executeUpdate();
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Process payment error: " + e.getMessage());
            return false;
        }
    }

    public static boolean refundPayment(int orderId, double refundAmount) {
        String sql = "UPDATE payments SET status = 'refunded' WHERE order_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Refund payment error: " + e.getMessage());
            return false;
        }
    }

    public static boolean clearPaymentData(int orderId) {
        String sql = "UPDATE payments SET encrypted_card_data = NULL, encrypted_wallet_data = NULL WHERE order_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Clear payment data error: " + e.getMessage());
            return false;
        }
    }

    public static double getReservationDeposit(int reservationId) {
        String sql = "SELECT deposit FROM reservations WHERE reservation_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reservationId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("deposit");
            }
        } catch (SQLException e) {
            System.err.println("Get reservation deposit error: " + e.getMessage());
        }
        return 0.0;
    }
}



