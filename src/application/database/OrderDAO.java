package application.database;

import application.models.Order;
import application.models.OrderItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderDAO {
    public static int createOrder(Order order) {
        String sql = "INSERT INTO orders (user_id, reservation_id, order_type, order_status, total_amount, " +
                    "payment_status, delivery_address, delivery_phone, token, special_requirements) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, order.getUserId());
            if (order.getReservationId() != null) {
                pstmt.setInt(2, order.getReservationId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setString(3, order.getOrderType());
            pstmt.setString(4, order.getOrderStatus());
            pstmt.setDouble(5, order.getTotalAmount());
            pstmt.setString(6, order.getPaymentStatus());
            pstmt.setString(7, order.getDeliveryAddress());
            pstmt.setString(8, order.getDeliveryPhone());
            pstmt.setString(9, order.getToken());
            pstmt.setString(10, order.getSpecialRequirements());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Create order error: " + e.getMessage());
        }
        return -1;
    }

    public static boolean addOrderItem(OrderItem item) {
        String sql = "INSERT INTO order_items (order_id, item_id, quantity, price, item_status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, item.getOrderId());
            pstmt.setInt(2, item.getItemId());
            pstmt.setInt(3, item.getQuantity());
            pstmt.setDouble(4, item.getPrice());
            pstmt.setString(5, item.getItemStatus());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Add order item error: " + e.getMessage());
            return false;
        }
    }

    public static Order getOrderById(int orderId) {
        String sql = "SELECT * FROM orders WHERE order_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("order_id"));
                order.setUserId(rs.getInt("user_id"));
                int resId = rs.getInt("reservation_id");
                if (!rs.wasNull()) order.setReservationId(resId);
                order.setOrderType(rs.getString("order_type"));
                order.setOrderStatus(rs.getString("order_status"));
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setPaymentStatus(rs.getString("payment_status"));
                order.setPaymentMethod(rs.getString("payment_method"));
                order.setDeliveryAddress(rs.getString("delivery_address"));
                order.setDeliveryPhone(rs.getString("delivery_phone"));
                order.setToken(rs.getString("token"));
                order.setSpecialRequirements(rs.getString("special_requirements"));
                order.setCreatedAt(rs.getTimestamp("created_at"));
                order.setOrderItems(getOrderItemsByOrderId(orderId));
                return order;
            }
        } catch (SQLException e) {
            System.err.println("Get order by ID error: " + e.getMessage());
        }
        return null;
    }

    public static List<OrderItem> getOrderItemsByOrderId(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT oi.*, mi.name as item_name FROM order_items oi " +
                    "JOIN menu_items mi ON oi.item_id = mi.item_id WHERE oi.order_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setOrderItemId(rs.getInt("order_item_id"));
                item.setOrderId(rs.getInt("order_id"));
                item.setItemId(rs.getInt("item_id"));
                item.setItemName(rs.getString("item_name"));
                item.setQuantity(rs.getInt("quantity"));
                item.setPrice(rs.getDouble("price"));
                item.setItemStatus(rs.getString("item_status"));
                items.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Get order items error: " + e.getMessage());
        }
        return items;
    }

    public static boolean updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE orders SET order_status = ? WHERE order_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, orderId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update order status error: " + e.getMessage());
            return false;
        }
    }

    public static boolean updateOrderItemStatus(int orderItemId, String status) {
        String sql = "UPDATE order_items SET item_status = ? WHERE order_item_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, orderItemId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update order item status error: " + e.getMessage());
            return false;
        }
    }

    public static boolean cancelOrder(int orderId, String reason) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            // Update order status
            String updateOrder = "UPDATE orders SET order_status = 'Cancelled' WHERE order_id = ?";
            PreparedStatement pstmt1 = conn.prepareStatement(updateOrder);
            pstmt1.setInt(1, orderId);
            pstmt1.executeUpdate();
            
            // Log cancellation
            String insertCancel = "INSERT INTO cancellations (order_id, reason) VALUES (?, ?)";
            PreparedStatement pstmt2 = conn.prepareStatement(insertCancel);
            pstmt2.setInt(1, orderId);
            pstmt2.setString(2, reason);
            pstmt2.executeUpdate();
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Cancel order error: " + e.getMessage());
            return false;
        }
    }

    public static String generateToken() {
        return "SH" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public static List<Order> getOrdersByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("order_id"));
                order.setUserId(rs.getInt("user_id"));
                int resId = rs.getInt("reservation_id");
                if (!rs.wasNull()) order.setReservationId(resId);
                order.setOrderType(rs.getString("order_type"));
                order.setOrderStatus(rs.getString("order_status"));
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setPaymentStatus(rs.getString("payment_status"));
                order.setCreatedAt(rs.getTimestamp("created_at"));
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Get orders by user error: " + e.getMessage());
        }
        return orders;
    }
    
    public static List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("order_id"));
                order.setUserId(rs.getInt("user_id"));
                int resId = rs.getInt("reservation_id");
                if (!rs.wasNull()) order.setReservationId(resId);
                order.setOrderType(rs.getString("order_type"));
                order.setOrderStatus(rs.getString("order_status"));
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setPaymentStatus(rs.getString("payment_status"));
                order.setCreatedAt(rs.getTimestamp("created_at"));
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Get all orders error: " + e.getMessage());
        }
        return orders;
    }
}



