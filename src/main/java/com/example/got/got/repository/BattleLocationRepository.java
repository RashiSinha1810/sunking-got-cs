package com.example.got.got.repository;

import com.example.got.got.model.BattleLocation;
import com.example.got.got.model.BattleLocationId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BattleLocationRepository extends JpaRepository<BattleLocation, BattleLocationId> {
}
