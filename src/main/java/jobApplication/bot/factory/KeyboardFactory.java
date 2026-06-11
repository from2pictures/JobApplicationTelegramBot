package jobApplication.bot.factory;

import jobApplication.bot.telegram.CallbackHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

@Component
public class KeyboardFactory {

    public ReplyKeyboardMarkup startMenu() {
        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(
                        new KeyboardRow("🔍 Найти вакансии", "📊 Статистика"),
                        new KeyboardRow("❤️ Избранное", "👁 Просмотренные"),
                        new KeyboardRow("📤 Экспорт", "❓ Помощь")
                ))
                .resizeKeyboard(true)
                .isPersistent(true)
                .build();
    }

    public InlineKeyboardMarkup searchSourceMenu() {
        return InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(
                        btn("🌐 Через API (свежие)", CallbackHandler.CB_SEARCH_API)
                ))
                .keyboardRow(new InlineKeyboardRow(
                        btn("🗄 В базе данных",      CallbackHandler.CB_SEARCH_DB)
                ))
                .keyboardRow(new InlineKeyboardRow(
                        btn("✖️ Отмена",              CallbackHandler.CB_CANCEL)
                ))
                .build();
    }

    public InlineKeyboardMarkup exportMenu() {
        return InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(
                        btn("📋 JSON", CallbackHandler.CB_EXPORT_JSON),
                        btn("📊 CSV",  CallbackHandler.CB_EXPORT_CSV),
                        btn("🌐 HTML", CallbackHandler.CB_EXPORT_HTML)
                ))
                .keyboardRow(new InlineKeyboardRow(
                        btn("✖️ Отмена", CallbackHandler.CB_CANCEL)
                ))
                .build();
    }

    public InlineKeyboardMarkup skipCancelButtons() {
        return InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(
                        btn("⏭ Пропустить", CallbackHandler.CB_FILTER_SKIP),
                        btn("✖️ Отмена",     CallbackHandler.CB_CANCEL)
                ))
                .build();
    }

    public InlineKeyboardMarkup remoteChoiceButtons() {
        return InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(
                        btn("✅ Да",         CallbackHandler.CB_FILTER_REMOTE_YES),
                        btn("❌ Нет",        CallbackHandler.CB_FILTER_REMOTE_NO),
                        btn("⏭ Пропустить", CallbackHandler.CB_FILTER_SKIP)
                ))
                .keyboardRow(new InlineKeyboardRow(
                        btn("✖️ Отмена",     CallbackHandler.CB_CANCEL)
                ))
                .build();
    }

    public InlineKeyboardMarkup cancelButton() {
        return InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(
                        btn("✖️ Отмена", CallbackHandler.CB_CANCEL)
                ))
                .build();
    }

    private InlineKeyboardButton btn(String text, String callbackData) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackData)
                .build();
    }
}