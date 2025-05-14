package com.example.got.got.repository;

import com.example.got.got.model.Battle;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BattleRepository extends JpaRepository<Battle, Long> {
    Optional<Battle> findByName(String name);
}
