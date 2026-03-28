package org.example.service;

import org.example.model.*;

public class ReportService {

    public String getReport(Mission mission) {
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
        if (mission.getOutcome() == MissionOutcome.SUCCESS) {
            outcomeStr = "УСПЕХ";
        } else {
            outcomeStr = "ПРОВАЛ";
        }
        sb.append("  Итог: ").append(outcomeStr).append("\n");

        if (mission.getComment() != null && !mission.getComment().isEmpty()) {
            sb.append("\n[6] КОММЕНТАРИЙ\n");
            sb.append("  ").append(mission.getComment()).append("\n");
        }

        return sb.toString();
    }
}