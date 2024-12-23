import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;


public class WebScraper{
    private static final String BASE_URL = "https://www.myprotein.it"; //sito
    private static final String LIST_URL = "https://www.myprotein.it/nutrition/protein.list"; //link della pagina prodotti
    private static final String IMAGE_SAVE_PATH = "images/myprotein/"; // Cartella di destinazione per le immagini

    //funzione per scaricare l'immagine del prodotto
    private void downloadImage(String imageUrl, String destinationPath) {

        try (InputStream in = new URL(imageUrl).openStream()) {
            Files.createDirectories(Paths.get(destinationPath).getParent()); // Crea la directory se non esiste
            Files.copy(in, Paths.get(destinationPath));
            /*System.out.println("Immagine salvata: " + destinationPath);*/
        } catch (IOException e) {

            System.err.println("Errore durante il download dell'immagine: " + e.getMessage());
        }
    }



    //funzione per gestire DA DOVE scaricare l'immagine e DOVE inserirla tramite JSOUP
    private void saveImage(String link, String destinationPath, String nameimage) {

        try{
            Document doc = Jsoup.connect(link).timeout(6000).get();
            String imageUrl = doc.select(".athenaProductImageCarousel_image").attr("src");
            if (!imageUrl.isEmpty()) {
                String imageName = nameimage.replaceAll("[^a-zA-Z0-9]", "_") + ".jpg"; //riempie tutti gli spazi vuoti con _
                downloadImage(imageUrl, IMAGE_SAVE_PATH + imageName); //chiama la funzione per scaricare l'immagine
            }
        }catch (IOException e){
            System.err.println("Errore durante il download dell'immagine: " + e.getMessage());

        }
    }

    //funzione per formattare correttamente il prezzo del prodotto
    private double getPrice(String price) {

        String cleanedPrice = price.replaceAll("[^\\d,\\.]", "").replace(",", "."); // Rimuove simboli non numerici (esclusi la virgola e il punto) e spazi
        try {
            return Double.parseDouble(cleanedPrice);

        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    //La funzione 'main' per fare lo scraping
    public void startScraping() {
        System.out.println("Scraping avviato");
        try {
            DatabaseManager dbManager = new DatabaseManager();

            int pageNumber = 1; //servirà per indicare il numero della pagina in cui andare nel sito
            int nprod = 0;//contatore dei prodoti aggiunti nel database
            boolean hasMorePages = true; //per continuare o smettere di cercare altre pagine

            while (hasMorePages) {
                String url = LIST_URL + "?pageNumber=" + pageNumber;//l'url corrente
                Document doc = Jsoup.connect(url).timeout(6000).get();

                Elements products = doc.select(".productListProducts_product"); //Utilizzando JSOUP possiamo prendere tutti i valori dove nell'HTML class="productListProducts_product" ovvero ogni prodotto

                if (products.isEmpty()) { //se non ci sono prodotti allora le pagine sono finite
                    hasMorePages = false;
                } else {

                    for (Element product : products) { //per ogni prodotto catturato:


                        String brand = "myprotein";
                        //si estraggono tutti i dati mancanti
                        String name = product.select(".athenaProductBlock_productName").text().replace("-","");

                        if(!name.contains("Miscela") && !name.contains("(Campione)") && !name.contains("Albume in Polvere") && !name.contains("Myvegan Plant Protein Superblend")&& !name.contains("(Sample)") && !name.contains("Collagene") && !name.contains("Breakfast") && name != "null") { //controllo dei prodotti che non vanno aggiunti

                            Double price = getPrice(product.select(".athenaProductBlock_fromValue").text());

                            if(price == 0.0){//purtroppo il sito è fatto maluccio (è uno dei siti più conosciuti al mondo, non ho parole) perciò potrebbe cambiare la classe
                                price = getPrice(product.select(".athenaProductBlock_priceValue").text());
                            }
                            String productUrl = BASE_URL + product.select(".athenaProductBlock_linkImage").attr("href");

                            NutritionalValues nutritionV = new NutritionalValues(productUrl); //per estrarre i dati nutrizionali ho preferito creare una classe a parte, potendo con facilità usarte un oggetto contenente tutti i valori




                            saveImage(productUrl, IMAGE_SAVE_PATH, name);//funzione per ottenere l'immagine del prodotto

                            dbManager.insertProduct("myprotein", name, price, IMAGE_SAVE_PATH + name.replace(" ", "_") + ".jpg", productUrl, nutritionV); //tutti i dati vengono passati alla classe che inserirà tutto nel database

                            // Ritardo tra una richiesta e l'altra per evitare di creare finti attacchi Dos
                            Thread.sleep(500);

                            System.out.println("Nuovo prodotto aggiunto: " + name);
                            nprod++;
                        }
                    }
                    pageNumber++;
                }
            }
            System.out.println("Scraping completato con successo!");
            System.out.println("Prodotti aggiunti: " + nprod);
        } catch (Exception e) {
            System.err.println("Errore durante lo scraping1: " + e.getMessage());
        }
    }
}
