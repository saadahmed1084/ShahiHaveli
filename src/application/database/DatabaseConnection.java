package application.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/shahi_haveli";
private static final String DB_USER = "shahi_user";
private static final String DB_PASSWORD = "Abdulrafay12"; // ← Change this

    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // Create Users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "user_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50) UNIQUE NOT NULL, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "role VARCHAR(20) NOT NULL, " +
                    "email VARCHAR(100), " +
                    "phone VARCHAR(20), " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            // Create Menu Items table
            stmt.execute("CREATE TABLE IF NOT EXISTS menu_items (" +
                    "item_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "description TEXT, " +
                    "category VARCHAR(50) NOT NULL, " +
                    "price DECIMAL(10,2) NOT NULL, " +
                    "is_available BOOLEAN DEFAULT TRUE, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            // Create Reservations table
            stmt.execute("CREATE TABLE IF NOT EXISTS reservations (" +
                    "reservation_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_id INT, " +
                    "customer_name VARCHAR(100) NOT NULL, " +
                    "phone VARCHAR(20) NOT NULL, " +
                    "reservation_date DATE NOT NULL, " +
                    "reservation_time TIME NOT NULL, " +
                    "party_size INT NOT NULL, " +
                    "status VARCHAR(20) DEFAULT 'pending', " +
                    "deposit DECIMAL(10,2) DEFAULT 0, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (user_id) REFERENCES users(user_id))");

            // Create Orders table
            stmt.execute("CREATE TABLE IF NOT EXISTS orders (" +
                    "order_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_id INT, " +
                    "reservation_id INT, " +
                    "order_type VARCHAR(20) NOT NULL, " +
                    "order_status VARCHAR(50) DEFAULT 'Receiving Order', " +
                    "total_amount DECIMAL(10,2) NOT NULL, " +
                    "payment_status VARCHAR(20) DEFAULT 'pending', " +
                    "payment_method VARCHAR(20), " +
                    "delivery_address TEXT, " +
                    "delivery_phone VARCHAR(20), " +
                    "token VARCHAR(20), " +
                    "special_requirements TEXT, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (user_id) REFERENCES users(user_id), " +
                    "FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id))");

            // Create Order Items table
            stmt.execute("CREATE TABLE IF NOT EXISTS order_items (" +
                    "order_item_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "order_id INT NOT NULL, " +
                    "item_id INT NOT NULL, " +
                    "quantity INT NOT NULL, " +
                    "price DECIMAL(10,2) NOT NULL, " +
                    "item_status VARCHAR(20) DEFAULT 'pending', " +
                    "FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (item_id) REFERENCES menu_items(item_id))");

            // Create Payments table
            stmt.execute("CREATE TABLE IF NOT EXISTS payments (" +
                    "payment_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "order_id INT NOT NULL, " +
                    "amount DECIMAL(10,2) NOT NULL, " +
                    "payment_method VARCHAR(20) NOT NULL, " +
                    "encrypted_card_data TEXT, " +
                    "encrypted_wallet_data TEXT, " +
                    "promo_code VARCHAR(20), " +
                    "discount DECIMAL(10,2) DEFAULT 0, " +
                    "status VARCHAR(20) DEFAULT 'pending', " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (order_id) REFERENCES orders(order_id))");

            // Create Reviews table
            stmt.execute("CREATE TABLE IF NOT EXISTS reviews (" +
                    "review_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "order_id INT NOT NULL, " +
                    "user_id INT, " +
                    "customer_name VARCHAR(100), " +
                    "contact_phone VARCHAR(20), " +
                    "contact_email VARCHAR(100), " +
                    "ambiance_rating INT CHECK (ambiance_rating BETWEEN 1 AND 5), " +
                    "food_rating INT CHECK (food_rating BETWEEN 1 AND 5), " +
                    "service_rating INT CHECK (service_rating BETWEEN 1 AND 5), " +
                    "overall_rating DECIMAL(3,2), " +
                    "written_feedback TEXT, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (order_id) REFERENCES orders(order_id), " +
                    "FOREIGN KEY (user_id) REFERENCES users(user_id))");

            // Create Cancellations table
            stmt.execute("CREATE TABLE IF NOT EXISTS cancellations (" +
                    "cancellation_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "order_id INT NOT NULL, " +
                    "reason VARCHAR(100) NOT NULL, " +
                    "refund_amount DECIMAL(10,2), " +
                    "refund_status VARCHAR(20) DEFAULT 'pending', " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (order_id) REFERENCES orders(order_id))");

            System.out.println("Database initialized successfully!");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}



