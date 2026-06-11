package jobApplication.bot.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "vacancies")
public class Vacancy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;
    private Integer salary;
    private Integer minSalary;
    private Integer maxSalary;
    private String stringSalary;
    @Column(columnDefinition = "TEXT")
    private String description;
    private LocalDate publishDate;
    private String linkToOriginal;
    @Column
    private Boolean isRemote;


    public String toTelegramMessage() {
        StringBuilder sb = new StringBuilder();

        String safeTitle = title != null ? title : "Без названия";
        sb.append("📌 <b>").append(escapeHtml(safeTitle)).append("</b>\n\n");

        String companyStr = (company != null && company.getName() != null) ? company.getName() : "Не указана";
        sb.append("🏢 Компания: ").append(escapeHtml(companyStr)).append("\n");

        String locationStr = "Не указана";
        if (city != null) {
            List<String> parts = new ArrayList<>(3);
            if (city.getName() != null) parts.add(city.getName());
            if (city.getState() != null) parts.add(city.getState());
            if (city.getCountry() != null) parts.add(city.getCountry());
            if (!parts.isEmpty()) locationStr = String.join(", ", parts);
        }
        sb.append("📍 Локация: ").append(escapeHtml(locationStr)).append("\n");

        String remoteStr = Boolean.TRUE.equals(isRemote) ? "✅ Удалённая работа"
                : Boolean.FALSE.equals(isRemote) ? "🏢 Офис / Гибрид"
                : "🌐 Не указано";
        sb.append("🖥 Формат: ").append(remoteStr).append("\n");

        String salaryStr = "Не указана";
        if (stringSalary != null && !stringSalary.trim().isEmpty()) {
            salaryStr = stringSalary;
        } else if (minSalary != null && maxSalary != null) {
            salaryStr = minSalary + " – " + maxSalary + "$";
        } else if (salary != null) {
            salaryStr = String.valueOf(salary);
        }
        sb.append("💰 Зарплата: ").append(escapeHtml(salaryStr)).append("\n");

        sb.append("📅 Дата: ").append(escapeHtml(publishDate != null ? publishDate.toString() : "Не указана")).append("\n\n");

        if (description != null && !description.trim().isEmpty()) {
            String desc = description.replaceAll("<[^>]*>", "").trim();
            if (desc.length() > 800) {
                desc = desc.substring(0, 800) + "...";
            }
            sb.append("📝 <b>Описание:</b>\n").append(escapeHtml(desc)).append("\n\n");
        }

        if (linkToOriginal != null && !linkToOriginal.trim().isEmpty()) {
            sb.append("<a href=\"").append(escapeHtml(linkToOriginal)).append("\">🔗 Открыть вакансию</a>");
        }

        return sb.toString();
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}