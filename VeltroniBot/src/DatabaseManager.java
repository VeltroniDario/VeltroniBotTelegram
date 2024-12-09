import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/fitness_bot";
    private static final String DB_USER = "root"; // Cambia se necessario
    private static final String DB_PASSWORD = ""; // Cambia se necessario

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static void insertAlimento(String nome, float calorie, float proteine, float carboidrati, float grassi) {
        String query = "INSERT INTO alimenti (nome, calorie, proteine, carboidrati, grassi) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE calorie=?, proteine=?, carboidrati=?, grassi=?";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nome);
            stmt.setFloat(2, calorie);
            stmt.setFloat(3, proteine);
            stmt.setFloat(4, carboidrati);
            stmt.setFloat(5, grassi);

            // Per aggiornamento
            stmt.setFloat(6, calorie);
            stmt.setFloat(7, proteine);
            stmt.setFloat(8, carboidrati);
            stmt.setFloat(9, grassi);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
