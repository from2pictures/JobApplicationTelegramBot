package jobApplication.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
public class VacancyFilter {
    private String title;
    private String company;
    private String city;
    private String country;
    private Integer minSalary;  // нет в api
    private Boolean isRemote;
}