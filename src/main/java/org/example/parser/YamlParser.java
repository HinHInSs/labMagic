package org.example.parser;

import org.example.model.MissionBuilder;
import org.example.model.MissionOutcome;
import org.yaml.snakeyaml.Yaml;

import java.util.List;
import java.util.Map;

public class YamlParser extends BaseParser {

    private final Yaml yaml = new Yaml();

    @Override
    protected void parseContent(String content, MissionBuilder builder) throws Exception {
        Map<String, Object> data = yaml.load(content);

        builder.setMissionId((String) data.get("missionId"));

        Object dateObj = data.get("date");
        if (dateObj != null) {
            builder.setDate(dateObj.toString());
        }

        builder.setLocation((String) data.get("location"));

        String outcome = (String) data.get("outcome");
        if (outcome != null) {
            switch (outcome) {
                case "SUCCESS":
                    builder.setOutcome(MissionOutcome.SUCCESS);
                    break;
                case "PARTIAL_SUCCESS":
                    builder.setOutcome(MissionOutcome.PARTIAL_SUCCESS);
                    break;
                default:
                    builder.setOutcome(MissionOutcome.FAILURE);
                    break;
            }
        }

        if (data.containsKey("damageCost")) {
            Object damageObj = data.get("damageCost");
            if (damageObj instanceof Integer) {
                builder.setDamageCost((Integer) damageObj);
            }
        }

        Map<String, String> curse = (Map<String, String>) data.get("curse");
        if (curse != null) {
            if (curse.containsKey("name")) {
                builder.setCurseName(curse.get("name"));
            }
            if (curse.containsKey("threatLevel")) {
                String threatLevel = curse.get("threatLevel");
                switch (threatLevel) {
                    case "HIGH": threatLevel = "ВЫСОКИЙ"; break;
                    case "MEDIUM": threatLevel = "СРЕДНИЙ"; break;
                    case "LOW": threatLevel = "НИЗКИЙ"; break;
                    case "SPECIAL_GRADE": threatLevel = "ОСОБЫЙ РАНГ"; break;
                }
                builder.setCurseThreatLevel(threatLevel);
            }
        }

        if (data.containsKey("comment")) {
            builder.setComment((String) data.get("comment"));
        }

        List<Map<String, String>> sorcerers = (List<Map<String, String>>) data.get("sorcerers");
        if (sorcerers != null) {
            for (Map<String, String> s : sorcerers) {
                String name = s.get("name");
                String rank = s.get("rank");
                if (name != null && rank != null) {
                    switch (rank) {
                        case "GRADE_1": rank = "1 РАНГ"; break;
                        case "GRADE_2": rank = "2 РАНГ"; break;
                        case "SEMI_GRADE_1": rank = "ПОЛУРАНГ 1"; break;
                    }
                    builder.setSorcerer(name, rank);
                }
            }
        }

        List<Map<String, Object>> techniques = (List<Map<String, Object>>) data.get("techniques");
        if (techniques != null) {
            for (Map<String, Object> t : techniques) {
                String name = (String) t.get("name");
                String type = (String) t.get("type");
                String owner = (String) t.get("owner");
                int damage = t.containsKey("damage") ? (Integer) t.get("damage") : 0;
                if (name != null && type != null && owner != null) {
                    switch (type) {
                        case "INNATE": type = "ВРОЖДЕННАЯ"; break;
                        case "SHIKIGAMI": type = "ШИКИГАМИ"; break;
                        case "WEAPON": type = "ОРУЖИЕ"; break;
                        case "BODY": type = "ТЕЛЕСНАЯ"; break;
                    }
                    builder.setTechnique(name, type, owner, damage);
                }
            }
        }

        if (data.containsKey("economicAssessment")) {
            Map<String, Object> economic = (Map<String, Object>) data.get("economicAssessment");
            Map<String, String> rusEconomic = new java.util.HashMap<>();
            if (economic.containsKey("totalDamageCost")) {
                rusEconomic.put("Общий ущерб", economic.get("totalDamageCost").toString());
            }
            if (economic.containsKey("infrastructureDamage")) {
                rusEconomic.put("Ущерб инфраструктуре", economic.get("infrastructureDamage").toString());
            }
            if (economic.containsKey("commercialDamage")) {
                rusEconomic.put("Коммерческий ущерб", economic.get("commercialDamage").toString());
            }
            if (economic.containsKey("transportDamage")) {
                rusEconomic.put("Ущерб транспорту", economic.get("transportDamage").toString());
            }
            if (economic.containsKey("recoveryEstimateDays")) {
                rusEconomic.put("Дней на восстановление", economic.get("recoveryEstimateDays").toString());
            }
            if (economic.containsKey("insuranceCovered")) {
                rusEconomic.put("Страховое покрытие", economic.get("insuranceCovered").toString());
            }
            builder.addExtension("Экономическая оценка", rusEconomic);
        }
    }
}