package jobApplication.bot.dto;

public record ApiResponseDTO(
        String status,
        String request_id,
        ParametersDTO parameters,
        DataDTO data
) {}