package com.example.got.got.model;
import jakarta.persistence.*;
import java.util.List;

// Main Unit of battle information
@Entity
@Table(name = "battles")
public class Battle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long battleId;

    private String name;
    private Integer year;
    private Integer battleNumber;

    private String attackerOutcome;
    private String battleType;

    private Integer majorDeath;
    private Integer majorCapture;

    private Integer attackerSize;
    private Integer defenderSize;

    private Boolean summer;

    // Can be binary or character
    @Lob
    private String note;

    @OneToMany(mappedBy = "battle", cascade = CascadeType.ALL)
    private List<BattleParticipant> participants;

    @ManyToOne
    @JoinColumn(name = "region_id")
    private Region region;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    // Getters and setters
    public Long getBattleId() {
        return battleId;
    }

    public void setBattleId(Long battleId) {
        this.battleId = battleId;
    }

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

    public Integer getBattleNumber() {
        return battleNumber;
    }

    public void setBattleNumber(Integer battleNumber) {
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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

    public List<BattleParticipant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<BattleParticipant> participants) {
        this.participants = participants;
    }
}
