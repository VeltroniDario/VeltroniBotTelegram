import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class FatSecretAPI {

    // funzione standard per ottenere l'access token dell'API
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

    // funzione per ottenere informazioni di un cibo
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
                return "Errore -->: " + responseCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Errore --> " + e.getMessage();
        }
    }

    // funzione per estrarre il token dalla risposta JSON
    public static String extractAccessToken(String jsonResponse) {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        return jsonObject.getString("access_token");
    }





    //funzione per prendere tutti i dati dal file json
    public static List<Map<String, String>> parseFoodJson(String jsonResponse) {
        List<Map<String, String>> foodList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONObject foodsObject = jsonObject.getJSONObject("foods");
            JSONArray foodArray = foodsObject.getJSONArray("food");

            // Itera attraverso gli oggetti nell'array "food"
            for (int i = 0; i < foodArray.length(); i++) {
                JSONObject foodItem = foodArray.getJSONObject(i);
                Map<String, String> foodData = new HashMap<>();

                // Inserisci i dati nel map
                foodData.put("food_id", foodItem.getString("food_id"));
                foodData.put("food_name", foodItem.getString("food_name"));
                foodData.put("food_type", foodItem.getString("food_type"));
                foodData.put("food_description", foodItem.getString("food_description"));
                foodData.put("food_url", foodItem.getString("food_url"));

                // Aggiungi il map alla lista
                foodList.add(foodData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return foodList;
    }

    // funzione per stampare il dati
    public static String getFoodDetails(List<Map<String, String>> foodList, String food) {
        if (foodList.isEmpty()) {
            return "No food items available.";
        }

        for (Map<String, String> foodItem : foodList) {
            if (foodItem.containsKey("food_name") && foodItem.get("food_name").equalsIgnoreCase(food)) {
                StringBuilder details = new StringBuilder();
                for (Map.Entry<String, String> entry : foodItem.entrySet()) {
                    details.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                }
                return details.toString();
            }
        }

        return "Food item not found. Try to write the food's name more specifically (es. No pizza, yes pizza margherita)\n Write in English";
    }


}
