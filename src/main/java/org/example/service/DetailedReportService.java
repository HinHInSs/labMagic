package org.example.service;

import org.example.model.*;

import java.util.List;
import java.util.Map;

public class DetailedReportService implements ReportGenerator {

    @Override
    public String generate(Mission mission) {
        StringBuilder sb = new StringBuilder();

        sb.append("\n");
        sb.append("ОТЧЕТ ПО МИССИИ\n");
        sb.append("\n");

        sb.append("[1] ОСНОВНАЯ ИНФОРМАЦИЯ\n");
        sb.append("  ID миссии: ").append(mission.getMissionId()).append("\n");
        sb.append("  Дата: ").append(mission.getDate()).append("\n");
        sb.append("  Локация: ").append(mission.getLocation()).append("\n");
        sb.append("  Ущерб: ").append(mission.getDamageCost()).append(" иен\n");

        sb.append("\n[2] ПРОКЛЯТИЕ\n");
        if (mission.getCurse() != null) {
            sb.append("  Имя: ").append(mission.getCurse().getName()).append("\n");
            sb.append("  Уровень угрозы: ").append(mission.getCurse().getThreatLevel()).append("\n");
        }

        sb.append("\n[3] МАГИ\n");
        for (int i = 0; i < mission.getSorcerers().size(); i++) {
            Sorcerer s = mission.getSorcerers().get(i);
            sb.append("  ").append(i + 1).append(". ").append(s.getName());
            sb.append(" (").append(s.getRank()).append(")\n");
        }

        sb.append("\n[4] ТЕХНИКИ\n");
        for (int i = 0; i < mission.getTechniques().size(); i++) {
            Technique t = mission.getTechniques().get(i);
            sb.append("  ").append(i + 1).append(". ").append(t.getName()).append("\n");
            sb.append("     Тип: ").append(t.getType()).append("\n");
            sb.append("     Владелец: ").append(t.getOwner()).append("\n");
            sb.append("     Нанесенный урон: ").append(t.getDamage()).append(" иен\n");
        }

        sb.append("\n[5] РЕЗУЛЬТАТ\n");
        String outcomeStr;
        switch (mission.getOutcome()) {
            case SUCCESS:
                outcomeStr = "УСПЕХ";
                break;
            case PARTIAL_SUCCESS:
                outcomeStr = "ЧАСТИЧНЫЙ УСПЕХ";
                break;
            default:
                outcomeStr = "ПРОВАЛ";
                break;
        }
        sb.append("  Итог: ").append(outcomeStr).append("\n");

        boolean hasError = false;
        StringBuilder errors = new StringBuilder();
        for (int i = 0; i < mission.getTechniques().size(); i++) {
            boolean ownerExists = false;
            Technique t = mission.getTechniques().get(i);
            for (int j = 0; j < mission.getSorcerers().size(); j++) {
                Sorcerer s = mission.getSorcerers().get(j);
                if (t.getOwner().equals(s.getName())) {
                    ownerExists = true;
                    break;
                }
            }
            if (!ownerExists) {
                hasError = true;
                errors.append(" у техники ").append(t.getName()).append(" владелец ").append(t.getOwner()).append(" которого нет в списке магов");
            }
        }

        if (hasError) {
            sb.append("\nОшибка:").append(errors.toString()).append("\n");
        }

        if (mission.getComment() != null && !mission.getComment().isEmpty()) {
            sb.append("\n[6] КОММЕНТАРИЙ\n");
            sb.append("  ").append(mission.getComment()).append("\n");
        }

        if (mission.getExtensions() != null && !mission.getExtensions().isEmpty()) {
            sb.append("\n[7] ДОПОЛНИТЕЛЬНАЯ ИНФОРМАЦИЯ\n");

            for (Map.Entry<String, Object> entry : mission.getExtensions().entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                sb.append("  ").append(key).append(":\n");

                if (value instanceof Map) {
                    Map<?, ?> map = (Map<?, ?>) value;
                    for (Map.Entry<?, ?> me : map.entrySet()) {
                        sb.append("    ").append(me.getKey()).append(": ").append(me.getValue()).append("\n");
                    }
                } else if (value instanceof List) {
                    List<?> list = (List<?>) value;
                    for (int i = 0; i < list.size(); i++) {
                        Object item = list.get(i);
                        if (item instanceof Map) {
                            sb.append("    ").append(i + 1).append(".\n");
                            Map<?, ?> map = (Map<?, ?>) item;
                            for (Map.Entry<?, ?> me : map.entrySet()) {
                                sb.append("       ").append(me.getKey()).append(": ").append(me.getValue()).append("\n");
                            }
                        } else {
                            sb.append("    ").append(i + 1).append(". ").append(item).append("\n");
                        }
                    }
                } else {
                    sb.append("    ").append(value).append("\n");
                }
            }
        }

        return sb.toString();
    }
}