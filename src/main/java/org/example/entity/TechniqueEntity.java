package org.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "techniques")
public class TechniqueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_db_id", nullable = false)
    private MissionEntity mission;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "owner")
    private String owner;

    @Column(name = "damage")
    private int damage;

    public Long getId() { return id; }

    public MissionEntity getMission() { return mission; }
    public void setMission(MissionEntity mission) { this.mission = mission; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }

    public int getDamage() { return damage; }
    public void setDamage(int damage) { this.damage = damage; }
}
