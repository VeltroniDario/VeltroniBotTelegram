import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class WebScraper {
    private static final String BASE_URL = "https://www.myprotein.it";
    private static final String LIST_URL = "https://www.myprotein.it/nutrition/protein.list";

    public String getDescription(String link){
        try{
            Document doc = Jsoup.connect(link).timeout(6000).get();
            String description = doc.select(".athenaProductPageSynopsisContent").text();
            System.out.println(description);
            return description;
        }catch (IOException e){
            System.err.println("Errore durante lo scraping: " + e.getMessage());

        }
        return null;

    }

    public void startScraping() {
        try {
            /*DatabaseManager dbManager = new DatabaseManager();*/
            int page = 1;
            boolean hasMorePages = true;
            System.out.println("mandarino");
            while (hasMorePages) {
                String url = LIST_URL + "?pageNumber=" + page;
                Document doc = Jsoup.connect(url).timeout(6000).get();

                Elements products = doc.select(".productListProducts_product");

                if (products.isEmpty()) {
                    hasMorePages = false;
                } else {
                    System.out.println("sium");
                    for (Element product : products) {
                        String name = product.select(".athenaProductBlock_productName").text();
                        String price = product.select(".athenaProductBlock_fromValue").text();
                        String productUrl = BASE_URL + product.select(".athenaProductBlock_linkImage").attr("href");
                        String description = getDescription(productUrl);
                        String category = "Proteine in polvere";
                        String site = "https://www.myprotein.it";


                        /*dbManager.insertProduct(name, price, description, category, site, site + productUrl);*/

                        // Ritardo tra una richiesta e l'altra per evitare di essere bloccati
                        Thread.sleep(2000);
                        System.out.println("patatone");
                    }
                    page++;
                }
            }
            System.out.println("Scraping completato con successo!");
        } catch (Exception e) {
            System.err.println("Errore durante lo scraping: " + e.getMessage());
        }
    }
}
