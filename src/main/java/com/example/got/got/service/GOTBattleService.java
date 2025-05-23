package com.example.got.got.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.got.got.repository.BattleRepository;
import com.example.got.got.repository.LocationRepository;
import com.example.got.got.repository.ParticipantRepository;
import com.example.got.got.repository.RegionRepository;
import com.example.got.got.dto.RegionLocationDto;
import com.example.got.got.dto.BattleCountRecordsDto;
import com.example.got.got.dto.BattleDetailsDto;
import com.example.got.got.model.Battle;
import com.example.got.got.model.BattleParticipant;
import com.example.got.got.model.Location;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class GOTBattleService {

    @Autowired
    private BattleRepository battleRepo;
    @Autowired
    private RegionRepository regionRepo;
    @Autowired
    private LocationRepository locationRepo;
    @Autowired
    private ParticipantRepository participantRepo;

    private static final Logger logger = LogManager.getLogger(GOTBattleService.class);

    public BattleCountRecordsDto getBattleCount() {
        logger.info("Fetching battle count");
        try {
            BattleCountRecordsDto count = new BattleCountRecordsDto();
            count.setTotalBattles(battleRepo.count());
            count.setTotalLocations(locationRepo.count());
            count.setTotalLocations(locationRepo.count());
            count.setTotalRegions(regionRepo.count());
            count.setTotalParticipants(participantRepo.count());
            logger.info("Battle count fetched successfully");
            return count;
        } catch (Exception e) {
            logger.error("Error fetching battle count: {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<RegionLocationDto> listPlaces() {
        logger.info("Fetching list of places");
        try {
            List<RegionLocationDto> places = regionRepo.findAll()
                    .stream()
                    .map(region -> new RegionLocationDto(region.getName(),
                            region.getLocations().stream().map(Location::getName).collect(Collectors.toList())))
                    .collect(Collectors.toList());
            logger.info("List of places fetched successfully");
            return places;
        } catch (Exception e) {
            logger.error("Error fetching list of places: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Optional<BattleDetailsDto> getBattleByName(String name) {
        logger.info("Fetching battle details for name: {}", name);
        try {
            Optional<BattleDetailsDto> battleDetails = battleRepo.findByName(name).map(battle -> {
                BattleDetailsDto dto = new BattleDetailsDto();
                dto.setName(battle.getName());
                dto.setYear(battle.getYear());
                dto.setRegion(battle.getRegion() != null ? battle.getRegion().getName() : null);
                List<String> locations = battle.getLocations().size() > 0
                        ? battle.getLocations().stream()
                                .map(location -> location.getLocation().getName())
                                .collect(Collectors.toList())
                        : Collections.emptyList();
                dto.setLocations(locations);
                dto.setAttackerOutcome(battle.getAttackerOutcome());

                List<String> attackers = new ArrayList<>();
                List<String> defenders = new ArrayList<>();
                List<String> attackerCommanders = new ArrayList<>();
                List<String> defenderCommanders = new ArrayList<>();
                String attackerKing = null;
                String defenderKing = null;

                for (BattleParticipant bp : battle.getParticipants()) {
                    switch (bp.getRole()) {
                        case ATTACKER -> attackers.add(bp.getParticipant().getName());
                        case DEFENDER -> defenders.add(bp.getParticipant().getName());
                        case ATTACKER_KING -> attackerKing = bp.getParticipant().getName();
                        case DEFENDER_KING -> defenderKing = bp.getParticipant().getName();
                        case COMMANDER_ATTACKER -> attackerCommanders.add(bp.getParticipant().getName());
                        case COMMANDER_DEFENDER -> defenderCommanders.add(bp.getParticipant().getName());
                    }
                }

                dto.setAttackerKing(attackerKing);
                dto.setDefenderKing(defenderKing);
                dto.setAttackers(attackers);
                dto.setDefenders(defenders);
                dto.setAttackerCommanders(attackerCommanders);
                dto.setDefenderCommanders(defenderCommanders);
                dto.setBattleNumber(battle.getBattleNumber());
                dto.setBattleType(battle.getBattleType());
                dto.setAttackerSize(battle.getAttackerSize());
                dto.setDefenderSize(battle.getDefenderSize());
                dto.setMajorDeath(battle.getMajorDeath());
                dto.setMajorCapture(battle.getMajorCapture());
                dto.setNote(battle.getNote());
                dto.setSummer(battle.getSummer());

                return dto;
            });
            logger.info("Battle details fetched successfully for name: {}", name);
            return battleDetails;
        } catch (Exception e) {
            logger.error("Error fetching battle details for name {}: {}", name, e.getMessage(), e);
            throw e;
        }
    }
}