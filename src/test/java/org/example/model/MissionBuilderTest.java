package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MissionBuilderTest {

    private MissionBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new MissionBuilder();
        builder.createNewMission();
    }

    private void fillValidMission() {
        builder.setMissionId("MISSION-001");
        builder.setDate("2024-01-01");
        builder.setLocation("Токио");
        builder.setOutcome(MissionOutcome.SUCCESS);
        builder.setCurseName("Проклятый дух");
        builder.setCurseThreatLevel("ВЫСОКИЙ");
        builder.setSorcerer("Итадори", "1 РАНГ");
        builder.setTechnique("Чёрная вспышка", "ВРОЖДЕННАЯ", "Итадори", 500);
    }

    @Test
    void buildValidMission_success() throws Exception {
        fillValidMission();
        Mission mission = builder.getMission();

        assertEquals("MISSION-001", mission.getMissionId());
        assertEquals("2024-01-01", mission.getDate());
        assertEquals("Токио", mission.getLocation());
        assertEquals(MissionOutcome.SUCCESS, mission.getOutcome());
        assertEquals("Проклятый дух", mission.getCurse().getName());
        assertEquals(1, mission.getSorcerers().size());
        assertEquals("Итадори", mission.getSorcerers().get(0).getName());
        assertEquals(1, mission.getTechniques().size());
        assertEquals("Чёрная вспышка", mission.getTechniques().get(0).getName());
    }

    @Test
    void buildMission_missingMissionId_throwsException() {
        builder.setDate("2024-01-01");
        builder.setLocation("Токио");
        builder.setOutcome(MissionOutcome.SUCCESS);
        builder.setCurseName("Дух");
        builder.setCurseThreatLevel("НИЗКИЙ");

        assertThrows(IllegalStateException.class, () -> builder.getMission());
    }

    @Test
    void buildMission_missingDate_throwsException() {
        builder.setMissionId("MISSION-002");
        builder.setLocation("Токио");
        builder.setOutcome(MissionOutcome.SUCCESS);
        builder.setCurseName("Дух");
        builder.setCurseThreatLevel("НИЗКИЙ");

        assertThrows(IllegalStateException.class, () -> builder.getMission());
    }

    @Test
    void buildMission_missingLocation_throwsException() {
        builder.setMissionId("MISSION-003");
        builder.setDate("2024-01-01");
        builder.setOutcome(MissionOutcome.SUCCESS);
        builder.setCurseName("Дух");
        builder.setCurseThreatLevel("НИЗКИЙ");

        assertThrows(IllegalStateException.class, () -> builder.getMission());
    }

    @Test
    void buildMission_missingOutcome_throwsException() {
        builder.setMissionId("MISSION-004");
        builder.setDate("2024-01-01");
        builder.setLocation("Токио");
        builder.setCurseName("Дух");
        builder.setCurseThreatLevel("НИЗКИЙ");

        assertThrows(IllegalStateException.class, () -> builder.getMission());
    }

    @Test
    void buildMission_missingCurseName_throwsException() {
        builder.setMissionId("MISSION-005");
        builder.setDate("2024-01-01");
        builder.setLocation("Токио");
        builder.setOutcome(MissionOutcome.SUCCESS);
        builder.setCurseThreatLevel("НИЗКИЙ");

        assertThrows(IllegalStateException.class, () -> builder.getMission());
    }

    @Test
    void buildMission_missingSorcererRank_throwsException() {
        builder.setMissionId("MISSION-006");
        builder.setDate("2024-01-01");
        builder.setLocation("Токио");
        builder.setOutcome(MissionOutcome.SUCCESS);
        builder.setCurseName("Дух");
        builder.setCurseThreatLevel("НИЗКИЙ");
        builder.setLastSorcererName("Итадори");

        assertThrows(IllegalStateException.class, () -> builder.getMission());
    }

    @Test
    void buildMission_withComment_commentSaved() throws Exception {
        fillValidMission();
        builder.setComment("Примечание к миссии");
        Mission mission = builder.getMission();

        assertEquals("Примечание к миссии", mission.getComment());
    }

    @Test
    void buildMission_damageCost_saved() throws Exception {
        fillValidMission();
        builder.setDamageCost(1000);
        Mission mission = builder.getMission();

        assertEquals(1000, mission.getDamageCost());
    }
}
