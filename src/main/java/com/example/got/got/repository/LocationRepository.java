package com.example.got.got.repository;

import com.example.got.got.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByName(String name);
    Optional<Location> findByNameAndRegionName(String name, String regionName);
    Optional<Location> findByNameAndRegionRegionId(String name, Long regionId);
}
