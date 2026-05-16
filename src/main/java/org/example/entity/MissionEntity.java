package org.example.entity;

import jakarta.persistence.*;
import org.example.model.MissionOutcome;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "missions")
public class MissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mission_id", nullable = false, unique = true)
    private String missionId;

    @Column(name = "date")
    private String date;

    @Column(name = "location")
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "outcome", nullable = false)
    private MissionOutcome outcome;

    @Column(name = "damage_cost")
    private int damageCost;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "curse_name")
    private String curseName;

    @Column(name = "curse_threat_level")
    private String curseThreatLevel;

    @Column(name = "extensions_json", columnDefinition = "TEXT")
    private String extensionsJson;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SorcererEntity> sorcerers = new ArrayList<>();

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TechniqueEntity> techniques = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMissionId() { return missionId; }
    public void setMissionId(String missionId) { this.missionId = missionId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public MissionOutcome getOutcome() { return outcome; }
    public void setOutcome(MissionOutcome outcome) { this.outcome = outcome; }

    public int getDamageCost() { return damageCost; }
    public void setDamageCost(int damageCost) { this.damageCost = damageCost; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getCurseName() { return curseName; }
    public void setCurseName(String curseName) { this.curseName = curseName; }

    public String getCurseThreatLevel() { return curseThreatLevel; }
    public void setCurseThreatLevel(String curseThreatLevel) { this.curseThreatLevel = curseThreatLevel; }

    public String getExtensionsJson() { return extensionsJson; }
    public void setExtensionsJson(String extensionsJson) { this.extensionsJson = extensionsJson; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public List<SorcererEntity> getSorcerers() { return sorcerers; }
    public void setSorcerers(List<SorcererEntity> sorcerers) { this.sorcerers = sorcerers; }

    public List<TechniqueEntity> getTechniques() { return techniques; }
    public void setTechniques(List<TechniqueEntity> techniques) { this.techniques = techniques; }
}
