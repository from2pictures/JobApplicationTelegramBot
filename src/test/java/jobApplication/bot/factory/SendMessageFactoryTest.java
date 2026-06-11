package jobApplication.bot.factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("SendMessageFactory")
@ExtendWith(MockitoExtension.class)
class SendMessageFactoryTest {

    @Mock  KeyboardFactory keyboardFactory;
    @InjectMocks SendMessageFactory factory;

    private static final long CHAT_ID = 123L;
    private static final String TEXT  = "Привет!";

    ReplyKeyboardMarkup  replyMarkup;
    InlineKeyboardMarkup inlineMarkup;

    @BeforeEach
    void setUp() {
        replyMarkup  = mock(ReplyKeyboardMarkup.class);
        inlineMarkup = mock(InlineKeyboardMarkup.class);
    }

    @Test
    @DisplayName("plain() — правильный chatId, текст, parseMode=HTML, без клавиатуры")
    void plain_basicProperties() {
        SendMessage msg = factory.plain(CHAT_ID, TEXT);
        assertThat(msg.getChatId()).isEqualTo(String.valueOf(CHAT_ID));
        assertThat(msg.getText()).isEqualTo(TEXT);
        assertThat(msg.getParseMode()).isEqualTo("HTML");
        assertThat(msg.getReplyMarkup()).isNull();
        verifyNoInteractions(keyboardFactory);
    }

    @Test
    @DisplayName("withMainMenu() — прикрепляет Reply-клавиатуру из KeyboardFactory")
    void withMainMenu_attachesReplyKeyboard() {
        when(keyboardFactory.startMenu()).thenReturn(replyMarkup);
        SendMessage msg = factory.withMainMenu(CHAT_ID, TEXT);
        assertThat(msg.getReplyMarkup()).isSameAs(replyMarkup);
        assertThat(msg.getText()).isEqualTo(TEXT);
        verify(keyboardFactory).startMenu();
    }

    @Test
    @DisplayName("withSearchSourceMenu() — прикрепляет inline-клавиатуру выбора источника")
    void withSearchSourceMenu_attachesInlineKeyboard() {
        when(keyboardFactory.searchSourceMenu()).thenReturn(inlineMarkup);
        SendMessage msg = factory.withSearchSourceMenu(CHAT_ID, TEXT);
        assertThat(msg.getReplyMarkup()).isSameAs(inlineMarkup);
        verify(keyboardFactory).searchSourceMenu();
    }

    @Test
    @DisplayName("withExportMenu() — прикрепляет inline-клавиатуру экспорта")
    void withExportMenu_attachesInlineKeyboard() {
        when(keyboardFactory.exportMenu()).thenReturn(inlineMarkup);
        SendMessage msg = factory.withExportMenu(CHAT_ID, TEXT);
        assertThat(msg.getReplyMarkup()).isSameAs(inlineMarkup);
        verify(keyboardFactory).exportMenu();
    }

    @Test
    @DisplayName("withSkipCancelButtons() — прикрепляет кнопки Пропустить/Отмена")
    void withSkipCancelButtons_attachesInlineKeyboard() {
        when(keyboardFactory.skipCancelButtons()).thenReturn(inlineMarkup);
        SendMessage msg = factory.withSkipCancelButtons(CHAT_ID, TEXT);
        assertThat(msg.getReplyMarkup()).isSameAs(inlineMarkup);
        verify(keyboardFactory).skipCancelButtons();
    }

    @Test
    @DisplayName("withRemoteChoiceButtons() — прикрепляет кнопки Да/Нет/Пропустить")
    void withRemoteChoiceButtons_attachesInlineKeyboard() {
        when(keyboardFactory.remoteChoiceButtons()).thenReturn(inlineMarkup);
        SendMessage msg = factory.withRemoteChoiceButtons(CHAT_ID, TEXT);
        assertThat(msg.getReplyMarkup()).isSameAs(inlineMarkup);
        verify(keyboardFactory).remoteChoiceButtons();
    }

    @Test
    @DisplayName("withCancelButton() — прикрепляет кнопку Отмена")
    void withCancelButton_attachesInlineKeyboard() {
        when(keyboardFactory.cancelButton()).thenReturn(inlineMarkup);
        SendMessage msg = factory.withCancelButton(CHAT_ID, TEXT);
        assertThat(msg.getReplyMarkup()).isSameAs(inlineMarkup);
        verify(keyboardFactory).cancelButton();
    }

    @Test
    @DisplayName("все методы проставляют parseMode=HTML")
    void allMethods_setHtmlParseMode() {
        when(keyboardFactory.startMenu()).thenReturn(replyMarkup);
        when(keyboardFactory.searchSourceMenu()).thenReturn(inlineMarkup);
        when(keyboardFactory.exportMenu()).thenReturn(inlineMarkup);
        when(keyboardFactory.skipCancelButtons()).thenReturn(inlineMarkup);
        when(keyboardFactory.remoteChoiceButtons()).thenReturn(inlineMarkup);
        when(keyboardFactory.cancelButton()).thenReturn(inlineMarkup);

        assertThat(factory.plain(CHAT_ID, TEXT).getParseMode()).isEqualTo("HTML");
        assertThat(factory.withMainMenu(CHAT_ID, TEXT).getParseMode()).isEqualTo("HTML");
        assertThat(factory.withSearchSourceMenu(CHAT_ID, TEXT).getParseMode()).isEqualTo("HTML");
        assertThat(factory.withExportMenu(CHAT_ID, TEXT).getParseMode()).isEqualTo("HTML");
        assertThat(factory.withSkipCancelButtons(CHAT_ID, TEXT).getParseMode()).isEqualTo("HTML");
        assertThat(factory.withRemoteChoiceButtons(CHAT_ID, TEXT).getParseMode()).isEqualTo("HTML");
        assertThat(factory.withCancelButton(CHAT_ID, TEXT).getParseMode()).isEqualTo("HTML");
    }
}