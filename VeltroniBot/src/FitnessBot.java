import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class FitnessBot extends TelegramLongPollingBot {

    @Override
    public String getBotUsername() {
        return "IlNomeDelTuoBot";
    }

    @Override
    public String getBotToken() {
        return "IL_TUO_TOKEN_TELEGRAM";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String command = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();

            String response;
            if (command.startsWith("/alimento")) {
                String alimento = command.replace("/alimento", "").trim();
                response = getAlimentoInfo(alimento);
            } else if (command.equals("/integratori")) {
                response = getIntegratoriList();
            } else if (command.equals("/crea_dieta")) {
                response = "Usa il comando /add_alimento [nome_alimento] per aggiungere alimenti.";
            } else {
                response = "Comando non riconosciuto. Usa /help per vedere i comandi disponibili.";
            }

            sendMessage(chatId, response);
        }
    }

    private void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getAlimentoInfo(String alimento) {
        // TODO: Integrazione con FatSecret API
        return "Informazioni su " + alimento + " (da implementare).";
    }

    private String getIntegratoriList() {
        // TODO: Integrazione con Web Scraper
        return "Elenco degli integratori (da implementare).";
    }
}
