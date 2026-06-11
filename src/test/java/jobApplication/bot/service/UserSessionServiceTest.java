package jobApplication.bot.service;

import jobApplication.bot.dto.VacancyFilter;
import jobApplication.bot.telegram.UserState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserSessionService")
class UserSessionServiceTest {

    private UserSessionService service;

    @BeforeEach
    void setUp() {
        service = new UserSessionService();
    }

    // ── getState ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getState возвращает IDLE для нового пользователя")
    void getState_unknownUser_returnsIdle() {
        assertThat(service.getState(999L)).isEqualTo(UserState.IDLE);
    }

    @Test
    @DisplayName("getState возвращает установленное состояние")
    void getState_afterSetState_returnsCorrectState() {
        service.setState(1L, UserState.WAITING_FOR_TITLE);
        assertThat(service.getState(1L)).isEqualTo(UserState.WAITING_FOR_TITLE);
    }

    // ── setState ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("setState обновляет состояние у того же пользователя")
    void setState_updatesTwice_returnsLatest() {
        service.setState(1L, UserState.WAITING_FOR_CITY);
        service.setState(1L, UserState.WAITING_FOR_IS_REMOTE);
        assertThat(service.getState(1L)).isEqualTo(UserState.WAITING_FOR_IS_REMOTE);
    }

    @Test
    @DisplayName("setState не влияет на другого пользователя")
    void setState_doesNotAffectOtherUsers() {
        service.setState(1L, UserState.WAITING_FOR_TITLE);
        assertThat(service.getState(2L)).isEqualTo(UserState.IDLE);
    }

    // ── getFilter ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getFilter создаёт пустой фильтр для нового пользователя")
    void getFilter_newUser_returnsEmptyFilter() {
        VacancyFilter filter = service.getFilter(1L);
        assertThat(filter).isNotNull();
        assertThat(filter.getTitle()).isNull();
        assertThat(filter.getCity()).isNull();
    }

    @Test
    @DisplayName("getFilter возвращает тот же объект при повторном вызове")
    void getFilter_calledTwice_returnsSameInstance() {
        VacancyFilter first  = service.getFilter(1L);
        VacancyFilter second = service.getFilter(1L);
        assertThat(first).isSameAs(second);
    }

    @Test
    @DisplayName("getFilter сохраняет изменения в фильтре")
    void getFilter_mutationsArePersisted() {
        service.getFilter(1L).setTitle("Java");
        assertThat(service.getFilter(1L).getTitle()).isEqualTo("Java");
    }

    @Test
    @DisplayName("getFilter изолирован между пользователями")
    void getFilter_isolatedBetweenUsers() {
        service.getFilter(1L).setTitle("Java");
        assertThat(service.getFilter(2L).getTitle()).isNull();
    }

    // ── clearSession ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("clearSession сбрасывает состояние в IDLE")
    void clearSession_resetsStateToIdle() {
        service.setState(1L, UserState.WAITING_FOR_CITY);
        service.clearSession(1L);
        assertThat(service.getState(1L)).isEqualTo(UserState.IDLE);
    }

    @Test
    @DisplayName("clearSession создаёт новый фильтр после очистки")
    void clearSession_createsNewFilterAfterClear() {
        VacancyFilter before = service.getFilter(1L);
        before.setTitle("Java");
        service.clearSession(1L);
        VacancyFilter after = service.getFilter(1L);
        assertThat(after).isNotSameAs(before);
        assertThat(after.getTitle()).isNull();
    }

    @Test
    @DisplayName("clearSession не влияет на другого пользователя")
    void clearSession_doesNotAffectOtherUsers() {
        service.setState(1L, UserState.WAITING_FOR_TITLE);
        service.setState(2L, UserState.WAITING_FOR_CITY);
        service.clearSession(1L);
        assertThat(service.getState(2L)).isEqualTo(UserState.WAITING_FOR_CITY);
    }
}