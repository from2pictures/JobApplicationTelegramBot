package jobApplication.bot.telegram;

import jobApplication.bot.dto.SalaryStatsDTO;
import jobApplication.bot.factory.SendMessageFactory;
import jobApplication.bot.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StatsHandler {

    private final StatsService statsService;
    private final SendMessageFactory sendMessageFactory;

    public SendMessage getStats(long chatId) {
        List<SalaryStatsDTO> stats = statsService.getSalaryStatsByCity();

        if (stats.isEmpty()) {
            return sendMessageFactory.withMainMenu(chatId,
                    "📊 Статистика пока пуста. Сначала добавь вакансии!");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("📊 <b>Статистика зарплат по городам</b>\n\n");

        int index = 1;
        for (SalaryStatsDTO s : stats) {
            String location = s.cityName() + (s.country() != null ? ", " + s.country() : "");
            sb.append("<b>").append(index++).append(". ").append(esc(location)).append("</b>\n");
            sb.append("   📋 Всего вакансий: ").append(s.totalVacancies());
            if (s.withSalary() != null && s.withSalary() > 0) {
                sb.append(" | 💼 С зарплатой: ").append(s.withSalary());
            }
            sb.append("\n");

            if (s.avgSalary() != null) {
                sb.append("   💰 Средняя: <b>").append(format(s.avgSalary())).append("</b>");
                if (s.minSalary() != null) sb.append(" | Мин: ").append(format(s.minSalary()));
                if (s.maxSalary() != null) sb.append(" | Макс: ").append(format(s.maxSalary()));
                sb.append("\n");
            } else {
                sb.append("   💰 Зарплаты не указаны\n");
            }
            sb.append("\n");
        }

        return sendMessageFactory.withMainMenu(chatId, sb.toString());
    }

    private String format(Integer salary) {
        if (salary == null) return "—";
        return String.format("%,d$".replace(",", " "), salary).trim();
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}