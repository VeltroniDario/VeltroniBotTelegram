import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//La casse NutritionalValues serve per gestire a parte tutto il processo per prendere e formattare correttamente i dati nutritivi di ogni prodotto

public class NutritionalValues {
    //tutti i valori che prenderemo da una tabella nel sito
    private double energia100g, energiaDose;
    private double grassi100g, grassiDose;
    private double saturi100g, saturiDose;
    private double carboidrati100g, carboidratiDose;
    private double zuccheri100g, zuccheriDose;
    private double proteine100g, proteineDose;
    private double sale100g, saleDose;

    //funzione per troncare la stringha di informazioni per togliere pezzi inutili
    public static String troncaFinoAEnergia(String testo) {
        Pattern pattern = Pattern.compile(".*(Energia.*)", Pattern.CASE_INSENSITIVE); // .* all'inizio per matchare tutto quello che c'è prima
        Matcher matcher = pattern.matcher(testo);
        if (matcher.matches()) {
            return matcher.group(1); //gruppo 1 cattura Energia e il resto
        }
        return null;
    }

    // Metodo per ottenere, tramite JSOUP, la tabella di informazioni dal link fornito
    public static String getTableString(String link) {
        try {
            Document doc = Jsoup.connect(link).timeout(6000).get();
            String table = doc.select(".nutritional-info-table tr").text();

            if (table == null || table.isEmpty()) {
                table = doc.select(".product-table").text();
            }

            return troncaFinoAEnergia(table);
        } catch (IOException e) {
            System.err.println("Errore durante lo scraping2: " + e.getMessage());
        }
        return null;
    }


    // Costruttore che estrae i valori nutrizionali
    public NutritionalValues(String link) {

        String table = getTableString(link); //per ottenere la stringha con tutte le informazioni
        ArrayList<String> n = new ArrayList<>(); //lista per contenere tutti i valori che ci servono singolarmente


        //tramite l'uso dei pattern posso estrarre dalla stringha tutti i numeri con e senza virgola
        //purtroppo nel sito i dati sono inseriti maluccio: alcuni numeri sono scritti con la virgola altri con il punto!!
        //Quindi ho dovuto prende tutti e due i casi
        Pattern pattern = Pattern.compile("\\d+(?:[.,]\\d+)?");
        Matcher matcher = pattern.matcher(table);
        while (matcher.find()) { //per tutti i pattern trovati
            n.add(matcher.group());//aggiunge il numero all'array
        }

        for (int i = 0; i < n.size(); i++) {
            String valoreOriginale = n.get(i);
            String valoreCorretto = valoreOriginale.replace(",", ".");
            n.set(i, valoreCorretto);
        }

        //Inserimento di tutti i dati trovati
        this.energia100g = Double.parseDouble(n.get(1));
        this.energiaDose = Double.parseDouble(n.get(2));

        this.grassi100g = Double.parseDouble(n.get(3));
        this.grassiDose = Double.parseDouble(n.get(4));
        this.saturi100g = Double.parseDouble(n.get(5));
        this.saturiDose = Double.parseDouble(n.get(6));
        this.carboidrati100g = Double.parseDouble(n.get(7));
        this.carboidratiDose = Double.parseDouble(n.get(8));

        this.zuccheri100g = Double.parseDouble(n.get(9));

        this.zuccheriDose = Double.parseDouble(n.get(10));
        this.proteine100g = Double.parseDouble(n.get(11));
        this.proteineDose = Double.parseDouble(n.get(12));
        this.sale100g = Double.parseDouble(n.get(13));
        this.saleDose = Double.parseDouble(n.get(14));

    }

    @Override
    public String toString() {
        return "Energia per 100g: "+energia100g+" kJ\n" + "Energia per dose: "+ energiaDose +" kJ\n" + "Grassi per 100g: "+grassi100g + " g\n" + "Grassi per dose: " + grassiDose + " g\n" + "Grassi saturi per 100g: " + saturi100g + " g\n" + "Grassi saturi per dose:" +saturiDose + " g\n" + "Carboidrati per 100g: " + carboidrati100g + " g\n" + "Carboidrati per dose: " + carboidratiDose + " g\n" + "Zuccheri per 100g: "+zuccheri100g +" g\n" + "Zuccheri per dose: " + zuccheriDose + " g\n" + "Proteine per 100g: " + proteine100g + " g\n" +"Proteine per dose: " + proteineDose + " g\n"+ "Sale per 100g: " + sale100g + " g\n" +"Sale per dose: " + saleDose + " g";
    }

    public double getEnergia100g() {
        return energia100g;
    }

    public void setEnergia100g(int energia100g) {
        this.energia100g = energia100g;
    }

    public double getEnergiaDose() {
        return energiaDose;
    }

    public void setEnergiaDose(int energiaDose) {
        this.energiaDose = energiaDose;
    }

    public double getGrassi100g() {
        return grassi100g;
    }

    public void setGrassi100g(double grassi100g) {
        this.grassi100g = grassi100g;
    }

    public double getGrassiDose() {
        return grassiDose;
    }

    public void setGrassiDose(double grassiDose) {
        this.grassiDose = grassiDose;
    }

    public double getSaturi100g() {
        return saturi100g;
    }

    public void setSaturi100g(double saturi100g) {
        this.saturi100g = saturi100g;
    }

    public double getSaturiDose() {
        return saturiDose;
    }

    public void setSaturiDose(double saturiDose) {
        this.saturiDose = saturiDose;
    }

    public double getProteineDose() {
        return proteineDose;
    }

    public void setProteineDose(double proteineDose) {
        this.proteineDose = proteineDose;
    }

    public double getProteine100g() {
        return proteine100g;
    }

    public void setProteine100g(double proteine100g) {
        this.proteine100g = proteine100g;
    }

    public double getZuccheri100g() {
        return zuccheri100g;
    }

    public void setZuccheri100g(double zuccheri100g) {
        this.zuccheri100g = zuccheri100g;
    }

    public double getCarboidratiDose() {
        return carboidratiDose;
    }

    public void setCarboidratiDose(double carboidratiDose) {
        this.carboidratiDose = carboidratiDose;
    }

    public double getCarboidrati100g() {
        return carboidrati100g;
    }

    public void setCarboidrati100g(double carboidrati100g) {
        this.carboidrati100g = carboidrati100g;
    }

    public double getZuccheriDose() {
        return zuccheriDose;
    }

    public void setZuccheriDose(double zuccheriDose) {
        this.zuccheriDose = zuccheriDose;
    }

    public double getSale100g() {
        return sale100g;
    }

    public void setSale100g(double sale100g) {
        this.sale100g = sale100g;
    }

    public double getSaleDose() {
        return saleDose;
    }

    public void setSaleDose(double saleDose) {
        this.saleDose = saleDose;
    }
}

