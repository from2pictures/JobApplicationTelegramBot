package jobApplication.bot.controller;

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
public class VacancyController {

    private final VacancyService service;

    @GetMapping("/{id}")
    public ResponseEntity<Vacancy> getVacancy(@PathVariable Long id) {
        return ResponseEntity.ok(service.getVacancyById(id));
    }

    @GetMapping
    public ResponseEntity<List<Vacancy>> getAllVacancies() {
        return ResponseEntity.ok(service.getAllVacancies());
    }

    @PostMapping
    public ResponseEntity<Vacancy> addVacancy(@RequestBody Vacancy vacancy) {
        Vacancy saved = service.addVacancy(vacancy);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<Vacancy>> addListOfVacancies(@RequestBody List<Vacancy> vacancies) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addListOfVacancies(vacancies));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVacancyById(@PathVariable Long id) {
        service.deleteVacancyById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Vacancy> updateVacancy(@PathVariable Long id, @RequestBody Vacancy vacancy) {
        return ResponseEntity.ok(service.updateVacancy(id, vacancy));
    }
}