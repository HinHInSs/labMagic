package org.example.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.*;

import java.util.ArrayList;
import java.util.List;

public class JsonParser implements Parser {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Mission parse(String content) throws Exception {
        Mission mission = new Mission();
        JsonNode root = mapper.readTree(content);

        mission.setMissionId(root.get("missionId").asText());
        mission.setDate(root.get("date").asText());
        mission.setLocation(root.get("location").asText());
        mission.setDamageCost(root.get("damageCost").asInt());

        String outcome = root.get("outcome").asText();
        if ("SUCCESS".equals(outcome)) {
            mission.setOutcome(MissionOutcome.SUCCESS);
        } else {
            mission.setOutcome(MissionOutcome.FAILURE);
        }

        if (root.has("comment")) {
            mission.setComment(root.get("comment").asText());
        }

        JsonNode curseNode = root.get("curse");
        if (curseNode != null) {
            Curse curse = new Curse();
            curse.setName(curseNode.get("name").asText());
            curse.setThreatLevel(curseNode.get("threatLevel").asText());
            mission.setCurse(curse);
        }

        JsonNode sorcerersNode = root.get("sorcerers");
        if (sorcerersNode != null && sorcerersNode.isArray()) {
            List<Sorcerer> sorcerers = new ArrayList<>();
            for (JsonNode s : sorcerersNode) {
                Sorcerer sorcerer = new Sorcerer();
                sorcerer.setName(s.get("name").asText());
                sorcerer.setRank(s.get("rank").asText());
                sorcerers.add(sorcerer);
            }
            mission.setSorcerers(sorcerers);
        }

        JsonNode techniquesNode = root.get("techniques");
        if (techniquesNode != null && techniquesNode.isArray()) {
            List<Technique> techniques = new ArrayList<>();
            for (JsonNode t : techniquesNode) {
                Technique technique = new Technique();
                technique.setName(t.get("name").asText());
                technique.setType(t.get("type").asText());
                technique.setOwner(t.get("owner").asText());
                technique.setDamage(t.get("damage").asInt());
                techniques.add(technique);
            }
            mission.setTechniques(techniques);
        }

        return mission;
    }
}