package jobApplication.bot.dto;

public record SalaryStatsDTO(
        String cityName,
        String country,
        Long totalVacancies,
        Long withSalary,
        Integer avgSalary,
        Integer minSalary,
        Integer maxSalary
) {}