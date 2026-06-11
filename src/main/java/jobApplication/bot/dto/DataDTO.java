package jobApplication.bot.dto;

import java.util.List;

public record DataDTO(
        List<VacancyDTO> jobs,
        String cursor
) {}