package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class Mission {
    private String missionId;
    private String date;
    private String location;
    private MissionOutcome outcome;
    private int damageCost;
    private Curse curse;
    private List<Sorcerer> sorcerers;
    private List<Technique> techniques;
    private String comment;

    public Mission() {
        this.sorcerers = new ArrayList<>();
        this.techniques = new ArrayList<>();
    }

    public String getMissionId() {
        return missionId;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public MissionOutcome getOutcome() {
        return outcome;
    }

    public void setOutcome(MissionOutcome outcome) {
        this.outcome = outcome;
    }

    public int getDamageCost() {
        return damageCost;
    }

    public void setDamageCost(int damageCost) {
        this.damageCost = damageCost;
    }

    public Curse getCurse() {
        return curse;
    }

    public void setCurse(Curse curse) {
        this.curse = curse;
    }

    public List<Sorcerer> getSorcerers() {
        return sorcerers;
    }

    public void setSorcerers(List<Sorcerer> sorcerers) {
        this.sorcerers = sorcerers;
    }

    public List<Technique> getTechniques() {
        return techniques;
    }

    public void setTechniques(List<Technique> techniques) {
        this.techniques = techniques;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}