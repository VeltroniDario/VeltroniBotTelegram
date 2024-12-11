import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        /*try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new FitnessBot());
            System.out.println("Bot avviato!");
        } catch (Exception e) {
            e.printStackTrace();
        }*/


        WebScraper scraper = new WebScraper();

        // Iniziamo il processo di scraping
        try {
            scraper.startScraping();

        } catch (Exception e) {
            System.err.println("Errore durante lo scraping: " + e.getMessage());
        }


        /*String clientId = "fe63e6d03d6744c5825a9377948f3e4c";
        String clientSecret = "99651d6c03854222989d5475b4472444";

        // Ottenere il token
        String accessTokenResponse = FatSecretAPI.getAccessToken(clientId, clientSecret);
        System.out.println("Risultato ottenuto dal server: " + accessTokenResponse);

        // Estrarre l'access token
        String accessToken = FatSecretAPI.extractAccessToken(accessTokenResponse);

        // Cercare un cibo
        String foodName = "bread";
        String response = FatSecretAPI.searchFood(accessToken, foodName);
        System.out.println("Risultato della ricerca: " + response);*/

    }
}
