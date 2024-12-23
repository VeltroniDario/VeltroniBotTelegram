import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

//classe per gestire interamente tutte le connessioni e query da fare al database

public class DatabaseManager {
    //dati del database
    private static final String DB_URL = "jdbc:mysql://localhost:3306/myprotein_scraper";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    //ottenere la connessione
    public Connection connect() throws Exception {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }


    //funzione per inserire tutti i dati passati nelle tabelle
    public void insertProduct(String marca, String nomeProdotto, double prezzo, String immaginePath, String linkPagina, NutritionalValues valoriNutritivi) {

        String insertValoriNutritiviQuery = "INSERT INTO valoriNutritivi (energia100g, energiaDose, grassi100g, grassiDose, saturi100g, saturiDose, carboidrati100g, carboidratiDose, zuccheri100g, zuccheriDose, proteine100g, proteineDose, sale100g, saleDose) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String insertProdottoQuery = "INSERT INTO prodotti (marca, nome_prodotto, prezzo, immagine, link_pagina, id_valori_nutritivi) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = connect()) {

            // Inserimento dei dati nella tabella valoriNutritivi
            try (PreparedStatement valoriNutritiviStmt = connection.prepareStatement(insertValoriNutritiviQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                valoriNutritiviStmt.setDouble(1, valoriNutritivi.getEnergia100g());
                valoriNutritiviStmt.setDouble(2, valoriNutritivi.getEnergiaDose());
                valoriNutritiviStmt.setDouble(3, valoriNutritivi.getGrassi100g());
                valoriNutritiviStmt.setDouble(4, valoriNutritivi.getGrassiDose());
                valoriNutritiviStmt.setDouble(5, valoriNutritivi.getSaturi100g());
                valoriNutritiviStmt.setDouble(6, valoriNutritivi.getSaturiDose());
                valoriNutritiviStmt.setDouble(7, valoriNutritivi.getCarboidrati100g());
                valoriNutritiviStmt.setDouble(8, valoriNutritivi.getCarboidratiDose());
                valoriNutritiviStmt.setDouble(9, valoriNutritivi.getZuccheri100g());
                valoriNutritiviStmt.setDouble(10, valoriNutritivi.getZuccheriDose());
                valoriNutritiviStmt.setDouble(11, valoriNutritivi.getProteine100g());
                valoriNutritiviStmt.setDouble(12, valoriNutritivi.getProteineDose());
                valoriNutritiviStmt.setDouble(13, valoriNutritivi.getSale100g());
                valoriNutritiviStmt.setDouble(14, valoriNutritivi.getSaleDose());
                valoriNutritiviStmt.executeUpdate();

                int idValoriNutritivi; //per generare l'id della righa dei valori nutritivi che sarà collegata ad un prodotto nella tabella dei prodotti
                try (ResultSet generatedKeys = valoriNutritiviStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {

                        idValoriNutritivi = generatedKeys.getInt(1);
                    } else {
                        throw new Exception("Errore --> nessun id generato.");

                    }
                }

                // Inserimento dei dati nella tabella prodotti
                try (PreparedStatement prodottoStmt = connection.prepareStatement(insertProdottoQuery)) {
                    prodottoStmt.setString(1, marca);
                    prodottoStmt.setString(2, nomeProdotto);
                    prodottoStmt.setDouble(3, prezzo);

                    File file = new File(immaginePath);
                    if (!file.exists() || !file.canRead()) {
                        throw new Exception("Errore --> Il file immagine non esiste o non è leggibile: " + immaginePath);
                    }

                    try (FileInputStream fis = new FileInputStream(file)) {
                        prodottoStmt.setBlob(4, fis);
                        prodottoStmt.setString(5, linkPagina);

                        prodottoStmt.setInt(6, idValoriNutritivi);
                        prodottoStmt.executeUpdate();

                    }
                    // Dopo aver caricato l'immagine nel database, viene cancellata dalla cartella (perchè non serve più)
                    if (!file.delete()) {
                        System.err.println("Errore durante l'eliminazione dell'immagine: " + immaginePath);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //funzione per ottenere tutte le informazioni di un prodotto
    public String getProductInfoByName( String nomeProdotto) throws Exception {

        try (Connection conn = connect()) {
            String query = "SELECT p.marca, p.nome_prodotto, p.prezzo, p.link_pagina, " + "v.energia100g, v.energiaDose, v.grassi100g, v.grassiDose, " + "v.saturi100g, v.saturiDose, v.carboidrati100g, v.carboidratiDose, " + "v.zuccheri100g, v.zuccheriDose, v.proteine100g, v.proteineDose, " + "v.sale100g, v.saleDose " + "FROM prodotti p " + "JOIN valoriNutritivi v ON p.id_valori_nutritivi = v.id " + "WHERE p.nome_prodotto LIKE ?";

            StringBuilder result = new StringBuilder();

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, "%" + nomeProdotto + "%");
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        result.append("Marca: ").append(rs.getString("marca")).append("\n");
                        result.append("Nome Prodotto: ").append(rs.getString("nome_prodotto")).append("\n");
                        result.append("Prezzo: ").append(rs.getDouble("prezzo")).append("\u20ac\n");
                        result.append("Link: ").append(rs.getString("link_pagina")).append("\n");
                        result.append("\nValori Nutritivi:\n");

                        result.append("  Energia (100g): ").append(rs.getInt("energia100g")).append(" kcal\n");
                        result.append("  Energia (dose): ").append(rs.getInt("energiaDose")).append(" kcal\n");
                        result.append("  Grassi (100g): ").append(rs.getDouble("grassi100g")).append(" g\n");
                        result.append("  Grassi (dose): ").append(rs.getDouble("grassiDose")).append(" g\n");
                        result.append("  Saturi (100g): ").append(rs.getDouble("saturi100g")).append(" g\n");
                        result.append("  Saturi (dose): ").append(rs.getDouble("saturiDose")).append(" g\n");
                        result.append("  Carboidrati (100g): ").append(rs.getDouble("carboidrati100g")).append(" g\n");
                        result.append("  Carboidrati (dose): ").append(rs.getDouble("carboidratiDose")).append(" g\n");
                        result.append("  Zuccheri (100g): ").append(rs.getDouble("zuccheri100g")).append(" g\n");
                        result.append("  Zuccheri (dose): ").append(rs.getDouble("zuccheriDose")).append(" g\n");
                        result.append("  Proteine (100g): ").append(rs.getDouble("proteine100g")).append(" g\n");
                        result.append("  Proteine (dose): ").append(rs.getDouble("proteineDose")).append(" g\n");
                        result.append("  Sale (100g): ").append(rs.getDouble("sale100g")).append(" g\n");
                        result.append("  Sale (dose): ").append(rs.getDouble("saleDose")).append(" g\n");

                    } else {
                        result.append("Errore --> Nessun prodotto trovato con il nome: ").append(nomeProdotto);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    result.append("Errore durante il recupero delle informazioni del prodotto.");
                }
            }catch (Exception e) {
                e.printStackTrace();
                result.append("Errore durante il recupero delle informazioni del prodotto.");
            }

        return result.toString();
    }
    }

    //funzione per ottenere la lista dei prodotti con il loro prezzo
    public Map<String, Double> getAllProductsWithPrices() throws Exception {
        Map<String, Double> products = new HashMap<>();
        String query = "SELECT nome_prodotto, prezzo FROM prodotti";
        try (Connection conn = connect()) {
            try (PreparedStatement stmt = conn.prepareStatement(query);

                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {

                    String nomeProdotto = rs.getString("nome_prodotto");
                    double prezzo = rs.getDouble("prezzo");
                    products.put(nomeProdotto, prezzo);
                }
            }
        }return products;
    }
}
