package org.example.parser;

import org.example.model.MissionBuilder;
import org.example.model.MissionOutcome;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class TxtParser extends BaseParser {

    @Override
    protected void parseContent(String content, MissionBuilder builder) throws Exception {
        try (BufferedReader reader = new BufferedReader(new StringReader(content))) {
            String line;
            String currentSection = "";
            Map<String, String> temp = new HashMap<>();
            Map<String, String> environmentBlock = new HashMap<>();

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.startsWith("[") && line.endsWith("]")) {
                    currentSection = line.substring(1, line.length() - 1);
                    continue;
                }

                int eq = line.indexOf('=');
                if (eq == -1) continue;

                String key = line.substring(0, eq).trim();
                String value = line.substring(eq + 1).trim();

                if (currentSection.equals("MISSION")) {
                    switch (key) {
                        case "missionId":
                            builder.setMissionId(value);
                            break;
                        case "date":
                            builder.setDate(value);
                            break;
                        case "location":
                            builder.setLocation(value);
                            break;
                        case "damageCost":
                            builder.setDamageCost(Integer.parseInt(value));
                            break;
                        case "outcome":
                            switch (value) {
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
                            break;
                        case "note":
                            builder.setComment(value);
                            break;
                    }
                } else if (currentSection.equals("CURSE")) {
                    switch (key) {
                        case "name":
                            builder.setCurseName(value);
                            break;
                        case "threatLevel":
                            builder.setCurseThreatLevel(value);
                            break;
                    }
                } else if (currentSection.equals("SORCERER")) {
                    if (key.equals("name")) {
                        temp.put("name", value);
                    } else if (key.equals("rank") && temp.containsKey("name")) {
                        builder.setSorcerer(temp.get("name"), value);
                        temp.remove("name");
                    }
                } else if (currentSection.equals("TECHNIQUE")) {
                    switch (key) {
                        case "name":
                            temp.put("tech_name", value);
                            break;
                        case "type":
                            temp.put("tech_type", value);
                            break;
                        case "owner":
                            temp.put("tech_owner", value);
                            break;
                        case "damage":
                            if (temp.containsKey("tech_name") && temp.containsKey("tech_type") && temp.containsKey("tech_owner")) {
                                builder.setTechnique(temp.get("tech_name"), temp.get("tech_type"), temp.get("tech_owner"), Integer.parseInt(value));
                                temp.remove("tech_name");
                                temp.remove("tech_type");
                                temp.remove("tech_owner");
                            }
                            break;
                    }
                } else if (currentSection.equals("ENVIRONMENT")) {
                    String rusKey;
                    switch (key) {
                        case "weather":
                            rusKey = "Погода";
                            break;
                        case "timeOfDay":
                            rusKey = "Время суток";
                            break;
                        case "visibility":
                            rusKey = "Видимость";
                            break;
                        case "cursedEnergyDensity":
                            rusKey = "Плотность проклятой энергии";
                            break;
                        default:
                            rusKey = key;
                            break;
                    }
                    environmentBlock.put(rusKey, value);
                }
            }

            if (!environmentBlock.isEmpty()) {
                builder.addExtension("Условия среды", environmentBlock);
            }
        }
    }
}