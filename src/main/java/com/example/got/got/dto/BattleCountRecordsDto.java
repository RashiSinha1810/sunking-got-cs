package com.example.got.got.dto;

public class BattleCountRecordsDto {
    private Number totalBattles;
    private Number totalParticipants;
    private Number totalRegions;
    private Number totalLocations;

    public Number getTotalBattles() {
        return totalBattles;
    }

    public void setTotalBattles(Number totalBattles) {
        this.totalBattles = totalBattles;
    }

    public Number getTotalParticipants() {
        return totalParticipants;
    }

    public void setTotalParticipants(Number totalParticipants) {
        this.totalParticipants = totalParticipants;
    }

    public Number getTotalRegions() {
        return totalRegions;
    }

    public void setTotalRegions(Number totalRegions) {
        this.totalRegions = totalRegions;
    }

    public Number getTotalLocations() {
        return totalLocations;
    }

    public void setTotalLocations(Number totalLocations) {
        this.totalLocations = totalLocations;
    }
}
