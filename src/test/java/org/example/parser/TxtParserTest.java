package org.example.parser;

import org.example.model.Mission;
import org.example.model.MissionOutcome;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TxtParserTest {

    private final TxtParser parser = new TxtParser();

    private static final String VALID_TXT =
            "[MISSION]\n" +
            "missionId=TXT-001\n" +
            "date=2024-03-15\n" +
            "location=Сендай\n" +
            "outcome=SUCCESS\n" +
            "damageCost=300\n" +
            "note=Плановая зачистка\n" +
            "[CURSE]\n" +
            "name=Речной дух\n" +
            "threatLevel=LOW\n" +
            "[SORCERER]\n" +
            "name=Итадори Юдзи\n" +
            "rank=GRADE_1\n" +
            "[TECHNIQUE]\n" +
            "name=Чёрная вспышка\n" +
            "type=INNATE\n" +
            "owner=Итадори Юдзи\n" +
            "damage=450\n";

    @Test
    void parseTxt_validContent_returnsMission() throws Exception {
        Mission mission = parser.parse(VALID_TXT);

        assertEquals("TXT-001", mission.getMissionId());
        assertEquals("2024-03-15", mission.getDate());
        assertEquals("Сендай", mission.getLocation());
        assertEquals(MissionOutcome.SUCCESS, mission.getOutcome());
        assertEquals(300, mission.getDamageCost());
        assertEquals("Плановая зачистка", mission.getComment());
    }

    @Test
    void parseTxt_curse_parsedCorrectly() throws Exception {
        Mission mission = parser.parse(VALID_TXT);

        assertNotNull(mission.getCurse());
        assertEquals("Речной дух", mission.getCurse().getName());
        assertEquals("НИЗКИЙ", mission.getCurse().getThreatLevel());
    }

    @Test
    void parseTxt_sorcerer_parsedCorrectly() throws Exception {
        Mission mission = parser.parse(VALID_TXT);

        assertEquals(1, mission.getSorcerers().size());
        assertEquals("Итадори Юдзи", mission.getSorcerers().get(0).getName());
        assertEquals("1 РАНГ", mission.getSorcerers().get(0).getRank());
    }

    @Test
    void parseTxt_technique_parsedCorrectly() throws Exception {
        Mission mission = parser.parse(VALID_TXT);

        assertEquals(1, mission.getTechniques().size());
        assertEquals("Чёрная вспышка", mission.getTechniques().get(0).getName());
        assertEquals("ВРОЖДЕННАЯ", mission.getTechniques().get(0).getType());
        assertEquals(450, mission.getTechniques().get(0).getDamage());
    }

    @Test
    void parseTxt_partialSuccess_outcomeCorrect() throws Exception {
        String content = VALID_TXT.replace("outcome=SUCCESS", "outcome=PARTIAL_SUCCESS");
        Mission mission = parser.parse(content);

        assertEquals(MissionOutcome.PARTIAL_SUCCESS, mission.getOutcome());
    }

    @Test
    void parseTxt_missingMissionId_throwsException() {
        String content =
                "[MISSION]\n" +
                "date=2024-01-01\n" +
                "location=Токио\n" +
                "outcome=SUCCESS\n" +
                "[CURSE]\n" +
                "name=Дух\n" +
                "threatLevel=LOW\n";

        assertThrows(Exception.class, () -> parser.parse(content));
    }

    @Test
    void parseTxt_environmentBlock_savedAsExtension() throws Exception {
        String content = VALID_TXT +
                "[ENVIRONMENT]\n" +
                "weather=HEAVY_RAIN\n" +
                "timeOfDay=NIGHT\n";

        Mission mission = parser.parse(content);

        assertNotNull(mission.getExtensions());
        assertTrue(mission.getExtensions().containsKey("Условия среды"));
    }
}
