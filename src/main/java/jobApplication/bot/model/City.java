package jobApplication.bot.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "cities")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column
    private String state;
    @Column(nullable = false)
    private String country;
    @ToString.Exclude
    @OneToMany(mappedBy = "city", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Vacancy> vacancies = new ArrayList<>();
}