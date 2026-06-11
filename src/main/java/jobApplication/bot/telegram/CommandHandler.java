package jobApplication.bot.telegram;

import jobApplication.bot.factory.SendMessageFactory;
import jobApplication.bot.service.UserSessionService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class CommandHandler {

    private final Map<String, Function<Long, SendMessage>> commands;
    private final SendMessageFactory sendMessageFactory;
    private final UserSessionService sessionService;
    private final SearchFlowHandler  searchFlowHandler;
    private final StatsHandler statsHandler;

    @Autowired
    public CommandHandler(SendMessageFactory sendMessageFactory,
                          UserSessionService sessionService,
                          SearchFlowHandler searchFlowHandler, StatsHandler statsHandler) {
        this.sendMessageFactory = sendMessageFactory;
        this.sessionService     = sessionService;
        this.searchFlowHandler  = searchFlowHandler;
        this.statsHandler       = statsHandler;
        this.commands           = new HashMap<>();


        commands.put("/start", this::handleStart);
        commands.put("/help",  this::handleHelp);


        commands.put("🔍 найти вакансии", chatId -> searchFlowHandler.startSearchSourceSelection(chatId));
        commands.put("❤️ избранное",      chatId -> sendMessageFactory.plain(chatId, "❤️ Избранное пока в разработке"));
        commands.put("👁 просмотренные",  chatId -> sendMessageFactory.plain(chatId, "👁 Просмотренные пока в разработке"));
        commands.put("📤 экспорт",        chatId -> sendMessageFactory.withExportMenu(chatId,
                "📤 <b>Экспорт вакансий</b>\n\nВыбери формат — файл придёт следующим сообщением:"));
        commands.put("📊 статистика",     chatId -> statsHandler.getStats(chatId));
        commands.put("❓ помощь",          this::handleHelp);
    }

    public SendMessage handle(@NonNull String text, long chatId) {

        UserState state = sessionService.getState(chatId);
        if (state != UserState.IDLE) {
            return searchFlowHandler.handleInput(chatId, text, state);
        }

        return commands
                .getOrDefault(
                        text.toLowerCase().strip(),
                        id -> sendMessageFactory.withMainMenu(chatId, "Не понимаю эту команду. Воспользуйся меню 👇")
                )
                .apply(chatId);
    }

    private SendMessage handleStart(long chatId) {
        sessionService.clearSession(chatId);
        String welcome = """
                👋 <b>Привет! Я JobBot</b>

                Помогу найти вакансии по всему миру 🌍
                Умею искать через API и сохранять в базу, а также искать уже сохранённые.

                Выбери, что хочешь сделать 👇
                """;
        return sendMessageFactory.withMainMenu(chatId, welcome);
    }

    private SendMessage handleHelp(long chatId) {
        String help = """
                <b>📖 Справка</b>

                🔍 <b>Найти вакансии</b> — ищет через внешнее API и сохраняет результаты
                🗄 <b>В базе данных</b> — быстрый поиск среди уже сохранённых
                ❤️ <b>Избранное</b> — вакансии, которые ты сохранил
                👁 <b>Просмотренные</b> — история просмотров

                /start — вернуться в главное меню
                """;
        return sendMessageFactory.withMainMenu(chatId, help);
    }
}