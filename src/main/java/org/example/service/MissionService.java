package org.example.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.datasource.MultipartDataSource;
import org.example.entity.MissionEntity;
import org.example.entity.SorcererEntity;
import org.example.entity.TechniqueEntity;
import org.example.model.Curse;
import org.example.model.Mission;
import org.example.model.Sorcerer;
import org.example.model.Technique;
import org.example.parser.Parser;
import org.example.parser.ParserFabric;
import org.example.repository.MissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MissionService {

    private final MissionRepository missionRepository;
    private final ReportService reportService;
    private final ObjectMapper objectMapper;

    @Autowired
    public MissionService(MissionRepository missionRepository,
                          ReportService reportService,
                          ObjectMapper objectMapper) {
        this.missionRepository = missionRepository;
        this.reportService = reportService;
        this.objectMapper = objectMapper;
    }

    public MissionEntity upload(MultipartFile file) throws Exception {
        MultipartDataSource source = new MultipartDataSource(file);
        Parser parser = ParserFabric.getParser(source);
        Mission mission = parser.parse(source.read());

        if (missionRepository.existsByMissionId(mission.getMissionId())) {
            throw new IllegalStateException("Миссия с ID '" + mission.getMissionId() + "' уже существует в архиве");
        }

        MissionEntity entity = toEntity(mission);
        return missionRepository.save(entity);
    }

    public List<MissionEntity> listAll() {
        return missionRepository.findAllByOrderByCreatedAtDesc();
    }

    public MissionEntity getById(Long id) {
        return missionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Миссия с id=" + id + " не найдена"));
    }

    public String generateReport(Long id) {
        MissionEntity entity = getById(id);
        Mission mission = toDomain(entity);
        return reportService.getReport(mission);
    }

    public void delete(Long id) {
        if (!missionRepository.existsById(id)) {
            throw new IllegalArgumentException("Миссия с id=" + id + " не найдена");
        }
        missionRepository.deleteById(id);
    }

    private MissionEntity toEntity(Mission mission) {
        MissionEntity entity = new MissionEntity();
        entity.setMissionId(mission.getMissionId());
        entity.setDate(mission.getDate());
        entity.setLocation(mission.getLocation());
        entity.setOutcome(mission.getOutcome());
        entity.setDamageCost(mission.getDamageCost());
        entity.setComment(mission.getComment());

        if (mission.getCurse() != null) {
            entity.setCurseName(mission.getCurse().getName());
            entity.setCurseThreatLevel(mission.getCurse().getThreatLevel());
        }

        List<SorcererEntity> sorcerers = new ArrayList<>();
        for (Sorcerer s : mission.getSorcerers()) {
            SorcererEntity se = new SorcererEntity();
            se.setName(s.getName());
            se.setRank(s.getRank());
            se.setMission(entity);
            sorcerers.add(se);
        }
        entity.setSorcerers(sorcerers);

        List<TechniqueEntity> techniques = new ArrayList<>();
        for (Technique t : mission.getTechniques()) {
            TechniqueEntity te = new TechniqueEntity();
            te.setName(t.getName());
            te.setType(t.getType());
            te.setOwner(t.getOwner());
            te.setDamage(t.getDamage());
            te.setMission(entity);
            techniques.add(te);
        }
        entity.setTechniques(techniques);

        if (mission.getExtensions() != null && !mission.getExtensions().isEmpty()) {
            try {
                entity.setExtensionsJson(objectMapper.writeValueAsString(mission.getExtensions()));
            } catch (Exception e) {
                entity.setExtensionsJson(null);
            }
        }

        return entity;
    }

    private Mission toDomain(MissionEntity entity) {
        Mission mission = new Mission();
        mission.setMissionId(entity.getMissionId());
        mission.setDate(entity.getDate());
        mission.setLocation(entity.getLocation());
        mission.setOutcome(entity.getOutcome());
        mission.setDamageCost(entity.getDamageCost());
        mission.setComment(entity.getComment());

        if (entity.getCurseName() != null) {
            Curse curse = new Curse();
            curse.setName(entity.getCurseName());
            curse.setThreatLevel(entity.getCurseThreatLevel());
            mission.setCurse(curse);
        }

        List<Sorcerer> sorcerers = new ArrayList<>();
        for (SorcererEntity se : entity.getSorcerers()) {
            Sorcerer s = new Sorcerer();
            s.setName(se.getName());
            s.setRank(se.getRank());
            sorcerers.add(s);
        }
        mission.setSorcerers(sorcerers);

        List<Technique> techniques = new ArrayList<>();
        for (TechniqueEntity te : entity.getTechniques()) {
            Technique t = new Technique();
            t.setName(te.getName());
            t.setType(te.getType());
            t.setOwner(te.getOwner());
            t.setDamage(te.getDamage());
            techniques.add(t);
        }
        mission.setTechniques(techniques);

        if (entity.getExtensionsJson() != null && !entity.getExtensionsJson().isBlank()) {
            try {
                Map<String, Object> ext = objectMapper.readValue(
                        entity.getExtensionsJson(),
                        new TypeReference<Map<String, Object>>() {}
                );
                mission.setExtensions(ext);
            } catch (Exception e) {
                // оставляем пустую map
            }
        }

        return mission;
    }
}
