import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLOutput;

public class WebScraper {
    private static final String BASE_URL = "https://www.myprotein.it";
    private static final String LIST_URL = "https://www.myprotein.it/nutrition/protein.list";
    private static final String IMAGE_SAVE_PATH = "images\\myprotein\\"; // Cartella di destinazione per le immagini


    public String getDescription(String link){
        try{
            Document doc = Jsoup.connect(link).timeout(6000).get();
            String description = doc.select(".athenaProductPageSynopsisContent").text();
            return description;
        }catch (IOException e){
            System.err.println("Errore durante lo scraping: " + e.getMessage());

        }
        return null;

    }

    private void downloadImage(String imageUrl, String destinationPath) {

        try (InputStream in = new URL(imageUrl).openStream()) {
            Files.createDirectories(Paths.get(destinationPath).getParent()); // Crea la directory se non esiste
            Files.copy(in, Paths.get(destinationPath));
            System.out.println("Immagine salvata: " + destinationPath);
        } catch (IOException e) {
            System.err.println("Errore durante il download dell'immagine: " + e.getMessage());
        }
    }

    private void saveImage(String link, String destinationPath, String name) {
        try{
            Document doc = Jsoup.connect(link).timeout(6000).get();
            String imageUrl = doc.select(".athenaProductImageCarousel_image").attr("src");
            if (!imageUrl.isEmpty()) {
                String imageName = name.replaceAll("[^a-zA-Z0-9]", "_") + ".jpg";
                downloadImage(imageUrl, IMAGE_SAVE_PATH + imageName);
            }
        }catch (IOException e){
            System.err.println("Errore durante il download dell'immagine: " + e.getMessage());

        }
    }

    public void startScraping() {
        try {
            DatabaseManager dbManager = new DatabaseManager();
            int page = 1;
            int nprod = 0;
            boolean hasMorePages = true;

            while (hasMorePages) {
                String url = LIST_URL + "?pageNumber=" + page;
                Document doc = Jsoup.connect(url).timeout(6000).get();

                Elements products = doc.select(".productListProducts_product");

                if (products.isEmpty()) {
                    hasMorePages = false;
                } else {

                    for (Element product : products) {
                        String name = product.select(".athenaProductBlock_productName").text();
                        String price = product.select(".athenaProductBlock_fromValue").text();
                        String productUrl = BASE_URL + product.select(".athenaProductBlock_linkImage").attr("href");
                        String description = getDescription(productUrl);
                        String category = "Proteine in polvere";
                        String site = "https://www.myprotein.it";
                        saveImage(productUrl, IMAGE_SAVE_PATH, name);

                        dbManager.insertProduct(name, price, description, category, site, productUrl);

                        // Ritardo tra una richiesta e l'altra per evitare di essere bloccati
                        Thread.sleep(2000);
                        System.out.println("Nuovo prodotto aggiunto: " + name);
                        nprod++;
                    }
                    page++;
                }
            }
            System.out.println("Scraping completato con successo!");
            System.out.println("Prodotti aggiunti: " + nprod);
        } catch (Exception e) {
            System.err.println("Errore durante lo scraping: " + e.getMessage());
        }
    }
}
