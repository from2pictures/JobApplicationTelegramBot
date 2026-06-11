package jobApplication.bot.repo;

import jobApplication.bot.dto.VacancyFilter;
import jobApplication.bot.model.Vacancy;

import java.util.List;

public interface VacancyRepoCustom {
    List<Vacancy> findByFilter(VacancyFilter filter);
}
