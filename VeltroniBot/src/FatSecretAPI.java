import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Scanner;
import org.json.JSONObject;

public class FatSecretAPI {

    // Metodo per ottenere l'access token
    public static String getAccessToken(String clientId, String clientSecret) {
        try {
            String tokenUrl = "https://oauth.fatsecret.com/connect/token";
            String credentials = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());

            URL url = new URL(tokenUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "Basic " + credentials);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String body = "grant_type=client_credentials&scope=basic";
            OutputStream os = connection.getOutputStream();
            os.write(body.getBytes());
            os.flush();
            os.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                Scanner scanner = new Scanner(connection.getInputStream());
                StringBuilder response = new StringBuilder();
                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();
                return response.toString(); // Restituisce la risposta completa
            } else {
                return "Errore nella richiesta: " + responseCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Errore: " + e.getMessage();
        }
    }

    // Metodo per cercare il cibo
    public static String searchFood(String accessToken, String foodName) {
        try {
            String apiUrl = "https://platform.fatsecret.com/rest/server.api";

            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String body = "method=foods.search&search_expression=" + foodName + "&format=json";
            OutputStream os = connection.getOutputStream();
            os.write(body.getBytes());
            os.flush();
            os.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                Scanner scanner = new Scanner(connection.getInputStream());
                StringBuilder response = new StringBuilder();
                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();
                return response.toString();
            } else {
                return "Errore nella richiesta: " + responseCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Errore: " + e.getMessage();
        }
    }

    // Metodo per estrarre il token dalla risposta JSON
    public static String extractAccessToken(String jsonResponse) {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        return jsonObject.getString("access_token");
    }
}
