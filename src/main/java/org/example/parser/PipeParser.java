package org.example.parser;

import org.example.model.MissionBuilder;
import org.example.model.MissionOutcome;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class PipeParser extends BaseParser {

    @Override
    protected void parseContent(String content, MissionBuilder builder) throws Exception {
        try (BufferedReader reader = new BufferedReader(new StringReader(content))) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\|");
                if (parts.length < 2) continue;

                String eventType = parts[0].trim();

                switch (eventType) {
                    case "MISSION_CREATED":
                        if (parts.length >= 4) {
                            builder.setMissionId(parts[1].trim());
                            builder.setDate(parts[2].trim());
                            builder.setLocation(parts[3].trim());
                        }
                        break;

                    case "CURSE_DETECTED":
                        if (parts.length >= 3) {
                            builder.setCurseName(parts[1].trim());
                            builder.setCurseThreatLevel(parts[2].trim());
                        }
                        break;

                    case "SORCERER_ASSIGNED":
                        if (parts.length >= 3) {
                            builder.setSorcerer(parts[1].trim(), parts[2].trim());
                        }
                        break;

                    case "TECHNIQUE_USED":
                        if (parts.length >= 5) {
                            builder.setTechnique(
                                    parts[1].trim(),
                                    parts[2].trim(),
                                    parts[3].trim(),
                                    Integer.parseInt(parts[4].trim())
                            );
                        }
                        break;

                    case "MISSION_RESULT":
                        if (parts.length >= 2) {
                            String result = parts[1].trim();
                            switch (result) {
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

                            for (int i = 2; i < parts.length; i++) {
                                String part = parts[i].trim();
                                if (part.startsWith("damageCost=")) {
                                    int cost = Integer.parseInt(part.substring(11));
                                    builder.setDamageCost(cost);
                                    break;
                                }
                            }
                        }
                        break;

                    case "TIMELINE_EVENT":
                        if (parts.length >= 4) {
                            Map<String, String> event = new HashMap<>();
                            event.put("Время", parts[1].trim());
                            event.put("Тип", parts[2].trim());
                            event.put("Описание", parts[3].trim());
                            builder.addToList("События по времени", event);
                        }
                        break;

                    case "ENEMY_ACTION":
                        if (parts.length >= 3) {
                            Map<String, String> action = new HashMap<>();
                            action.put("Тип", parts[1].trim());
                            action.put("Описание", parts[2].trim());
                            builder.addToList("Действия противника", action);
                        }
                        break;

                    case "CIVILIAN_IMPACT":
                        if (parts.length >= 2) {
                            Map<String, String> impact = new HashMap<>();
                            for (int i = 1; i < parts.length; i++) {
                                String[] kv = parts[i].trim().split("=");
                                if (kv.length == 2) {
                                    String key;
                                    switch (kv[0]) {
                                        case "evacuated":
                                            key = "Эвакуировано";
                                            break;
                                        case "injured":
                                            key = "Пострадавшие";
                                            break;
                                        case "missing":
                                            key = "Пропавшие";
                                            break;
                                        default:
                                            key = kv[0];
                                            break;
                                    }
                                    impact.put(key, kv[1]);
                                }
                            }
                            builder.addExtension("Влияние на гражданских", impact);
                        }
                        break;
                }
            }
        }
    }
}