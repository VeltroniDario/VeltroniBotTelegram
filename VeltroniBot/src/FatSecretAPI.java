import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class FatSecretAPI {
    // Inserisci qui la tua chiave API e il segreto
    private static final String API_KEY = "fe63e6d03d6744c5825a9377948f3e4c";
    private static final String API_SECRET = "99651d6c03854222989d5475b4472444";

    // Endpoint base di FatSecret per le richieste
    private static final String BASE_URL = "https://platform.fatsecret.com/rest/server.api";

    public static void main(String[] args) throws IOException {
        // Cibo da cercare (puoi cambiare con qualsiasi nome di cibo)
        String foodName = "apple";

        // Invia la richiesta all'API di FatSecret per cercare il cibo
        String response = getFoodInformation(foodName);

        // Stampa la risposta JSON
        System.out.println(response);

        // Analizza la risposta JSON
        JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
        // Esegui il parsing dei dati come preferisci
        System.out.println("Dati nutrizionali per: " + foodName);
        System.out.println(jsonResponse);
    }

    // Funzione per inviare la richiesta all'API e ottenere la risposta
    private static String getFoodInformation(String foodName) throws IOException {
        // Crea il client HTTP
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            // Crea la richiesta POST
            HttpPost request = new HttpPost(BASE_URL);

            // Parametri da inviare nella richiesta (modifica secondo le specifiche dell'API)
            String params = "method=foods.search&search_expression=" + foodName + "&api_key=" + API_KEY + "&format=json";

            // Aggiungi i parametri nel corpo della richiesta
            request.setEntity(new org.apache.http.entity.StringEntity(params));

            // Esegui la richiesta e ottieni la risposta
            HttpEntity entity = client.execute(request).getEntity();
            String responseString = EntityUtils.toString(entity);

            return responseString;
        }
    }
}

