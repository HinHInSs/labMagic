package org.example.parser;

import org.example.model.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class JsonParser implements Parser {

    @Override
    public Mission parse(String content) {
        Mission mission = new Mission();
        JSONObject json = new JSONObject(content);

        mission.setMissionId(json.getString("missionId"));
        mission.setDate(json.getString("date"));
        mission.setLocation(json.getString("location"));
        mission.setDamageCost(json.getInt("damageCost"));

        String outcome = json.getString("outcome");
        if (outcome.equals("SUCCESS")) {
            mission.setOutcome(MissionOutcome.SUCCESS);
        } else {
            mission.setOutcome(MissionOutcome.FAILURE);
        }

        if (json.has("comment")) {
            mission.setComment(json.getString("comment"));
        }

        JSONObject curseJson = json.getJSONObject("curse");
        Curse curse = new Curse();
        curse.setName(curseJson.getString("name"));
        curse.setThreatLevel(curseJson.getString("threatLevel"));
        mission.setCurse(curse);

        JSONArray sorcerersJson = json.getJSONArray("sorcerers");
        for (int i = 0; i < sorcerersJson.length(); i++) {
            JSONObject s = sorcerersJson.getJSONObject(i);
            Sorcerer sorcerer = new Sorcerer();
            sorcerer.setName(s.getString("name"));
            sorcerer.setRank(s.getString("rank"));
            mission.getSorcerers().add(sorcerer);
        }

        JSONArray techniquesJson = json.getJSONArray("techniques");
        for (int i = 0; i < techniquesJson.length(); i++) {
            JSONObject t = techniquesJson.getJSONObject(i);
            Technique technique = new Technique();
            technique.setName(t.getString("name"));
            technique.setType(t.getString("type"));
            technique.setOwner(t.getString("owner"));
            technique.setDamage(t.getInt("damage"));
            mission.getTechniques().add(technique);
        }

        return mission;
    }
}