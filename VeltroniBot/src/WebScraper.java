import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class WebScraper {

    public static List<String> getIntegratori() throws Exception {
        String url = "https://www.esempio-sito.com/integratori";

        Document doc = Jsoup.connect(url).get();
        Elements products = doc.select(".product-card"); // Cambia il selettore in base al sito

        List<String> integratori = new ArrayList<>();
        for (var product : products) {
            String name = product.select(".product-name").text();
            String price = product.select(".price").text();
            integratori.add(name + " - " + price);
        }
        return integratori;
    }
}
