import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/myprotein_scraper";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public Connection connect() throws Exception {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public void insertProduct(String name, String price, String description, String category, String site, String productUrl) throws Exception {
        String sql = "INSERT INTO products (name, price, description, category, site, product_url) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, price);
            stmt.setString(3, description);
            stmt.setString(4, category);
            stmt.setString(5, site);
            stmt.setString(6, productUrl);
            stmt.executeUpdate();
        }
    }
}
