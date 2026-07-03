package jobApplication.bot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jobApplication.bot.dto.VacancyExportDTO;
import jobApplication.bot.model.Vacancy;
import jobApplication.bot.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vacancies")
@RequiredArgsConstructor
@Tag(name = "Вакансии", description = "API для работы с вакансиями")
public class VacancyController {

    private final VacancyService service;

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить вакансию по ID",
            description = "Возвращает полные данные вакансии, если она найден в базе"
    )
    public ResponseEntity<VacancyExportDTO> getVacancy(@PathVariable Long id) {
        return ResponseEntity.ok(service.getVacancyById(id));
    }

    @Operation(
            summary = "Получить все вакансии",
            description = "Возвращает полные данные всех вакансий"
    )
    @GetMapping
    public ResponseEntity<List<VacancyExportDTO>> getAllVacancies() {
        return ResponseEntity.ok(service.getAllVacancies());
    }

    @Operation(
            summary = "Добавить вакансию",
            description = "Принимает вакансию в теле запроса и добавляет в БД"
    )
    @PostMapping
    public ResponseEntity<Vacancy> addVacancy(@RequestBody Vacancy vacancy) {
        Vacancy saved = service.addVacancy(vacancy);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(
            summary = "Добавить список вакансий",
            description = "Принимает сразу много вакансий в теле запроса и добавляет в БД"
    )
    @PostMapping("/batch")
    public ResponseEntity<List<Vacancy>> addListOfVacancies(@RequestBody List<Vacancy> vacancies) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addListOfVacancies(vacancies));
    }

    @Operation(
            summary = "Удалить вакансию по ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Заказ успешно удален")
            },
            description = "Удаляет вакансию по ее ID, если она найден в базе"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVacancyById(@PathVariable Long id) {
        service.deleteVacancyById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Обновить вакансию по ID",
            description = "Меняет данные вакансии с нужным ID на обновленные поля вакансии из тела запроса"
    )
    @PatchMapping("/{id}")
    public ResponseEntity<Vacancy> updateVacancy(@PathVariable Long id, @RequestBody Vacancy vacancy) {
        return ResponseEntity.ok(service.updateVacancy(id, vacancy));
    }
}