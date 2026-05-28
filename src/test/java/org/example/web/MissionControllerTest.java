package org.example.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entity.MissionEntity;
import org.example.model.MissionOutcome;
import org.example.service.MissionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MissionController.class)
class MissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MissionService missionService;

    private MissionEntity buildEntity(Long id, String missionId) {
        MissionEntity e = new MissionEntity();
        e.setId(id);
        e.setMissionId(missionId);
        e.setDate("2024-01-01");
        e.setLocation("Токио");
        e.setOutcome(MissionOutcome.SUCCESS);
        e.setSorcerers(List.of());
        e.setTechniques(List.of());
        return e;
    }

    @Test
    void uploadMission_validFile_returns201() throws Exception {
        MissionEntity saved = buildEntity(1L, "TEST-001");
        when(missionService.upload(any())).thenReturn(saved);

        MockMultipartFile file = new MockMultipartFile(
                "file", "mission.json", "application/json",
                "{\"missionId\":\"TEST-001\"}".getBytes());

        mockMvc.perform(multipart("/api/missions/upload").file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.missionId").value("TEST-001"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void uploadMission_duplicate_returns409() throws Exception {
        when(missionService.upload(any()))
                .thenThrow(new IllegalStateException("Миссия с ID 'TEST-001' уже существует в архиве"));

        MockMultipartFile file = new MockMultipartFile(
                "file", "mission.json", "application/json",
                "{\"missionId\":\"TEST-001\"}".getBytes());

        mockMvc.perform(multipart("/api/missions/upload").file(file))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void uploadMission_invalidData_returns422() throws Exception {
        when(missionService.upload(any()))
                .thenThrow(new IllegalArgumentException("missionId обязателен"));

        MockMultipartFile file = new MockMultipartFile(
                "file", "bad.json", "application/json", "{}".getBytes());

        mockMvc.perform(multipart("/api/missions/upload").file(file))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void listAllMissions_returns200WithList() throws Exception {
        when(missionService.listAll()).thenReturn(List.of(
                buildEntity(1L, "M-001"),
                buildEntity(2L, "M-002")
        ));

        mockMvc.perform(get("/api/missions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].missionId").value("M-001"))
                .andExpect(jsonPath("$[1].missionId").value("M-002"));
    }

    @Test
    void getMissionById_exists_returns200() throws Exception {
        when(missionService.getById(1L)).thenReturn(buildEntity(1L, "M-001"));

        mockMvc.perform(get("/api/missions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.missionId").value("M-001"));
    }

    @Test
    void getMissionById_notFound_returns404() throws Exception {
        when(missionService.getById(99L))
                .thenThrow(new IllegalArgumentException("Миссия с id=99 не найдена"));

        mockMvc.perform(get("/api/missions/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void getMissionReport_exists_returns200() throws Exception {
        when(missionService.generateReport(1L)).thenReturn("Отчёт по миссии M-001");

        mockMvc.perform(get("/api/missions/1/report"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Отчёт")));
    }

    @Test
    void getMissionReport_notFound_returns404() throws Exception {
        when(missionService.generateReport(99L))
                .thenThrow(new IllegalArgumentException("Миссия с id=99 не найдена"));

        mockMvc.perform(get("/api/missions/99/report"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteMission_exists_returns204() throws Exception {
        doNothing().when(missionService).delete(1L);

        mockMvc.perform(delete("/api/missions/1"))
                .andExpect(status().isNoContent());

        verify(missionService).delete(1L);
    }

    @Test
    void deleteMission_notFound_returns404() throws Exception {
        doThrow(new IllegalArgumentException("Миссия с id=99 не найдена"))
                .when(missionService).delete(99L);

        mockMvc.perform(delete("/api/missions/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }
}
