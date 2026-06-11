package jobApplication.bot.repo;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jobApplication.bot.dto.VacancyFilter;
import jobApplication.bot.model.QVacancy;
import jobApplication.bot.model.Vacancy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class VacancyRepoImpl implements VacancyRepoCustom {

    private final JPAQueryFactory queryFactory;
    private static final QVacancy vacancy = QVacancy.vacancy;

    @Override
    public List<Vacancy> findByFilter(VacancyFilter filter) {
        BooleanBuilder where = buildWhere(filter);

        return queryFactory
                .selectFrom(vacancy)
                .leftJoin(vacancy.company).fetchJoin()
                .leftJoin(vacancy.city).fetchJoin()
                .where(where)
                .orderBy(vacancy.id.desc())
                .fetch();
    }

    private BooleanBuilder buildWhere(VacancyFilter filter) {
        BooleanBuilder where = new BooleanBuilder();

        if (filter.getTitle() != null && !filter.getTitle().isBlank())
            where.and(vacancy.title.containsIgnoreCase(filter.getTitle()));

        if (filter.getIsRemote() != null)
            where.and(vacancy.isRemote.eq(filter.getIsRemote()));

        if (filter.getMinSalary() != null)
            where.and(vacancy.salary.goe(filter.getMinSalary())
                    .or(vacancy.minSalary.goe(filter.getMinSalary())));

        if (filter.getCity() != null && !filter.getCity().isBlank())
            where.and(vacancy.city.name.containsIgnoreCase(filter.getCity()));

        if (filter.getCountry() != null && !filter.getCountry().isBlank()) {
            where.and(vacancy.city.country.containsIgnoreCase(filter.getCountry()));
        }
        return where;
    }
}
