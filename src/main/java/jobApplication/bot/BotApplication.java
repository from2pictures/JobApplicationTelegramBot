package jobApplication.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import jobApplication.bot.dto.ApiResponseDTO;

import jobApplication.bot.service.VacancyService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootApplication
@EnableAsync
public class BotApplication {
	public static void main(String[] args) throws IOException {
		ApplicationContext context = SpringApplication.run(BotApplication.class, args);

		String json = Files.readString(Paths.get("src/main/resources/example.json"));
		ObjectMapper objectMapper = new ObjectMapper();
		ApiResponseDTO apiResponseDTO = objectMapper.readValue(json, ApiResponseDTO.class);
		VacancyService service = context.getBean(VacancyService.class);
		service.saveFromListOfDto(apiResponseDTO.data().jobs());
	}
}