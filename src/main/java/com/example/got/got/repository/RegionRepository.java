package com.example.got.got.repository;

import com.example.got.got.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Long> {
    Optional<Region> findByName(String name);

    @Query("SELECT r FROM Region r LEFT JOIN FETCH r.locations")
    List<Region> findAll();

}
