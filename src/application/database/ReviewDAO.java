package application.database;

import application.models.Review;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {
    public static boolean createReview(Review review) {
        String sql = "INSERT INTO reviews (order_id, user_id, customer_name, contact_phone, contact_email, " +
                    "ambiance_rating, food_rating, service_rating, overall_rating, written_feedback) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, review.getOrderId());
            if (review.getUserId() != null) {
                pstmt.setInt(2, review.getUserId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setString(3, review.getCustomerName());
            pstmt.setString(4, review.getContactPhone());
            pstmt.setString(5, review.getContactEmail());
            pstmt.setInt(6, review.getAmbianceRating());
            pstmt.setInt(7, review.getFoodRating());
            pstmt.setInt(8, review.getServiceRating());
            pstmt.setDouble(9, review.getOverallRating());
            pstmt.setString(10, review.getWrittenFeedback());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Create review error: " + e.getMessage());
            return false;
        }
    }

    public static boolean hasReviewForOrder(int orderId) {
        String sql = "SELECT COUNT(*) FROM reviews WHERE order_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Check review exists error: " + e.getMessage());
        }
        return false;
    }

    public static List<Review> getAllReviews() {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM reviews ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Review review = new Review();
                review.setReviewId(rs.getInt("review_id"));
                review.setOrderId(rs.getInt("order_id"));
                int userId = rs.getInt("user_id");
                if (!rs.wasNull()) review.setUserId(userId);
                review.setCustomerName(rs.getString("customer_name"));
                review.setContactPhone(rs.getString("contact_phone"));
                review.setContactEmail(rs.getString("contact_email"));
                review.setAmbianceRating(rs.getInt("ambiance_rating"));
                review.setFoodRating(rs.getInt("food_rating"));
                review.setServiceRating(rs.getInt("service_rating"));
                review.setOverallRating(rs.getDouble("overall_rating"));
                review.setWrittenFeedback(rs.getString("written_feedback"));
                review.setCreatedAt(rs.getTimestamp("created_at"));
                reviews.add(review);
            }
        } catch (SQLException e) {
            System.err.println("Get all reviews error: " + e.getMessage());
        }
        return reviews;
    }

    public static List<Review> filterReviewsByDateRange(Date startDate, Date endDate) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM reviews WHERE DATE(created_at) BETWEEN ? AND ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, startDate);
            pstmt.setDate(2, endDate);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Review review = new Review();
                review.setReviewId(rs.getInt("review_id"));
                review.setOrderId(rs.getInt("order_id"));
                int userId = rs.getInt("user_id");
                if (!rs.wasNull()) review.setUserId(userId);
                review.setCustomerName(rs.getString("customer_name"));
                review.setContactPhone(rs.getString("contact_phone"));
                review.setContactEmail(rs.getString("contact_email"));
                review.setAmbianceRating(rs.getInt("ambiance_rating"));
                review.setFoodRating(rs.getInt("food_rating"));
                review.setServiceRating(rs.getInt("service_rating"));
                review.setOverallRating(rs.getDouble("overall_rating"));
                review.setWrittenFeedback(rs.getString("written_feedback"));
                review.setCreatedAt(rs.getTimestamp("created_at"));
                reviews.add(review);
            }
        } catch (SQLException e) {
            System.err.println("Filter reviews by date error: " + e.getMessage());
        }
        return reviews;
    }
}



