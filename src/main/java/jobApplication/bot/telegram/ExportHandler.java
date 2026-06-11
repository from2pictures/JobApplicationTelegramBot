package jobApplication.bot.telegram;

import jobApplication.bot.dto.VacancyDTO;
import jobApplication.bot.dto.VacancyExportDTO;
import jobApplication.bot.model.Vacancy;
import jobApplication.bot.service.ExportService;
import jobApplication.bot.service.VacancyService;
import jobApplication.bot.factory.SendMessageFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExportHandler {

    private static final DateTimeFormatter FILE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");

    private final ExportService     exportService;
    private final VacancyService    vacancyService;
    private final TelegramClient    telegramClient;
    private final SendMessageFactory sendMessageFactory;

    public enum Format { JSON, CSV, HTML }

    @Async
    public void export(long chatId, Format format) {
        try {
            List<VacancyExportDTO> vacancies = vacancyService.getAllForExport();

            if (vacancies.isEmpty()) {
                telegramClient.execute(
                        sendMessageFactory.withMainMenu(chatId, "😔 В базе пока нет сохранённых вакансий.")
                );
                return;
            }

            String timestamp = LocalDateTime.now().format(FILE_FMT);
            String filename;
            byte[] data;
            String mimeType;

            switch (format) {
                case JSON -> {
                    data     = exportService.toJson(vacancies);
                    filename = "vacancies_" + timestamp + ".json";
                    mimeType = "application/json";
                }
                case CSV -> {
                    data     = exportService.toCsv(vacancies);
                    filename = "vacancies_" + timestamp + ".csv";
                    mimeType = "text/csv";
                }
                case HTML -> {
                    data     = exportService.toHtml(vacancies);
                    filename = "vacancies_" + timestamp + ".html";
                    mimeType = "text/html";
                }
                default -> throw new IllegalArgumentException("Unknown format: " + format);
            }

            SendDocument doc = SendDocument.builder()
                    .chatId(chatId)
                    .document(new InputFile(new ByteArrayInputStream(data), filename))
                    .caption("📎 <b>" + vacancies.size() + " вакансий</b> — " + format.name())
                    .parseMode("HTML")
                    .build();

            telegramClient.execute(doc);

        } catch (IOException e) {
            log.error("Ошибка генерации экспорта format={} chatId={}", format, chatId, e);
            trySendError(chatId);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки файла format={} chatId={}", format, chatId, e);
        }
    }

    private void trySendError(long chatId) {
        try {
            telegramClient.execute(
                    sendMessageFactory.withMainMenu(chatId, "❌ Не удалось создать файл. Попробуй ещё раз.")
            );
        } catch (TelegramApiException ex) {
            log.error("Не удалось отправить сообщение об ошибке", ex);
        }
    }
}