package jobApplication.bot.dto;

import java.time.LocalDate;

public record VacancyExportDTO(
        Long id,
        String title,
        String description,
        String companyName,
        String cityName,
        String country,
        Integer salaryMin,
        Integer salaryMax,
        String stringSalary,
        String url,
        LocalDate createdAt,
        Boolean isRemote
) {}