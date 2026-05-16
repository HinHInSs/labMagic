package org.example.repository;

import org.example.entity.MissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MissionRepository extends JpaRepository<MissionEntity, Long> {

    Optional<MissionEntity> findByMissionId(String missionId);

    boolean existsByMissionId(String missionId);

    List<MissionEntity> findAllByOrderByCreatedAtDesc();
}
