package jobApplication.bot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jobApplication.bot.dto.VacancyExportDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final ObjectMapper objectMapper;

    public byte[] toJson(List<VacancyExportDTO> vacancies) throws JsonProcessingException {
        return objectMapper.writeValueAsString(vacancies).getBytes(StandardCharsets.UTF_8);
    }

    public byte[] toCsv(List<VacancyExportDTO> vacancies) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (OutputStreamWriter w = new OutputStreamWriter(baos, StandardCharsets.UTF_8)) {
            w.write('\uFEFF');
            w.write("ID,Должность,Компания,Город,Страна,Зарплата,Удалённо,Дата,Ссылка\n");
            for (VacancyExportDTO v : vacancies) {
                w.write(String.join(",",
                        csvField(String.valueOf(v.id())), csvField(v.title()), csvField(v.companyName()),
                        csvField(v.cityName()), csvField(v.country()), csvField(v.stringSalary()),
                        csvField(Boolean.TRUE.equals(v.isRemote()) ? "Да" : "Нет"),
                        csvField(v.createdAt() != null ? v.createdAt().format(DATE_FMT) : ""),
                        csvField(v.url())
                ) + "\n");
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка генерации CSV", e);
        }
        return baos.toByteArray();
    }

    public byte[] toHtml(List<VacancyExportDTO> vacancies) {
        StringBuilder sb = new StringBuilder();
        sb.append("""
                <!DOCTYPE html>
                <html lang="ru">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <title>Вакансии</title>
                  <style>
                    *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
                    body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
                           background: #f5f7fa; color: #1a1a2e; padding: 32px 16px; }
                    h1 { font-size: 1.6rem; font-weight: 700; margin-bottom: 8px; }
                    .subtitle { color: #666; font-size: 0.9rem; margin-bottom: 32px; }
                    .grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
                            gap: 20px; }
                    .card { background: #fff; border-radius: 12px; padding: 20px 24px;
                            box-shadow: 0 2px 8px rgba(0,0,0,.07); display: flex;
                            flex-direction: column; gap: 8px; }
                    .card-title { font-size: 1.05rem; font-weight: 600; }
                    .card-company { font-size: 0.9rem; color: #555; }
                    .tags { display: flex; flex-wrap: wrap; gap: 6px; margin-top: 4px; }
                    .tag { font-size: 0.78rem; padding: 3px 10px; border-radius: 20px;
                           background: #eef2ff; color: #4361ee; font-weight: 500; }
                    .tag.remote { background: #e8faf0; color: #2d9e5f; }
                    .tag.salary { background: #fff8e6; color: #c97a00; }
                    .desc { font-size: 0.85rem; color: #666; line-height: 1.5;
                            display: -webkit-box; -webkit-line-clamp: 3;
                            -webkit-box-orient: vertical; overflow: hidden; }
                    .card-link { margin-top: auto; padding-top: 12px; font-size: 0.85rem; }
                    .card-link a { color: #4361ee; text-decoration: none; font-weight: 500; }
                    .card-link a:hover { text-decoration: underline; }
                    .empty { text-align: center; color: #999; padding: 60px 0; }
                  </style>
                </head>
                <body>
                  <h1>💼 Вакансии</h1>
                  <p class="subtitle">Найдено: """).append(vacancies.size()).append(" вакансий</p>\n");

        if (vacancies.isEmpty()) {
            sb.append("  <p class=\"empty\">Нет вакансий для отображения</p>\n");
        } else {
            sb.append("  <div class=\"grid\">\n");
            for (VacancyExportDTO v : vacancies) {
                sb.append("    <div class=\"card\">\n");
                sb.append("      <div class=\"card-title\">").append(esc(v.title())).append("</div>\n");
                if (v.companyName() != null) {
                    sb.append("      <div class=\"card-company\">🏢 ").append(esc(v.companyName())).append("</div>\n");
                }
                sb.append("      <div class=\"tags\">\n");
                if (v.cityName() != null) {
                    String loc = v.cityName() + (v.country() != null ? ", " + v.country() : "");
                    sb.append("        <span class=\"tag\">📍 ").append(esc(loc)).append("</span>\n");
                }
                if (v.stringSalary() != null) {
                    sb.append("        <span class=\"tag salary\">💰 ").append(esc(v.stringSalary())).append("</span>\n");
                }
                if (Boolean.TRUE.equals(v.isRemote())) {
                    sb.append("        <span class=\"tag remote\">🏠 Удалённо</span>\n");
                }
                if (v.createdAt() != null) {
                    sb.append("        <span class=\"tag\">📅 ").append(v.createdAt().format(DATE_FMT)).append("</span>\n");
                }
                sb.append("      </div>\n");
                if (v.description() != null && !v.description().isBlank()) {
                    sb.append("      <p class=\"desc\">").append(esc(v.description())).append("</p>\n");
                }
                if (v.url() != null && !v.url().isBlank()) {
                    sb.append("      <div class=\"card-link\"><a href=\"").append(esc(v.url()))
                            .append("\" target=\"_blank\">Открыть вакансию →</a></div>\n");
                }
                sb.append("    </div>\n");
            }
            sb.append("  </div>\n");
        }
        sb.append("</body>\n</html>");
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String csvField(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }
}