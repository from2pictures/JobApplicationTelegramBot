package jobApplication.bot.dto;

public record ApplyOptionDTO(
        String apply_link,
        Boolean is_direct,
        String publisher
) {}