package jobApplication.bot.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "companies")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    private String websiteUrl;
    @OneToMany(mappedBy = "company", cascade = CascadeType.PERSIST, orphanRemoval = true)
    @ToString.Exclude
    private List<Vacancy> vacancies = new ArrayList<>();
}