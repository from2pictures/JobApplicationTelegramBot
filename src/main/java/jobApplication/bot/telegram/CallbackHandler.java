package jobApplication.bot.telegram;

import jobApplication.bot.factory.SendMessageFactory;
import jobApplication.bot.service.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class CallbackHandler {

    public static final String CB_STATS     = "cb_stats";
    public static final String CB_SEARCH     = "cb_search";
    public static final String CB_FAVOURITES = "cb_favourites";
    public static final String CB_VIEWED     = "cb_viewed";
    public static final String CB_EXPORT     = "cb_export";

    public static final String CB_SEARCH_API = "cb_search_api";
    public static final String CB_SEARCH_DB  = "cb_search_db";

    public static final String CB_FILTER_REMOTE_YES = "cb_filter_remote_yes";
    public static final String CB_FILTER_REMOTE_NO  = "cb_filter_remote_no";
    public static final String CB_FILTER_SKIP       = "cb_filter_skip";
    public static final String CB_CANCEL            = "cb_cancel";

    public static final String CB_EXPORT_JSON = "cb_export_json";
    public static final String CB_EXPORT_CSV  = "cb_export_csv";
    public static final String CB_EXPORT_HTML = "cb_export_html";

    private final SendMessageFactory sendMessageFactory;
    private final UserSessionService sessionService;
    private final SearchFlowHandler  searchFlowHandler;
    private final ExportHandler      exportHandler;
    private final StatsHandler       statsHandler;

    @Autowired
    public CallbackHandler(SendMessageFactory sendMessageFactory,
                           UserSessionService sessionService,
                           SearchFlowHandler searchFlowHandler,
                           ExportHandler exportHandler, StatsHandler statsHandler) {
        this.sendMessageFactory = sendMessageFactory;
        this.sessionService     = sessionService;
        this.searchFlowHandler  = searchFlowHandler;
        this.exportHandler      = exportHandler;
        this.statsHandler       = statsHandler;
    }

    public SendMessage handle(CallbackQuery callbackQuery) {
        String data   = callbackQuery.getData();
        long   chatId = callbackQuery.getMessage().getChatId();

        return switch (data) {

            case CB_STATS      -> statsHandler.getStats(chatId);
            case CB_SEARCH     -> searchFlowHandler.startSearchSourceSelection(chatId);
            case CB_FAVOURITES -> sendMessageFactory.plain(chatId, "❤️ Избранное пока в разработке");
            case CB_VIEWED     -> sendMessageFactory.plain(chatId, "👁 Просмотренные пока в разработке");
            case CB_EXPORT     -> sendMessageFactory.withExportMenu(chatId,
                    "📤 <b>Экспорт вакансий</b>\n\nВыбери формат — файл придёт следующим сообщением:");


            case CB_SEARCH_API -> searchFlowHandler.startFilterFlow(chatId, true);
            case CB_SEARCH_DB  -> searchFlowHandler.startFilterFlow(chatId, false);


            case CB_FILTER_REMOTE_YES -> searchFlowHandler.handleRemoteChoice(chatId, true);
            case CB_FILTER_REMOTE_NO  -> searchFlowHandler.handleRemoteChoice(chatId, false);

            case CB_FILTER_SKIP -> searchFlowHandler.handleSkip(chatId);


            case CB_EXPORT_JSON -> {
                exportHandler.export(chatId, ExportHandler.Format.JSON);
                yield sendMessageFactory.plain(chatId, "⏳ Генерирую JSON...");
            }
            case CB_EXPORT_CSV -> {
                exportHandler.export(chatId, ExportHandler.Format.CSV);
                yield sendMessageFactory.plain(chatId, "⏳ Генерирую CSV...");
            }
            case CB_EXPORT_HTML -> {
                exportHandler.export(chatId, ExportHandler.Format.HTML);
                yield sendMessageFactory.plain(chatId, "⏳ Генерирую HTML...");
            }


            case CB_CANCEL -> {
                sessionService.clearSession(chatId);
                yield sendMessageFactory.withMainMenu(chatId, "Действие отменено. Главное меню 👇");
            }

            default -> sendMessageFactory.withMainMenu(chatId, "Неизвестная команда. Главное меню 👇");
        };
    }
}