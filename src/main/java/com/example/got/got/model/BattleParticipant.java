package com.example.got.got.model;

import jakarta.persistence.*;

// Capture many-to-many relationship between battles and participants with roles
/*
    1. A battle has many participants
    2. A participant can appear in multiple battles
    3. Their role (attacker, defender, commander, etc.) is dynamic
*/ 
@Entity
@Table(name = "battle_participants")
public class BattleParticipant {

    @EmbeddedId
    private BattleParticipantId id = new BattleParticipantId();

    @ManyToOne
    @MapsId("battleId")
    @JoinColumn(name = "battle_id")
    private Battle battle;

    @ManyToOne
    @MapsId("participantId")
    @JoinColumn(name = "participant_id")
    private Participant participant;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {
        ATTACKER_KING,
        DEFENDER_KING,
        ATTACKER,
        DEFENDER,
        COMMANDER_ATTACKER,
        COMMANDER_DEFENDER
    }

    // Getters and setters
    public BattleParticipantId getId() {
        return id;
    }

    public void setId(BattleParticipantId id) {
        this.id = id;
    }

    public Battle getBattle() {
        return battle;
    }

    public void setBattle(Battle battle) {
        this.battle = battle;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}

