package com.example.got.got.repository;

import com.example.got.got.model.BattleParticipant;
import com.example.got.got.model.BattleParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BattleParticipantRepository extends JpaRepository<BattleParticipant, BattleParticipantId> {
}
