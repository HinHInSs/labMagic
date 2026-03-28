package org.example.parser;

import org.example.model.*;

import java.io.BufferedReader;
import java.io.StringReader;

public class TxtParser implements Parser {

    @Override
    public Mission parse(String content) throws Exception {
        Mission mission = new Mission();

        try (BufferedReader reader = new BufferedReader(new StringReader(content))) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                int colonIndex = line.indexOf(':');
                if (colonIndex == -1) continue;

                String key = line.substring(0, colonIndex).trim();
                String value = line.substring(colonIndex + 1).trim();

                switch (key) {
                    case "missionId":
                        mission.setMissionId(value);
                        break;
                    case "date":
                        mission.setDate(value);
                        break;
                    case "location":
                        mission.setLocation(value);
                        break;
                    case "damageCost":
                        mission.setDamageCost(Integer.parseInt(value));
                        break;
                    case "outcome":
                        if ("SUCCESS".equals(value)) {
                            mission.setOutcome(MissionOutcome.SUCCESS);
                        } else {
                            mission.setOutcome(MissionOutcome.FAILURE);
                        }
                        break;
                    case "curse.name":
                        if (mission.getCurse() == null) {
                            mission.setCurse(new Curse());
                        }
                        mission.getCurse().setName(value);
                        break;
                    case "curse.threatLevel":
                        if (mission.getCurse() == null) {
                            mission.setCurse(new Curse());
                        }
                        mission.getCurse().setThreatLevel(value);
                        break;
                    case "note":
                        mission.setComment(value);
                        break;
                }

                if (key.startsWith("sorcerer") && key.endsWith("name")) {
                    Sorcerer sorcerer = new Sorcerer();
                    sorcerer.setName(value);
                    mission.getSorcerers().add(sorcerer);
                } else if (key.startsWith("sorcerer") && key.endsWith("rank")) {
                    if (!mission.getSorcerers().isEmpty()) {
                        mission.getSorcerers().get(mission.getSorcerers().size() - 1).setRank(value);
                    }
                } else if (key.startsWith("technique") && key.endsWith("name")) {
                    Technique technique = new Technique();
                    technique.setName(value);
                    mission.getTechniques().add(technique);
                } else if (key.startsWith("technique") && key.endsWith("type")) {
                    if (!mission.getTechniques().isEmpty()) {
                        mission.getTechniques().get(mission.getTechniques().size() - 1).setType(value);
                    }
                } else if (key.startsWith("technique") && key.endsWith("owner")) {
                    if (!mission.getTechniques().isEmpty()) {
                        mission.getTechniques().get(mission.getTechniques().size() - 1).setOwner(value);
                    }
                } else if (key.startsWith("technique") && key.endsWith("damage")) {
                    if (!mission.getTechniques().isEmpty()) {
                        mission.getTechniques().get(mission.getTechniques().size() - 1).setDamage(Integer.parseInt(value));
                    }
                }
            }
        }

        return mission;
    }
}