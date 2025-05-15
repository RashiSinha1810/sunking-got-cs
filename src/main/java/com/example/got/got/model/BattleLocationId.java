package com.example.got.got.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

// The battle_participants table is a many-to-many join table with extra attributes, namely the role. 
// In such case, we need to create a composite key class (combination of battle_id and participant_id)
// This class will be used as the @EmbeddedId in the BattleParticipant entity.
@Embeddable
public class BattleLocationId implements Serializable {

    private Long battleId;
    private Long locationId;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof BattleLocationId))
            return false;
        BattleLocationId that = (BattleLocationId) o;
        return Objects.equals(battleId, that.battleId) &&
                Objects.equals(locationId, that.locationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(battleId, locationId);
    }

    // Getters and setters
    public Long getBattleId() {
        return battleId;
    }

    public void setBattleId(Long battleId) {
        this.battleId = battleId;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }
}
