package org.example.parser;

import org.example.model.Mission;
import org.example.model.MissionOutcome;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonParserTest {

    private final JsonParser parser = new JsonParser();

    private static final String VALID_JSON = """
            {
              "missionId": "JSON-001",
              "date": "2024-05-10",
              "location": "Осака",
              "outcome": "SUCCESS",
              "damageCost": 500,
              "comment": "Успешная операция",
              "curse": {
                "name": "Проклятый дух особого ранга",
                "threatLevel": "SPECIAL_GRADE"
              },
              "sorcerers": [
                {"name": "Годзё Сатору", "rank": "GRADE_1"}
              ],
              "techniques": [
                {"name": "Бесконечность", "type": "INNATE", "owner": "Годзё Сатору", "damage": 9999}
              ]
            }
            """;

    @Test
    void parseJson_validContent_returnsMission() throws Exception {
        Mission mission = parser.parse(VALID_JSON);

        assertEquals("JSON-001", mission.getMissionId());
        assertEquals("2024-05-10", mission.getDate());
        assertEquals("Осака", mission.getLocation());
        assertEquals(MissionOutcome.SUCCESS, mission.getOutcome());
        assertEquals(500, mission.getDamageCost());
        assertEquals("Успешная операция", mission.getComment());
    }

    @Test
    void parseJson_curse_parsedCorrectly() throws Exception {
        Mission mission = parser.parse(VALID_JSON);

        assertNotNull(mission.getCurse());
        assertEquals("Проклятый дух особого ранга", mission.getCurse().getName());
        assertEquals("ОСОБЫЙ РАНГ", mission.getCurse().getThreatLevel());
    }

    @Test
    void parseJson_sorcerersAndTechniques_parsedCorrectly() throws Exception {
        Mission mission = parser.parse(VALID_JSON);

        assertEquals(1, mission.getSorcerers().size());
        assertEquals("Годзё Сатору", mission.getSorcerers().get(0).getName());
        assertEquals("1 РАНГ", mission.getSorcerers().get(0).getRank());

        assertEquals(1, mission.getTechniques().size());
        assertEquals("Бесконечность", mission.getTechniques().get(0).getName());
        assertEquals("ВРОЖДЕННАЯ", mission.getTechniques().get(0).getType());
        assertEquals(9999, mission.getTechniques().get(0).getDamage());
    }

    @Test
    void parseJson_failure_outcomeCorrect() throws Exception {
        String content = VALID_JSON.replace("\"SUCCESS\"", "\"FAILURE\"");
        Mission mission = parser.parse(content);

        assertEquals(MissionOutcome.FAILURE, mission.getOutcome());
    }

    @Test
    void parseJson_highThreatLevel_translatedToRussian() throws Exception {
        String content = VALID_JSON.replace("SPECIAL_GRADE", "HIGH");
        Mission mission = parser.parse(content);

        assertEquals("ВЫСОКИЙ", mission.getCurse().getThreatLevel());
    }

    @Test
    void parseJson_multipleSorcerers_allParsed() throws Exception {
        String content = """
                {
                  "missionId": "JSON-002",
                  "date": "2024-06-01",
                  "location": "Киото",
                  "outcome": "PARTIAL_SUCCESS",
                  "curse": {"name": "Дух", "threatLevel": "MEDIUM"},
                  "sorcerers": [
                    {"name": "Итадори", "rank": "GRADE_1"},
                    {"name": "Мегуми", "rank": "GRADE_2"},
                    {"name": "Нобара", "rank": "GRADE_2"}
                  ],
                  "techniques": []
                }
                """;
        Mission mission = parser.parse(content);

        assertEquals(3, mission.getSorcerers().size());
        assertEquals(MissionOutcome.PARTIAL_SUCCESS, mission.getOutcome());
    }

    @Test
    void parseJson_invalidJson_throwsException() {
        String broken = "{ not valid json }";
        assertThrows(Exception.class, () -> parser.parse(broken));
    }
}
