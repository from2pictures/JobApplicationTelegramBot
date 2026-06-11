package jobApplication.bot.service;

import jobApplication.bot.dto.VacancyFilter;
import jobApplication.bot.telegram.UserState;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserSessionService {

    private final Map<Long, UserState> states = new HashMap<>();
    private final Map<Long, VacancyFilter> filters = new HashMap<>();

    public UserState getState(long chatId) {
        return states.getOrDefault(chatId, UserState.IDLE);
    }

    public void setState(Long chatId, UserState state) {
        states.put(chatId, state);
    }

    public VacancyFilter getFilter(Long chatId) {
        return filters.computeIfAbsent(chatId, id -> new VacancyFilter());
    }

    public void clearSession(Long chatId) {
        states.remove(chatId);
        filters.remove(chatId);
    }
}
