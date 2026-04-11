package org.example.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.MissionBuilder;
import org.example.model.MissionOutcome;

public class JsonParser extends BaseParser {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void parseContent(String content, MissionBuilder builder) throws Exception {
        JsonNode root = mapper.readTree(content);

        builder.setMissionId(root.get("missionId").asText());
        builder.setDate(root.get("date").asText());
        builder.setLocation(root.get("location").asText());

        if (root.has("damageCost")) {
            builder.setDamageCost(root.get("damageCost").asInt());
        }

        String outcome = root.get("outcome").asText();
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

        JsonNode curseNode = root.get("curse");
        if (curseNode != null) {
            if (curseNode.has("name")) {
                builder.setCurseName(curseNode.get("name").asText());
            }
            if (curseNode.has("threatLevel")) {
                String threatLevel = curseNode.get("threatLevel").asText();
                switch (threatLevel) {
                    case "HIGH": threatLevel = "ВЫСОКИЙ"; break;
                    case "MEDIUM": threatLevel = "СРЕДНИЙ"; break;
                    case "LOW": threatLevel = "НИЗКИЙ"; break;
                    case "SPECIAL_GRADE": threatLevel = "ОСОБЫЙ РАНГ"; break;
                }
                builder.setCurseThreatLevel(threatLevel);
            }
        }

        if (root.has("comment")) {
            builder.setComment(root.get("comment").asText());
        }

        JsonNode sorcerersNode = root.get("sorcerers");
        if (sorcerersNode != null && sorcerersNode.isArray()) {
            for (JsonNode s : sorcerersNode) {
                String name = s.has("name") ? s.get("name").asText() : null;
                String rank = s.has("rank") ? s.get("rank").asText() : null;
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

        JsonNode techniquesNode = root.get("techniques");
        if (techniquesNode != null && techniquesNode.isArray()) {
            for (JsonNode t : techniquesNode) {
                String name = t.has("name") ? t.get("name").asText() : null;
                String type = t.has("type") ? t.get("type").asText() : null;
                String owner = t.has("owner") ? t.get("owner").asText() : null;
                int damage = t.has("damage") ? t.get("damage").asInt() : 0;
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
    }
}