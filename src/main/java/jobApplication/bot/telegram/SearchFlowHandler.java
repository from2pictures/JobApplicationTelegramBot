package jobApplication.bot.telegram;

import jobApplication.bot.dto.VacancyFilter;
import jobApplication.bot.factory.SendMessageFactory;
import jobApplication.bot.service.UserSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchFlowHandler {

    private static final String USE_API_FLAG = "__useApi__";

    private final SendMessageFactory sendMessageFactory;
    private final UserSessionService sessionService;
    private final SearchExecutor     searchExecutor;

    public SendMessage startSearchSourceSelection(long chatId) {
        sessionService.clearSession(chatId);
        return sendMessageFactory.withSearchSourceMenu(chatId,
                "🔍 <b>Где искать вакансии?</b>\n\n" +
                        "• <b>Через API</b> — свежие вакансии из внешнего источника, сохранятся в базу\n" +
                        "• <b>В базе</b> — быстрый поиск среди уже сохранённых");
    }

    public SendMessage startFilterFlow(long chatId, boolean useApi) {
        sessionService.clearSession(chatId);
        VacancyFilter filter = sessionService.getFilter(chatId);
        filter.setCompany(useApi ? USE_API_FLAG : null);
        sessionService.setState(chatId, UserState.WAITING_FOR_TITLE);
        return sendMessageFactory.withCancelButton(chatId,
                "✏️ Введи <b>название должности</b> (например: Java Developer, Data Analyst):");
    }

    public SendMessage handleInput(long chatId, String text, UserState state) {
        VacancyFilter filter = sessionService.getFilter(chatId);

        return switch (state) {
            case WAITING_FOR_TITLE -> {
                filter.setTitle(text.strip());
                sessionService.setState(chatId, UserState.WAITING_FOR_CITY);
                yield sendMessageFactory.withSkipCancelButtons(chatId,
                        "🏙 Введи <b>город</b> (или пропусти):");
            }
            case WAITING_FOR_CITY -> {
                filter.setCity(text.strip());
                sessionService.setState(chatId, UserState.WAITING_FOR_MIN_SALARY);
                yield sendMessageFactory.withSkipCancelButtons(chatId,
                        "💰 Введи <b>минимальную зарплату</b> в USD (или пропусти):");
            }
            case WAITING_FOR_MIN_SALARY -> {
                try {
                    filter.setMinSalary(Integer.parseInt(text.strip()));
                } catch (NumberFormatException e) {
                    yield sendMessageFactory.withSkipCancelButtons(chatId,
                            "⚠️ Введи число, например <b>2000</b>, или пропусти:");
                }
                sessionService.setState(chatId, UserState.WAITING_FOR_IS_REMOTE);
                yield sendMessageFactory.withRemoteChoiceButtons(chatId,
                        "🏠 Тебя интересует <b>удалённая работа</b>?");
            }
            default -> sendMessageFactory.withMainMenu(chatId, "Что-то пошло не так. Главное меню 👇");
        };
    }

    public SendMessage handleRemoteChoice(long chatId, boolean isRemote) {
        VacancyFilter filter = sessionService.getFilter(chatId);
        filter.setIsRemote(isRemote);
        return triggerSearch(chatId, filter);
    }

    public SendMessage handleSkip(long chatId) {
        UserState state = sessionService.getState(chatId);
        VacancyFilter filter = sessionService.getFilter(chatId);

        return switch (state) {
            case WAITING_FOR_CITY -> {
                sessionService.setState(chatId, UserState.WAITING_FOR_MIN_SALARY);
                yield sendMessageFactory.withSkipCancelButtons(chatId,
                        "💰 Введи <b>минимальную зарплату</b> в USD (или пропусти):");
            }
            case WAITING_FOR_MIN_SALARY -> {
                sessionService.setState(chatId, UserState.WAITING_FOR_IS_REMOTE);
                yield sendMessageFactory.withRemoteChoiceButtons(chatId,
                        "🏠 Тебя интересует <b>удалённая работа</b>?");
            }
            case WAITING_FOR_IS_REMOTE -> triggerSearch(chatId, filter);
            default -> sendMessageFactory.withMainMenu(chatId, "Главное меню 👇");
        };
    }

    private SendMessage triggerSearch(long chatId, VacancyFilter filter) {
        boolean useApi = USE_API_FLAG.equals(filter.getCompany());
        if (useApi) filter.setCompany(null);

        sessionService.clearSession(chatId);

        searchExecutor.search(chatId, filter, useApi);

        String source = useApi ? "внешнем API 🌐" : "базе данных 🗄";
        return sendMessageFactory.plain(chatId, "⏳ Ищу вакансии в " + source + ", подожди...");
    }
}