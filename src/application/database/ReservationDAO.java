package application.database;

import application.models.Reservation;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {
    public static Reservation getReservationById(int reservationId) {
        String sql = "SELECT * FROM reservations WHERE reservation_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reservationId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Reservation res = new Reservation();
                res.setReservationId(rs.getInt("reservation_id"));
                int userId = rs.getInt("user_id");
                if (!rs.wasNull()) res.setUserId(userId);
                res.setCustomerName(rs.getString("customer_name"));
                res.setPhone(rs.getString("phone"));
                res.setReservationDate(rs.getDate("reservation_date"));
                res.setReservationTime(rs.getTime("reservation_time"));
                res.setPartySize(rs.getInt("party_size"));
                res.setStatus(rs.getString("status"));
                res.setDeposit(rs.getDouble("deposit"));
                res.setCreatedAt(rs.getTimestamp("created_at"));
                return res;
            }
        } catch (SQLException e) {
            System.err.println("Get reservation error: " + e.getMessage());
        }
        return null;
    }

    public static List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations ORDER BY reservation_date DESC, reservation_time DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Reservation res = new Reservation();
                res.setReservationId(rs.getInt("reservation_id"));
                int userId = rs.getInt("user_id");
                if (!rs.wasNull()) res.setUserId(userId);
                res.setCustomerName(rs.getString("customer_name"));
                res.setPhone(rs.getString("phone"));
                res.setReservationDate(rs.getDate("reservation_date"));
                res.setReservationTime(rs.getTime("reservation_time"));
                res.setPartySize(rs.getInt("party_size"));
                res.setStatus(rs.getString("status"));
                res.setDeposit(rs.getDouble("deposit"));
                res.setCreatedAt(rs.getTimestamp("created_at"));
                reservations.add(res);
            }
        } catch (SQLException e) {
            System.err.println("Get all reservations error: " + e.getMessage());
        }
        return reservations;
    }

    public static List<Reservation> getPendingReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE status = 'pending' ORDER BY created_at ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Reservation res = new Reservation();
                res.setReservationId(rs.getInt("reservation_id"));
                int userId = rs.getInt("user_id");
                if (!rs.wasNull()) res.setUserId(userId);
                res.setCustomerName(rs.getString("customer_name"));
                res.setPhone(rs.getString("phone"));
                res.setReservationDate(rs.getDate("reservation_date"));
                res.setReservationTime(rs.getTime("reservation_time"));
                res.setPartySize(rs.getInt("party_size"));
                res.setStatus(rs.getString("status"));
                res.setDeposit(rs.getDouble("deposit"));
                res.setCreatedAt(rs.getTimestamp("created_at"));
                reservations.add(res);
            }
        } catch (SQLException e) {
            System.err.println("Get pending reservations error: " + e.getMessage());
        }
        return reservations;
    }

    public static boolean updateReservationStatus(int reservationId, String status) {
        String sql = "UPDATE reservations SET status = ? WHERE reservation_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, reservationId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update reservation status error: " + e.getMessage());
            return false;
        }
    }

    public static boolean createReservation(Reservation reservation) {
        String sql = "INSERT INTO reservations (user_id, customer_name, phone, reservation_date, " +
                    "reservation_time, party_size, status, deposit) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (reservation.getUserId() != null) {
                pstmt.setInt(1, reservation.getUserId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }
            pstmt.setString(2, reservation.getCustomerName());
            pstmt.setString(3, reservation.getPhone());
            pstmt.setDate(4, reservation.getReservationDate());
            pstmt.setTime(5, reservation.getReservationTime());
            pstmt.setInt(6, reservation.getPartySize());
            pstmt.setString(7, reservation.getStatus());
            pstmt.setDouble(8, reservation.getDeposit());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Create reservation error: " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteReservation(int reservationId) {
        String sql = "DELETE FROM reservations WHERE reservation_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reservationId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Delete reservation error: " + e.getMessage());
            return false;
        }
    }
}



