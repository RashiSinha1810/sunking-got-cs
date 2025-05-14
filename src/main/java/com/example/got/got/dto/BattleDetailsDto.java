package com.example.got.got.dto;

import java.util.List;

public class BattleDetailsDto {
    private Long battleNumber;
    private String name;
    private Integer year;
    private String region;
    private String location;
    private String attackerOutcome;
    private String battleType;
    private Integer majorDeath;
    private Integer majorCapture;
    private Integer attackerSize;
    private Integer defenderSize;
    private boolean summer;
    private String note;
    private List<String> attackers;
    private List<String> defenders;
    private String attackerKing;
    private String defenderKing;
    private List<String> attackerCommanders;
    private List<String> defenderCommanders;

    // Getters and Setters

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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAttackerOutcome() {
        return attackerOutcome;
    }

    public void setAttackerOutcome(String attackerOutcome) {
        this.attackerOutcome = attackerOutcome;
    }

    public List<String> getAttackers() {
        return attackers;
    }

    public void setAttackers(List<String> attackers) {
        this.attackers = attackers;
    }

    public List<String> getDefenders() {
        return defenders;
    }

    public void setDefenders(List<String> defenders) {
        this.defenders = defenders;
    }

    public String getAttackerKing() {
        return attackerKing;
    }

    public void setAttackerKing(String attackerKing) {
        this.attackerKing = attackerKing;
    }

    public String getDefenderKing() {
        return defenderKing;
    }

    public void setDefenderKing(String defenderKing) {
        this.defenderKing = defenderKing;
    }

    public List<String> getAttackerCommanders() {
        return attackerCommanders;
    }

    public void setAttackerCommanders(List<String> attackerCommanders) {
        this.attackerCommanders = attackerCommanders;
    }

    public List<String> getDefenderCommanders() {
        return defenderCommanders;
    }

    public void setDefenderCommanders(List<String> defenderCommanders) {
        this.defenderCommanders = defenderCommanders;
    }

    public Long getBattleNumber() {
        return battleNumber;
    }

    public void setBattleNumber(Long battleNumber) {
        this.battleNumber = battleNumber;
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

    public boolean getSummer() {
        return summer;
    }

    public void setSummer(boolean summer) {
        this.summer = summer;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getBattleType() {
        return battleType;
    }

    public void setBattleType(String battleType) {
        this.battleType = battleType;
    }
}