package jobApplication.bot.factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("KeyboardFactory")
class KeyboardFactoryTest {

    private KeyboardFactory factory;

    @BeforeEach
    void setUp() {
        factory = new KeyboardFactory();
    }

    // ── startMenu ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("startMenu() не null и resizeKeyboard=true")
    void startMenu_notNullAndResizable() {
        ReplyKeyboardMarkup menu = factory.startMenu();
        assertThat(menu).isNotNull();
        assertThat(menu.getResizeKeyboard()).isTrue();
    }

    @Test
    @DisplayName("startMenu() содержит кнопку поиска вакансий")
    void startMenu_containsSearchButton() {
        ReplyKeyboardMarkup menu = factory.startMenu();
        boolean found = menu.getKeyboard().stream()
                .flatMap(row -> row.stream())
                .anyMatch(btn -> btn.getText().contains("Найти вакансии"));
        assertThat(found).isTrue();
    }

    @Test
    @DisplayName("startMenu() содержит кнопку экспорта")
    void startMenu_containsExportButton() {
        boolean found = factory.startMenu().getKeyboard().stream()
                .flatMap(row -> row.stream())
                .anyMatch(btn -> btn.getText().contains("Экспорт"));
        assertThat(found).isTrue();
    }

    // ── searchSourceMenu ──────────────────────────────────────────────────────

    @Test
    @DisplayName("searchSourceMenu() содержит кнопки API и базы данных")
    void searchSourceMenu_hasTwoSourceButtons() {
        List<String> callbacks = allCallbacks(factory.searchSourceMenu());
        assertThat(callbacks).contains("cb_search_api", "cb_search_db");
    }

    @Test
    @DisplayName("searchSourceMenu() содержит кнопку отмены")
    void searchSourceMenu_hasCancelButton() {
        assertThat(allCallbacks(factory.searchSourceMenu())).contains("cb_cancel");
    }

    // ── exportMenu ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("exportMenu() содержит кнопки JSON, CSV, HTML")
    void exportMenu_hasAllFormatButtons() {
        List<String> callbacks = allCallbacks(factory.exportMenu());
        assertThat(callbacks).contains("cb_export_json", "cb_export_csv", "cb_export_html");
    }

    @Test
    @DisplayName("exportMenu() содержит кнопку отмены")
    void exportMenu_hasCancelButton() {
        assertThat(allCallbacks(factory.exportMenu())).contains("cb_cancel");
    }

    // ── skipCancelButtons ─────────────────────────────────────────────────────

    @Test
    @DisplayName("skipCancelButtons() содержит Пропустить и Отмена")
    void skipCancelButtons_hasBothButtons() {
        List<String> callbacks = allCallbacks(factory.skipCancelButtons());
        assertThat(callbacks).contains("cb_filter_skip", "cb_cancel");
    }

    @Test
    @DisplayName("skipCancelButtons() — ровно 2 кнопки")
    void skipCancelButtons_exactlyTwoButtons() {
        assertThat(allCallbacks(factory.skipCancelButtons())).hasSize(2);
    }

    // ── remoteChoiceButtons ───────────────────────────────────────────────────

    @Test
    @DisplayName("remoteChoiceButtons() содержит Да, Нет, Пропустить, Отмена")
    void remoteChoiceButtons_hasFourButtons() {
        List<String> callbacks = allCallbacks(factory.remoteChoiceButtons());
        assertThat(callbacks).contains(
                "cb_filter_remote_yes", "cb_filter_remote_no",
                "cb_filter_skip", "cb_cancel"
        );
    }

    // ── cancelButton ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("cancelButton() — ровно одна кнопка с cb_cancel")
    void cancelButton_singleCancelButton() {
        List<String> callbacks = allCallbacks(factory.cancelButton());
        assertThat(callbacks).containsExactly("cb_cancel");
    }

    // ── хелпер ───────────────────────────────────────────────────────────────

    private List<String> allCallbacks(InlineKeyboardMarkup markup) {
        return markup.getKeyboard().stream()
                .flatMap(row -> row.stream())
                .map(InlineKeyboardButton::getCallbackData)
                .toList();
    }
}