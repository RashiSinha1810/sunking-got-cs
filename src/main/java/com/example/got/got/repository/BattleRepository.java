package com.example.got.got.repository;

import com.example.got.got.model.Battle;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

public interface BattleRepository extends JpaRepository<Battle, Long> {
    @Query("SELECT b FROM Battle b LEFT JOIN FETCH b.participants LEFT JOIN FETCH b.locations WHERE b.name = :name")
    Optional<Battle> findByName(@Param("name") String name);
}
