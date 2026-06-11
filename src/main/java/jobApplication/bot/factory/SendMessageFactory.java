package jobApplication.bot.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public class SendMessageFactory {

    private final KeyboardFactory keyboardFactory;

    @Autowired
    private SendMessageFactory(KeyboardFactory keyboardFactory) {
        this.keyboardFactory = keyboardFactory;
    }

    public SendMessage plain(long chatId, String text) {
        return SendMessage.builder()
                .chatId(chatId).text(text).parseMode("HTML").build();
    }

    public SendMessage withMainMenu(long chatId, String text) {
        return SendMessage.builder()
                .chatId(chatId).text(text).parseMode("HTML")
                .replyMarkup(keyboardFactory.startMenu()).build();
    }

    public SendMessage withSearchSourceMenu(long chatId, String text) {
        return SendMessage.builder()
                .chatId(chatId).text(text).parseMode("HTML")
                .replyMarkup(keyboardFactory.searchSourceMenu()).build();
    }

    public SendMessage withExportMenu(long chatId, String text) {
        return SendMessage.builder()
                .chatId(chatId).text(text).parseMode("HTML")
                .replyMarkup(keyboardFactory.exportMenu()).build();
    }

    public SendMessage withSkipCancelButtons(long chatId, String text) {
        return SendMessage.builder()
                .chatId(chatId).text(text).parseMode("HTML")
                .replyMarkup(keyboardFactory.skipCancelButtons()).build();
    }

    public SendMessage withRemoteChoiceButtons(long chatId, String text) {
        return SendMessage.builder()
                .chatId(chatId).text(text).parseMode("HTML")
                .replyMarkup(keyboardFactory.remoteChoiceButtons()).build();
    }

    public SendMessage withCancelButton(long chatId, String text) {
        return SendMessage.builder()
                .chatId(chatId).text(text).parseMode("HTML")
                .replyMarkup(keyboardFactory.cancelButton()).build();
    }
}