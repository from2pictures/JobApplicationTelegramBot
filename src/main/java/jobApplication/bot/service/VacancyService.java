package jobApplication.bot.service;

import jobApplication.bot.dto.VacancyDTO;
import jobApplication.bot.dto.VacancyExportDTO;
import jobApplication.bot.dto.VacancyFilter;
import jobApplication.bot.mapper.VacancyMapper;
import jobApplication.bot.model.City;
import jobApplication.bot.model.Company;
import jobApplication.bot.model.Vacancy;
import jobApplication.bot.repo.CityRepo;
import jobApplication.bot.repo.CompanyRepo;
import jobApplication.bot.repo.VacancyRepo;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class VacancyService {

    private final VacancyRepo vacancyRepo;
    private final CityRepo cityRepo;
    private final CompanyRepo companyRepo;
    private final VacancyMapper vacancyMapper;

    public VacancyExportDTO getVacancyById(long id) {
        return vacancyRepo.findById(id).map(vacancyMapper::toVacancyExportDTO).orElse(null);
    }

    public List<VacancyExportDTO> getAllVacancies() {
        return vacancyRepo.findAll().stream().map(vacancyMapper::toVacancyExportDTO).toList();
    }

    public Vacancy addVacancy(Vacancy vacancy) {
        return vacancyRepo.save(vacancy);
    }

    public Vacancy saveFromDto(VacancyDTO dto) {
        Vacancy vacancy = vacancyMapper.toVacancy(dto);

        Company company = companyRepo.findByName(dto.employer_name())
                .orElseGet(() -> companyRepo.save(vacancyMapper.toCompany(dto)));
        vacancy.setCompany(company);
        company.getVacancies().add(vacancy);

        if (dto.job_city() != null) {
            City city = cityRepo.findByNameAndCountry(dto.job_city(), dto.job_country())
                    .orElseGet(() -> cityRepo.save(vacancyMapper.toCity(dto)));
            vacancy.setCity(city);
            city.getVacancies().add(vacancy);
        }

        return vacancyRepo.save(vacancy);
    }

    public List<Vacancy> saveFromListOfDto(@NonNull List<VacancyDTO> vacancyDTOS) {
        List<Vacancy> vacancies = new ArrayList<>();
        for (VacancyDTO dto : vacancyDTOS) {
            Vacancy vacancy = vacancyMapper.toVacancy(dto);

            Company company = companyRepo.findByName(dto.employer_name())
                    .orElseGet(() -> companyRepo.save(vacancyMapper.toCompany(dto)));
            vacancy.setCompany(company);
            company.getVacancies().add(vacancy);

            if (dto.job_city() != null) {
                City city = cityRepo.findByNameAndCountry(dto.job_city(), dto.job_country())
                        .orElseGet(() -> cityRepo.save(vacancyMapper.toCity(dto)));
                vacancy.setCity(city);
                city.getVacancies().add(vacancy);
            }
            vacancies.add(vacancy);
        }
        return vacancyRepo.saveAll(vacancies);
    }

    @Transactional(readOnly = true)
    public Optional<Vacancy> getVacancyWithRelations(String jobId) {
        return vacancyRepo.findByJobIdWithRelations(jobId);
    }

    public List<Vacancy> addListOfVacancies(List<Vacancy> vacancies) {
        return vacancyRepo.saveAll(vacancies);
    }

    public void deleteVacancyById(long id) {
        vacancyRepo.deleteById(id);
    }

    public List<VacancyExportDTO> getAllForExport() {
        return vacancyRepo.findAllForExport();
    }

    public Vacancy updateVacancy(long id, Vacancy updatedVacancy) {
        Vacancy existing = vacancyRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vacancy not found: " + id));
        if (updatedVacancy.getTitle() != null) existing.setTitle(updatedVacancy.getTitle());
        if (updatedVacancy.getDescription() != null) existing.setDescription(updatedVacancy.getDescription());
        if (updatedVacancy.getSalary() != null) existing.setSalary(updatedVacancy.getSalary());
        if (updatedVacancy.getMinSalary() != null) existing.setMinSalary(updatedVacancy.getMinSalary());
        if (updatedVacancy.getMaxSalary() != null) existing.setMaxSalary(updatedVacancy.getMaxSalary());
        if (updatedVacancy.getStringSalary() != null) existing.setStringSalary(updatedVacancy.getStringSalary());
        if (updatedVacancy.getCity() != null) existing.setCity(updatedVacancy.getCity());
        if (updatedVacancy.getCompany() != null) existing.setCompany(updatedVacancy.getCompany());
        if (updatedVacancy.getPublishDate() != null) existing.setPublishDate(updatedVacancy.getPublishDate());
        if (updatedVacancy.getLinkToOriginal() != null) existing.setLinkToOriginal(updatedVacancy.getLinkToOriginal());

        return vacancyRepo.save(existing);
    }

    public List<Vacancy> searchWithFilter(VacancyFilter filter) {
        return vacancyRepo.findByFilter(filter);
    }
}