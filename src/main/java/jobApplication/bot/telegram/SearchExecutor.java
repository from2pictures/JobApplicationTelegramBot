package jobApplication.bot.telegram;

import jobApplication.bot.dto.VacancyFilter;
import jobApplication.bot.factory.SendMessageFactory;
import jobApplication.bot.model.Vacancy;
import jobApplication.bot.service.ApiService;
import jobApplication.bot.service.VacancyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchExecutor {

    private static final int MAX_MESSAGE_LENGTH = 4000; // с запасом от лимита 4096
    private static final int MAX_VACANCIES      = 20;

    private final SendMessageFactory sendMessageFactory;
    private final VacancyService     vacancyService;
    private final ApiService         apiService;
    private final TelegramClient     telegramClient;

    @Async
    public void search(long chatId, VacancyFilter filter, boolean useApi) {
        log.info("Запуск поиска: chatId={}, useApi={}, filter={}", chatId, useApi, filter);
        try {
            List<SendMessage> messages = useApi
                    ? searchViaApi(chatId, filter)
                    : searchInDb(chatId, filter);
            for (SendMessage msg : messages) {
                msg.setParseMode("HTML");
                telegramClient.execute(msg);
            }
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки результатов: chatId={}", chatId, e);
            trySendError(chatId);
        } catch (Exception e) {
            log.error("Ошибка поиска: chatId={}", chatId, e);
            trySendError(chatId);
        }
    }

    private List<SendMessage> searchViaApi(long chatId, VacancyFilter filter) {
        return apiService.searchWithFilter(filter)
                .filter(r -> r.data() != null && !r.data().jobs().isEmpty())
                .map(r -> {
                    List<Vacancy> saved = vacancyService.saveFromListOfDto(r.data().jobs());
                    return buildMessages(chatId, saved, true);
                })
                .orElse(List.of(sendMessageFactory.withMainMenu(chatId,
                        "😔 По запросу ничего не найдено. Попробуй изменить фильтры.")));
    }

    private List<SendMessage> searchInDb(long chatId, VacancyFilter filter) {
        List<Vacancy> results = vacancyService.searchWithFilter(filter);
        return buildMessages(chatId, results, false);
    }

    private List<SendMessage> buildMessages(long chatId, List<Vacancy> vacancies, boolean fromApi) {
        if (vacancies.isEmpty()) {
            return List.of(sendMessageFactory.withMainMenu(chatId,
                    "😔 Ничего не найдено. Попробуй изменить фильтры."));
        }

        String source = fromApi ? "API (сохранено в базу)" : "базы данных";
        List<Vacancy> limited = vacancies.stream().limit(MAX_VACANCIES).toList();

        String header = "✅ <b>Найдено " + vacancies.size() + " вакансий</b> из " + source + ":\n\n";
        List<String> chunks = new ArrayList<>();
        StringBuilder current = new StringBuilder(header);

        for (Vacancy v : limited) {
            String block = v.toTelegramMessage() + "\n\n";
            if (current.length() + block.length() > MAX_MESSAGE_LENGTH) {
                chunks.add(current.toString());
                current = new StringBuilder(block);
            } else {
                current.append(block);
            }
        }
        if (!current.isEmpty()) {
            chunks.add(current.toString());
        }
        List<SendMessage> messages = new ArrayList<>();
        for (int i = 0; i < chunks.size(); i++) {
            boolean isLast = i == chunks.size() - 1;
            messages.add(isLast
                    ? sendMessageFactory.withMainMenu(chatId, chunks.get(i))
                    : sendMessageFactory.plain(chatId, chunks.get(i))
            );
        }
        return messages;
    }

    private void trySendError(long chatId) {
        try {
            telegramClient.execute(
                    sendMessageFactory.withMainMenu(chatId, "❌ Ошибка при поиске. Попробуй ещё раз.")
            );
        } catch (TelegramApiException ex) {
            log.error("Не удалось отправить сообщение об ошибке", ex);
        }
    }
}