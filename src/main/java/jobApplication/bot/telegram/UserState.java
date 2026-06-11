package jobApplication.bot.telegram;

public enum UserState {
    IDLE,
    WAITING_FOR_TITLE,
    WAITING_FOR_CITY,
    WAITING_FOR_COUNTRY,
    WAITING_FOR_MIN_SALARY,
    WAITING_FOR_IS_REMOTE
}