package org.example.model;
import java.util.*;

public class MissionBuilder {
    private String missionId;
    private String date;
    private String location;
    private int damageCost;
    private MissionOutcome outcome;
    private Curse curse;
    private List<Sorcerer> sorcerers = new ArrayList<>();
    private List<Technique> techniques = new ArrayList<>();
    private String comment;
    private Map<String, Object> extensions = new HashMap<>();

    public void createNewMission() {
        this.missionId = null;
        this.date = null;
        this.location = null;
        this.damageCost = 0;
        this.outcome = null;
        this.curse = new Curse();
        this.sorcerers.clear();
        this.techniques.clear();
        this.comment = null;
        this.extensions.clear();
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDamageCost(int damageCost) {
        this.damageCost = damageCost;
    }

    public void setOutcome(MissionOutcome outcome) {
        this.outcome = outcome;
    }

    public void setCurseName(String name) {
        this.curse.setName(name);
    }

    public void setCurseThreatLevel(String threatLevel) {
        this.curse.setThreatLevel(threatLevel);
    }

    public void setSorcerer(String name, String rank) {
        Sorcerer s = new Sorcerer();
        s.setName(name);
        s.setRank(rank);
        this.sorcerers.add(s);
    }

    public void setTechnique(String name, String type, String owner, int damage) {
        Technique t = new Technique();
        t.setName(name);
        t.setType(type);
        t.setDamage(damage);
        t.setOwner(owner);
        this.techniques.add(t);
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    private Sorcerer getLastSorcerer() {
        if (sorcerers.isEmpty()) {
            sorcerers.add(new Sorcerer());
        }
        return sorcerers.get(sorcerers.size() - 1);
    }

    public void setLastSorcererName(String name) {
        getLastSorcerer().setName(name);
    }

    public void setLastSorcererRank(String rank) {
        getLastSorcerer().setRank(rank);
    }

    private Technique getLastTechnique() {
        if (techniques.isEmpty()) {
            techniques.add(new Technique());
        }
        return techniques.get(techniques.size() - 1);
    }

    public void setLastTechniqueName(String name) {
        getLastTechnique().setName(name);
    }

    public void setLastTechniqueType(String type) {
        getLastTechnique().setType(type);
    }

    public void setLastTechniqueOwner(String owner) {
        getLastTechnique().setOwner(owner);
    }

    public void setLastTechniqueDamage(int damage) {
        getLastTechnique().setDamage(damage);
    }

    public void addExtension(String key, Object value) {
        this.extensions.put(key, value);
    }

    public void addToList(String key, Object value) {
        if (!this.extensions.containsKey(key)) {
            this.extensions.put(key, new ArrayList<>());
        }
        ((List<Object>) this.extensions.get(key)).add(value);
    }

    public Mission getMission() throws IllegalStateException {
        if (missionId == null || missionId.trim().isEmpty()) {
            throw new IllegalStateException("missionId обязателен");
        }
        if (date == null || date.trim().isEmpty()) {
            throw new IllegalStateException("date обязателен");
        }
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalStateException("location обязателен");
        }
        if (outcome == null) {
            throw new IllegalStateException("outcome обязателен");
        }

        if (curse == null) {
            throw new IllegalStateException("curse обязателен");
        }
        if (curse.getName() == null || curse.getName().trim().isEmpty()) {
            throw new IllegalStateException("curse.name обязателен");
        }
        if (curse.getThreatLevel() == null || curse.getThreatLevel().trim().isEmpty()) {
            throw new IllegalStateException("curse.threatLevel обязателен");
        }

        for (int i = 0; i < sorcerers.size(); i++) {
            Sorcerer s = sorcerers.get(i);
            if (s.getName() == null || s.getName().trim().isEmpty()) {
                throw new IllegalStateException("sorcerers[" + i + "].name обязателен");
            }
            if (s.getRank() == null || s.getRank().trim().isEmpty()) {
                throw new IllegalStateException("sorcerers[" + i + "].rank обязателен");
            }
        }

        for (int i = 0; i < techniques.size(); i++) {
            Technique t = techniques.get(i);
            if (t.getName() == null || t.getName().trim().isEmpty()) {
                throw new IllegalStateException("techniques[" + i + "].name обязателен");
            }
            if (t.getType() == null || t.getType().trim().isEmpty()) {
                throw new IllegalStateException("techniques[" + i + "].type обязателен");
            }
            if (t.getOwner() == null || t.getOwner().trim().isEmpty()) {
                throw new IllegalStateException("techniques[" + i + "].owner обязателен");
            }
        }

        Mission mission = new Mission();
        mission.setMissionId(missionId);
        mission.setDate(date);
        mission.setLocation(location);
        mission.setDamageCost(damageCost);
        mission.setOutcome(outcome);
        mission.setCurse(curse);
        mission.setSorcerers(sorcerers);
        mission.setTechniques(techniques);
        mission.setComment(comment);
        mission.setExtensions(new HashMap<>(extensions));

        return mission;
    }
}