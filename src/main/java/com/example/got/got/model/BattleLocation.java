package com.example.got.got.model;

import jakarta.persistence.*;

// Capture many-to-many relationship between battles and locations with roles
/*
    1. A battle has many locations
    2. A location can appear in multiple battles
*/
@Entity
@Table(name = "battle_locations", indexes = {
        @Index(name = "idx_bl_battle_id", columnList = "battle_id"),
        @Index(name = "idx_bl_location_id", columnList = "location_id")
})
public class BattleLocation {

    @EmbeddedId
    private BattleLocationId id = new BattleLocationId();

    @ManyToOne
    @MapsId("battleId")
    @JoinColumn(name = "battle_id")
    private Battle battle;

    @ManyToOne
    @MapsId("locationId")
    @JoinColumn(name = "location_id")
    private Location location;

    // Getters and Setters
    public BattleLocationId getId() {
        return id;
    }

    public void setId(BattleLocationId id) {
        this.id = id;
    }

    public Battle getBattle() {
        return battle;
    }

    public void setBattle(Battle battle) {
        this.battle = battle;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

}
