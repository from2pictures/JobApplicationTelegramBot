package jobApplication.bot.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatId;
    private String firstName;
    private String lastName;
    private String username;
    private String language;
    @ManyToOne
    @JoinColumn(name = "city_id")
    private City interestingCity;
    @ManyToMany
    @JoinTable(
            name = "users_favorite_vacancies",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "vacancy_id")
    )
    private List<Vacancy> favoriteVacancies = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "users_viewed_vacancies",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "vacancy_id")
    )
    private List<Vacancy> viewedVacancies = new ArrayList<>();
}
