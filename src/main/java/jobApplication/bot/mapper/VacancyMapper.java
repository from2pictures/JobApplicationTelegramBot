package jobApplication.bot.mapper;

import jobApplication.bot.dto.VacancyDTO;
import jobApplication.bot.model.City;
import jobApplication.bot.model.Company;
import jobApplication.bot.model.Vacancy;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.ZoneId;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface VacancyMapper {

    @Mapping(source = "job_title", target = "title")
    @Mapping(source = "job_salary_string", target = "stringSalary")
    @Mapping(source = "job_salary", target = "salary")
    @Mapping(source = "job_min_salary", target = "minSalary")
    @Mapping(source = "job_max_salary", target = "maxSalary")
    @Mapping(source = "job_description", target = "description")
    @Mapping(source = "job_apply_link", target = "linkToOriginal")
    @Mapping(source = "job_is_remote", target = "isRemote")

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "city", ignore = true)

    @Mapping(target = "publishDate", expression = "java(dto.job_posted_at_datetime_utc() != null ? java.time.Instant.parse(dto.job_posted_at_datetime_utc()).atZone(java.time.ZoneId.systemDefault()).toLocalDate() : null)")
    Vacancy toVacancy(VacancyDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vacancies", ignore = true)
    @Mapping(source = "employer_name", target = "name")
    @Mapping(source = "employer_website", target = "websiteUrl")
    Company toCompany(VacancyDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vacancies", ignore = true)
    @Mapping(source = "job_city", target = "name")
    @Mapping(source = "job_state", target = "state")
    @Mapping(source = "job_country", target = "country")
    City toCity(VacancyDTO dto);
}
