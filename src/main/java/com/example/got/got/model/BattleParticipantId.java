package com.example.got.got.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;


// The battle_participants table is a many-to-many join table with extra attributes, namely the role. 
// In such case, we need to create a composite key class (combination of battle_id and participant_id)
// This class will be used as the @EmbeddedId in the BattleParticipant entity.
@Embeddable
public class BattleParticipantId implements Serializable {

    private Long battleId;
    private Long participantId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BattleParticipantId)) return false;
        BattleParticipantId that = (BattleParticipantId) o;
        return Objects.equals(battleId, that.battleId) &&
               Objects.equals(participantId, that.participantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(battleId, participantId);
    }

    // Getters and setters
    public Long getBattleId() {
        return battleId;
    }

    public void setBattleId(Long battleId) {
        this.battleId = battleId;
    }

    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }
}
