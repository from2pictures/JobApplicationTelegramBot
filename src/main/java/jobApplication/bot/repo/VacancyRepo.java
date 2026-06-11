package jobApplication.bot.repo;

import jobApplication.bot.dto.VacancyExportDTO;
import jobApplication.bot.model.Vacancy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VacancyRepo extends JpaRepository<Vacancy, Long>, VacancyRepoCustom {
    @EntityGraph(attributePaths = {"company", "city"})
    @Query("SELECT v FROM Vacancy v WHERE v.id = :vacancy_id")
    Optional<Vacancy> findByJobIdWithRelations(@Param("vacancy_id") String vacancy_id);

    @EntityGraph(attributePaths = {"company", "city"})
    @Query("SELECT v FROM Vacancy v")
    List<Vacancy> findAllWithRelations();

    @Query("""
        SELECT new jobApplication.bot.dto.VacancyExportDTO(
            v.id, v.title, v.description,
            c.name, ct.name, ct.country,
            v.minSalary, v.maxSalary, v.stringSalary,
            v.linkToOriginal, v.publishDate, v.isRemote
        )
        FROM Vacancy v
        JOIN v.company c
        JOIN v.city ct
    """)
    List<VacancyExportDTO> findAllForExport();
}