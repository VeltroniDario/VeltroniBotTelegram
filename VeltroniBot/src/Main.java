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
    }
}
