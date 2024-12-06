import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class WebScraper {
    public static String scrapeNews() {
        try {
            Document doc = Jsoup.connect("https://example.com/news").get(); // Cambia con l'URL desiderato
            Elements headlines = doc.select(".headline"); // Cambia il selettore CSS per adattarlo al sito
            StringBuilder result = new StringBuilder();

            headlines.forEach(headline -> result.append(headline.text()).append("\n"));

            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "Errore durante lo scraping!";
        }
    }
}
