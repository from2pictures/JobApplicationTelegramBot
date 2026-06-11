package jobApplication.bot.dto;

public record ParametersDTO(
        String query,
        Integer num_pages,
        String country,
        String language
) {}