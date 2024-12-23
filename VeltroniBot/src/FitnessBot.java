import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

public class FitnessBot extends TelegramLongPollingBot {

    private final Map<Long, BotState> userState = new HashMap<>(); //utilizzerò degli "stati" per memorizzare cosa sta facendo l'utente
    private final Map<String, Map<String, Map<String, List<String>>>> dietTable = new HashMap<>(); //per memorizzare la tabella della dieta che l'utente potrà creare


    @Override
    public String getBotUsername() {
        return "FittyBot";
    }

    @Override
    public String getBotToken() {
        return "8185869448:AAEitVqhhl7aGBWaDCFBvyVyk3O84MFSTKM";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId(); // Ottieni il chatId dal messaggio del CallbackQuery

            SendMessage response = new SendMessage();
            response.setChatId(chatId.toString()); // Usa il chatId corretto

            switch (callbackData) {
                case "OPTION_1":
                    userState.put(chatId, BotState.PROTEINS);
                    response.setText("Usa i comandi /get_product e /get_list");
                    break;
                case "OPTION_2":
                    response.setText("Usa il comando /add_day [nome giorno] per aggiungere un giorno alla tabella\n ES. /add_day lunedì");
                    userState.put(chatId, BotState.CREATE_DIET);
                    break;
                case "OPTION_3":
                    response.setText("Usa /info [food_name] per cercare informazioni nutrizionali");
                    userState.put(chatId,null);
                    break;
                default:
                    response.setText("Opzione sconosciuta!");
            }

            try {
                execute(response);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }


        if (update.hasMessage() && update.getMessage().hasText()) { //controllo sei i messaggi non sono vuoti

            String command = update.getMessage().getText(); //command contiene il testo del messaggio scritto dall'utente
            Long chatId = Long.valueOf(update.getMessage().getChatId().toString()); //salviamo l'id dell'utente

            BotState currentState = userState.get(chatId);// Ottieni lo stato attuale dell'utente

            String response;
            if(currentState == null) { //controlla se ci sono degli stati
                if (command.startsWith("/info")) {
                    String alimento = command.replace("/info", "").trim();
                    if(alimento.isEmpty()){
                        response = "insert a name of food after the command";
                    }else{
                        response = FatSecretAPIrequest(alimento);
                    }

                } else if (command.equals("/proteins")) {
                    userState.put(chatId, BotState.PROTEINS);
                    response = "usa i comandi /get_product e /get_list";
                } else if (command.equals("/create_diet")) {
                    userState.put(chatId, BotState.CREATE_DIET);
                    response = "Usa il comando /add_day [nome giorno] per aggiungere un giorno alla tabella\n ES. /add_day lunedì";
                } else if(command.equals("/start")) {
                    printMenu(update, String.valueOf(chatId));
                    userState.put((chatId), null);
                    response = "";
                } else {
                    response = "Comando non riconosciuto. Usa /help per vedere i comandi disponibili.";
                }
                sendMessage(chatId, response);
            }else{
                switch (currentState) {
                    case CREATE_DIET:
                        // Parsing del comando
                        String[] commandParts = command.split(" ", 2);
                        String mainCommand = commandParts[0];
                        String arguments = (commandParts.length > 1) ? commandParts[1].trim() : "";

                        String chatKey = String.valueOf(chatId);

                        switch (mainCommand) {
                            case "/add_day":
                                if (arguments.isEmpty()) {
                                    response = "Inserisci un giorno della settimana dopo il comando /add_day.";
                                } else {
                                    dietTable.putIfAbsent(chatKey, new HashMap<>());

                                    Map<String, Map<String, List<String>>> userDiet = dietTable.get(chatKey);
                                    if (!userDiet.containsKey(arguments)) {
                                        userDiet.put(arguments, new HashMap<>());
                                        response = "Giorno " + arguments + " aggiunto alla tua dieta. Usa /add_meal " + arguments + " [nome_pasto] per aggiungere un pasto.";
                                    } else {
                                        response = "Il giorno " + arguments + " è già presente nella tua dieta.";
                                    }
                                }
                                break;

                            case "/add_meal":
                                String[] mealArgs = arguments.split(" ", 2);
                                if (mealArgs.length < 2) {
                                    response = "Formato errato. Usa il comando /add_meal [giorno] [nome_pasto].";
                                } else {
                                    String day = mealArgs[0];
                                    String meal = mealArgs[1];

                                    if (!dietTable.containsKey(chatKey) || !dietTable.get(chatKey).containsKey(day)) {
                                        response = "Giorno non trovato. Aggiungi prima un giorno con il comando /add_day.";
                                    } else {
                                        dietTable.get(chatKey).get(day).putIfAbsent(meal, new ArrayList<>());
                                        response = "Pasto " + meal + " aggiunto per il giorno " + day + ". Usa /add_food " + day + " " + meal + " [nome_alimento] per aggiungere alimenti.";
                                    }
                                }
                                break;

                            case "/add_food":
                                String[] foodArgs = arguments.split(" ", 3);
                                if (foodArgs.length < 3) {
                                    response = "Formato errato. Usa il comando /add_food [giorno] [nome_pasto] [nome_alimento].";
                                } else {
                                    String day = foodArgs[0];
                                    String meal = foodArgs[1];
                                    String food = foodArgs[2];

                                    if (!dietTable.containsKey(chatKey) || !dietTable.get(chatKey).containsKey(day) || !dietTable.get(chatKey).get(day).containsKey(meal)) {
                                        response = "Giorno o pasto non trovato. Aggiungi prima un giorno e un pasto.";
                                    } else {
                                        dietTable.get(chatKey).get(day).get(meal).add(food);
                                        response = "Alimento " + food + " aggiunto al pasto " + meal + " del giorno " + day + ".";
                                    }
                                }
                                break;

                            case "/show_diet":
                                if (!dietTable.containsKey(chatKey) || dietTable.get(chatKey).isEmpty()) {
                                    response = "La tua dieta è vuota. Usa /add_day per iniziare.";
                                } else {
                                    StringBuilder dietSummary = new StringBuilder("Ecco la tua dieta:\n");
                                    Map<String, Map<String, List<String>>> userDiet = dietTable.get(chatKey);

                                    for (Map.Entry<String, Map<String, List<String>>> dayEntry : userDiet.entrySet()) {
                                        dietSummary.append("\n").append(dayEntry.getKey()).append(":\n");

                                        for (Map.Entry<String, List<String>> mealEntry : dayEntry.getValue().entrySet()) {
                                            dietSummary.append("  ").append(mealEntry.getKey()).append(": ");

                                            List<String> foods = mealEntry.getValue();
                                            if (foods.isEmpty()) {
                                                dietSummary.append("Nessun alimento aggiunto.\n");
                                            } else {
                                                dietSummary.append(String.join(", ", foods)).append("\n");
                                            }
                                        }
                                    }

                                    response = dietSummary.toString();
                                }
                                break;
                            case "/start":
                                printMenu(update, String.valueOf(chatId));
                                userState.put((chatId), null);
                                response = "";
                                break;
                            default:
                                response = "Comando non riconosciuto. Usa /add_day, /add_meal, /add_food o /show_diet.";
                                break;
                        }

                        sendMessage(chatId, response);
                        break;

                    case PROTEINS:
                        DatabaseManager dbManager = new DatabaseManager();
                        if (command.startsWith("/get_product")) {
                            String productName = command.replace("/get_product", "").trim();
                            if (productName.isEmpty()) {
                                response = "Per favore, specifica il nome del prodotto. Usa il comando /get_product [nome_prodotto].";
                            } else {
                                try {
                                    response = dbManager.getProductInfoByName(productName);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        } else if (command.startsWith("/get_list")) {
                            StringBuilder res = new StringBuilder("Ecco la lista dei prodotti disponibili con i loro prezzi:\n\n");
                            try {
                                Map<String, Double> products = dbManager.getAllProductsWithPrices();

                                // Ordina i prodotti per nome
                                List<String> sortedProductNames = new ArrayList<>(products.keySet());
                                Collections.sort(sortedProductNames);

                                // Costruisce la risposta
                                for (String productName : sortedProductNames) {
                                    double price = products.get(productName);
                                    res.append("- ").append(productName).append(": €").append(String.format("%.2f", price)).append("\n");
                                }

                                if (products.isEmpty()) {
                                    res = new StringBuilder("Nessun prodotto trovato nel database.");
                                }

                            } catch (Exception e) {
                                res = new StringBuilder("Errore durante il recupero dei dati: ").append(e.getMessage());
                            }

                            response = res.toString();

                        }else if(command.startsWith("/start")) {
                            printMenu(update, String.valueOf(chatId));
                            userState.put((chatId), null);
                            response = "";
                        }else {
                        response = "Comando non riconosciuto. Usa /get_product [nome_prodotto] per ottenere informazioni.";
                    }
                        sendMessage(chatId, response);
                        break;


                    default:
                        response = "Comando non riconosciuto nello stato corrente.";
                        sendMessage(chatId, response);
                        break;
                }

            }

        }

    }

    public void sendMessage(Long chatId, String what){
        SendMessage sm = SendMessage.builder()
                .chatId(chatId.toString()) //Who are we sending a message to
                .text(what).build();    //Message content
        try {
            execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }

    private void sendLinkMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.setParseMode("Markdown"); //text = "Clicca su questo [link](https://www.google.it)"

        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printMenu(Update update, String chatId){
        // Handle button clicks

            // Create a message with buttons
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("MENU\nChoose an option:");

            // Create buttons
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();

            // Row 1
            List<InlineKeyboardButton> row1 = new ArrayList<>();
            InlineKeyboardButton button1 = new InlineKeyboardButton();
            button1.setText("Search proteins");
            button1.setCallbackData("OPTION_1");
            row1.add(button1);
            InlineKeyboardButton button2 = new InlineKeyboardButton();
            button2.setText("Create a diet");
            button2.setCallbackData("OPTION_2");
            row1.add(button2);
            rows.add(row1);


            List<InlineKeyboardButton> row2 = new ArrayList<>();
            InlineKeyboardButton button3 = new InlineKeyboardButton();
            button3.setText("find nutritional informations");
            button3.setCallbackData("OPTION_3");
            row2.add(button3);
            rows.add(row2);


            markup.setKeyboard(rows);
            message.setReplyMarkup(markup);

            // Send the message
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

    }

    public static String FatSecretAPIrequest(String food){

        String clientId = "fe63e6d03d6744c5825a9377948f3e4c";
        String clientSecret = "99651d6c03854222989d5475b4472444";

        // Ottenere il token
        String accessTokenResponse = FatSecretAPI.getAccessToken(clientId, clientSecret);
        /*System.out.println("Token ottenuto dal server FatSacretAPI: " + accessTokenResponse);*/

        // Estrarre l'access token
        String accessToken = FatSecretAPI.extractAccessToken(accessTokenResponse);

        // Cercare un cibo
        String foodName = food;
        String response = FatSecretAPI.searchFood(accessToken, foodName);
        /*System.out.println(response);*/
        List<Map<String, String>> foodList = FatSecretAPI.parseFoodJson(response);

        // Stampare la lista
        return FatSecretAPI.getFoodDetails(foodList, food);

        /*System.out.println("Risultato della ricerca: " + response);*/
    }

    private enum BotState {
        CREATE_DIET,
        PROTEINS
    }
}
