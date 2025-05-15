package com.example.got.got.model;

import jakarta.persistence.*;
import java.util.List;

// All individuals (Kings, Commanders, others) stored uniquely.
/*
    Single person might be
        Attacker in one battle
        Defender in another
        Commander in another
 */
@Entity
@Table(name = "participants")
public class Participant {

    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long participantId;

    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL)
    private List<BattleParticipant> battles;

    public Participant(String name) {
        this.name = name;
    }
    
    public Participant() {
        // Default constructor
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParticipantId() {
        return participantId;
    }
    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }
    public List<BattleParticipant> getBattles() {
        return battles;
    }
    public void setBattles(List<BattleParticipant> battles) {
        this.battles = battles;
    }
}