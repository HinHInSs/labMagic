package org.example.parser;

import org.example.model.*;

public class TxtParser implements Parser {

    @Override
    public Mission parse(String content) {
        Mission mission = new Mission();
        String[] lines = content.split("\n");

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            int colonIndex = line.indexOf(':');
            if (colonIndex == -1) continue;

            String key = line.substring(0, colonIndex).trim();
            String value = line.substring(colonIndex + 1).trim();

            if (key.equals("missionId")) {
                mission.setMissionId(value);
            } else if (key.equals("date")) {
                mission.setDate(value);
            } else if (key.equals("location")) {
                mission.setLocation(value);
            } else if (key.equals("damageCost")) {
                mission.setDamageCost(Integer.parseInt(value));
            } else if (key.equals("outcome")) {
                if (value.equals("SUCCESS")) {
                    mission.setOutcome(MissionOutcome.SUCCESS);
                } else {
                    mission.setOutcome(MissionOutcome.FAILURE);
                }
            } else if (key.equals("curse.name")) {
                if (mission.getCurse() == null) mission.setCurse(new Curse());
                mission.getCurse().setName(value);
            } else if (key.equals("curse.threatLevel")) {
                if (mission.getCurse() == null) mission.setCurse(new Curse());
                mission.getCurse().setThreatLevel(value);
            } else if (key.equals("note")) {
                mission.setComment(value);
            } else if (key.startsWith("sorcerer") && key.endsWith("name")) {
                Sorcerer s = new Sorcerer();
                s.setName(value);
                mission.getSorcerers().add(s);
            } else if (key.startsWith("sorcerer") && key.endsWith("rank")) {
                if (!mission.getSorcerers().isEmpty()) {
                    mission.getSorcerers().get(mission.getSorcerers().size() - 1).setRank(value);
                }
            } else if (key.startsWith("technique") && key.endsWith("name")) {
                Technique t = new Technique();
                t.setName(value);
                mission.getTechniques().add(t);
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

        return mission;
    }
}