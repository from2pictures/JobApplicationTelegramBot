package jobApplication.bot.service;

import jobApplication.bot.dto.VacancyDTO;
import jobApplication.bot.dto.VacancyFilter;
import jobApplication.bot.mapper.VacancyMapper;
import jobApplication.bot.model.City;
import jobApplication.bot.model.Company;
import jobApplication.bot.model.Vacancy;
import jobApplication.bot.repo.CityRepo;
import jobApplication.bot.repo.CompanyRepo;
import jobApplication.bot.repo.VacancyRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("VacancyService")
@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {

    @Mock VacancyRepo  vacancyRepo;
    @Mock CityRepo     cityRepo;
    @Mock CompanyRepo  companyRepo;
    @Mock VacancyMapper vacancyMapper;
    @InjectMocks VacancyService service;

    // ── getVacancyById ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("getVacancyById()")
    class GetById {

        @Test
        @DisplayName("возвращает вакансию когда она есть")
        void found() {
            Vacancy v = new Vacancy();
            when(vacancyRepo.findById(1L)).thenReturn(Optional.of(v));
            assertThat(service.getVacancyById(1L)).isSameAs(v);
        }

        @Test
        @DisplayName("бросает IllegalArgumentException когда не найдено")
        void notFound_throws() {
            when(vacancyRepo.findById(99L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.getVacancyById(99L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("99");
        }
    }

    // ── getAllVacancies ────────────────────────────────────────────────────────

    @Test
    @DisplayName("getAllVacancies() делегирует в repo и возвращает список")
    void getAllVacancies_delegatesToRepo() {
        List<Vacancy> expected = List.of(new Vacancy(), new Vacancy());
        when(vacancyRepo.findAll()).thenReturn(expected);
        assertThat(service.getAllVacancies()).isEqualTo(expected);
    }

    // ── saveFromDto ───────────────────────────────────────────────────────────
    @Test
    @DisplayName("saveFromListOfDto() бросает NPE на null аргументе")
    void saveFromListOfDto_nullArgument_throws() {
        assertThatThrownBy(() -> service.saveFromListOfDto(null))
                .isInstanceOf(NullPointerException.class);
    }

    // ── deleteVacancyById ─────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteVacancyById() вызывает repo.deleteById")
    void deleteVacancyById_callsRepo() {
        service.deleteVacancyById(5L);
        verify(vacancyRepo).deleteById(5L);
    }

    // ── updateVacancy ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("updateVacancy()")
    class UpdateVacancy {

        @Test
        @DisplayName("обновляет только непустые поля")
        void updatesNonNullFields() {
            Vacancy existing = new Vacancy();
            existing.setTitle("Old");
            Vacancy update = new Vacancy();
            update.setTitle("New");

            when(vacancyRepo.findById(1L)).thenReturn(Optional.of(existing));
            when(vacancyRepo.save(existing)).thenReturn(existing);

            Vacancy result = service.updateVacancy(1L, update);
            assertThat(result.getTitle()).isEqualTo("New");
        }

        @Test
        @DisplayName("не затирает поля, которые null в update")
        void doesNotOverwriteWithNull() {
            Vacancy existing = new Vacancy();
            existing.setTitle("Keep");
            Vacancy update = new Vacancy(); // title == null

            when(vacancyRepo.findById(1L)).thenReturn(Optional.of(existing));
            when(vacancyRepo.save(existing)).thenReturn(existing);

            Vacancy result = service.updateVacancy(1L, update);
            assertThat(result.getTitle()).isEqualTo("Keep");
        }

        @Test
        @DisplayName("бросает IllegalArgumentException если вакансия не найдена")
        void notFound_throws() {
            when(vacancyRepo.findById(99L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.updateVacancy(99L, new Vacancy()))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // ── searchWithFilter ──────────────────────────────────────────────────────

    @Test
    @DisplayName("searchWithFilter() делегирует в repo и возвращает результат")
    void searchWithFilter_delegatesToRepo() {
        VacancyFilter filter = new VacancyFilter();
        List<Vacancy> expected = List.of(new Vacancy());
        when(vacancyRepo.findByFilter(filter)).thenReturn(expected);
        assertThat(service.searchWithFilter(filter)).isEqualTo(expected);
    }
}