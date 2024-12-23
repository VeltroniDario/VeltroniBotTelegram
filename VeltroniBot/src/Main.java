import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.List;
import java.util.Map;

public class Main {



    public static void main(String[] args) {

        //esecuzione dello scraping per riempire tutto il database con i dati necessari al bot
        WebScraper scraper = new WebScraper();
        try {
            scraper.startScraping();
        } catch (Exception e) {
            System.err.println("Errore durante lo scraping: " + e.getMessage());
        }

        //avvio del bot
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new FitnessBot());
            System.out.println("Bot avviato!");
        } catch (Exception e) {
            e.printStackTrace();
        }




    }
}
