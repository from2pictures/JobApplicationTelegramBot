package jobApplication.bot.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Slf4j
@Component
public class MyTelegramBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;
    private final CommandHandler commandHandler;
    private final CallbackHandler callbackHandler;
    private final String token;

    @Autowired
    private MyTelegramBot(TelegramClient telegramClient, CommandHandler commandHandler, CallbackHandler callbackHandler, @Value("${telegram.bot.token}") String token) {
        this.telegramClient = telegramClient;
        this.commandHandler = commandHandler;
        this.callbackHandler = callbackHandler;
        this.token = token;
        log.info("Бот создан");
    }

    @Override
    public String getBotToken() {
        log.info("Кто то запросил токен");
        return token;
    }

    @Override
    public LongPollingSingleThreadUpdateConsumer getUpdatesConsumer() {
        log.info("Кто то использовал странный метод");
        return this;
    }

    @Override
    public void consume(List<Update> updates) {
        log.info("Пришли сообщения: " + updates);
        updates.forEach(this::processUpdate);
    }

    @Override
    public void consume(Update update) {
        log.info("Пришло сообщение: " + update);
        processUpdate(update);
    }

    private void processUpdate(Update update) {
        try {
            SendMessage response;

            if (update.hasMessage() && update.getMessage().hasText()) {
                response = commandHandler.handle(
                        update.getMessage().getText(),
                        update.getMessage().getChatId()
                );
            }

            else if (update.hasCallbackQuery()) {
                response = callbackHandler.handle(update.getCallbackQuery());
            }

            else return;
            response.setParseMode("HTML");
            telegramClient.execute(response);
            log.info("Ответил на сообщение: " + update);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }
}