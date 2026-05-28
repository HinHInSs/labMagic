package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entity.MissionEntity;
import org.example.model.MissionOutcome;
import org.example.repository.MissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MissionServiceTest {

    @Mock
    private MissionRepository missionRepository;

    private MissionService missionService;

    private static final String VALID_JSON = """
            {
              "missionId": "SVC-001",
              "date": "2024-01-01",
              "location": "Токио",
              "outcome": "SUCCESS",
              "curse": {"name": "Тёмный дух", "threatLevel": "HIGH"},
              "sorcerers": [{"name": "Итадори", "rank": "GRADE_1"}],
              "techniques": [{"name": "Удар", "type": "BODY", "owner": "Итадори", "damage": 200}]
            }
            """;

    @BeforeEach
    void setUp() {
        ReportService reportService = new ReportService();
        missionService = new MissionService(missionRepository, reportService, new ObjectMapper());
    }

    @Test
    void upload_newMission_savedSuccessfully() throws Exception {
        when(missionRepository.existsByMissionId("SVC-001")).thenReturn(false);
        when(missionRepository.save(any())).thenAnswer(inv -> {
            MissionEntity e = inv.getArgument(0);
            e.setId(1L);
            return e;
        });

        MockMultipartFile file = new MockMultipartFile("file", "mission.json",
                "application/json", VALID_JSON.getBytes());

        MissionEntity result = missionService.upload(file);

        assertNotNull(result);
        assertEquals("SVC-001", result.getMissionId());
        verify(missionRepository).save(any(MissionEntity.class));
    }

    @Test
    void upload_duplicateMissionId_throwsIllegalStateException() {
        when(missionRepository.existsByMissionId("SVC-001")).thenReturn(true);

        MockMultipartFile file = new MockMultipartFile("file", "mission.json",
                "application/json", VALID_JSON.getBytes());

        assertThrows(IllegalStateException.class, () -> missionService.upload(file));
        verify(missionRepository, never()).save(any());
    }

    @Test
    void listAll_returnsAllMissions() {
        MissionEntity e1 = new MissionEntity();
        e1.setMissionId("M-001");
        MissionEntity e2 = new MissionEntity();
        e2.setMissionId("M-002");
        when(missionRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(e1, e2));

        List<MissionEntity> result = missionService.listAll();

        assertEquals(2, result.size());
        verify(missionRepository).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void getById_existingId_returnsMissionEntity() {
        MissionEntity entity = new MissionEntity();
        entity.setId(1L);
        entity.setMissionId("SVC-001");
        when(missionRepository.findById(1L)).thenReturn(Optional.of(entity));

        MissionEntity result = missionService.getById(1L);

        assertEquals("SVC-001", result.getMissionId());
    }

    @Test
    void getById_missingId_throwsIllegalArgumentException() {
        when(missionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> missionService.getById(99L));
    }

    @Test
    void delete_existingId_callsDeleteById() {
        when(missionRepository.existsById(1L)).thenReturn(true);

        missionService.delete(1L);

        verify(missionRepository).deleteById(1L);
    }

    @Test
    void delete_missingId_throwsIllegalArgumentException() {
        when(missionRepository.existsById(99L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> missionService.delete(99L));
        verify(missionRepository, never()).deleteById(any());
    }

    @Test
    void upload_missionEntityHasCurseFields() throws Exception {
        when(missionRepository.existsByMissionId(anyString())).thenReturn(false);
        when(missionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        MockMultipartFile file = new MockMultipartFile("file", "mission.json",
                "application/json", VALID_JSON.getBytes());

        MissionEntity result = missionService.upload(file);

        assertEquals("Тёмный дух", result.getCurseName());
        assertEquals("ВЫСОКИЙ", result.getCurseThreatLevel());
        assertEquals(MissionOutcome.SUCCESS, result.getOutcome());
    }
}
