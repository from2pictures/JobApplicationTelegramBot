package jobApplication.bot.service;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jobApplication.bot.dto.SalaryStatsDTO;
import jobApplication.bot.model.QVacancy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {

    private final JPAQueryFactory queryFactory;

    public List<SalaryStatsDTO> getSalaryStatsByCity() {
        QVacancy v = QVacancy.vacancy;

        List<Tuple> tuples = queryFactory
                .select(
                        v.city.name,
                        v.city.country,
                        v.count(),
                        v.salary.count(),
                        v.salary.avg(),
                        v.salary.min(),
                        v.salary.max()
                )
                .from(v)
                .where(v.city.isNotNull())
                .groupBy(v.city.id, v.city.name, v.city.country)
                .orderBy(v.salary.avg().coalesce((double) 0).desc())
                .fetch();

        return tuples.stream()
                .map(t -> new SalaryStatsDTO(
                        t.get(v.city.name),
                        t.get(v.city.country),
                        t.get(v.count()),
                        t.get(v.salary.count()),
                        toInt(t.get(v.salary.avg())),
                        t.get(v.salary.min()),
                        t.get(v.salary.max())
                ))
                .toList();
    }

    private Integer toInt(Number number) {
        return number == null ? null : number.intValue();
    }
}