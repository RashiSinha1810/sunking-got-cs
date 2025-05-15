package com.example.got.got.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.Set;

// Main Unit of battle information
@Entity
@Table(name = "battles", indexes = {
        @Index(name = "idx_battle_number", columnList = "battleNumber"),
        @Index(name = "idx_battle_name", columnList = "name"),
})
public class Battle {

    @Id
    private Long battleNumber;

    private String name;
    private Integer year;

    private String attackerOutcome;
    private String battleType;

    private Integer majorDeath;
    private Integer majorCapture;

    private Integer attackerSize;
    private Integer defenderSize;

    private Boolean summer;

    // Can be binary or character
    // @Lob
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @OneToMany(mappedBy = "battle", cascade = CascadeType.ALL)
    private Set<BattleParticipant> participants;

    @OneToMany(mappedBy = "battle", cascade = CascadeType.ALL)
    private Set<BattleLocation> locations;

    @ManyToOne
    @JoinColumn(name = "region_id")
    private Region region;

    // Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Long getBattleNumber() {
        return battleNumber;
    }

    public void setBattleNumber(Long battleNumber) {
        this.battleNumber = battleNumber;
    }

    public String getAttackerOutcome() {
        return attackerOutcome;
    }

    public void setAttackerOutcome(String attackerOutcome) {
        this.attackerOutcome = attackerOutcome;
    }

    public String getBattleType() {
        return battleType;
    }

    public void setBattleType(String battleType) {
        this.battleType = battleType;
    }

    public Integer getMajorDeath() {
        return majorDeath;
    }

    public void setMajorDeath(Integer majorDeath) {
        this.majorDeath = majorDeath;
    }

    public Integer getMajorCapture() {
        return majorCapture;
    }

    public void setMajorCapture(Integer majorCapture) {
        this.majorCapture = majorCapture;
    }

    public Integer getAttackerSize() {
        return attackerSize;
    }

    public void setAttackerSize(Integer attackerSize) {
        this.attackerSize = attackerSize;
    }

    public Integer getDefenderSize() {
        return defenderSize;
    }

    public void setDefenderSize(Integer defenderSize) {
        this.defenderSize = defenderSize;
    }

    public Boolean getSummer() {
        return summer;
    }

    public void setSummer(Boolean summer) {
        this.summer = summer;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Set<BattleParticipant> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<BattleParticipant> participants) {
        this.participants = participants;
    }

    public Set<BattleLocation> getLocations() {
        return locations;
    }

    public void setLocations(Set<BattleLocation> locations) {
        this.locations = locations;
    }
}
