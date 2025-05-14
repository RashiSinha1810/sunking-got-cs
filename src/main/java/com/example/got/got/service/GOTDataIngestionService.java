package com.example.got.got.service;

import com.example.got.got.model.*;
import com.example.got.got.repository.*;
import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.util.*;

@Service
public class GOTDataIngestionService {

    @Autowired private BattleRepository battleRepo;
    @Autowired private ParticipantRepository participantRepo;
    @Autowired private BattleParticipantRepository bpRepo;
    @Autowired private RegionRepository regionRepo;
    @Autowired private LocationRepository locationRepo;

    private String get(String[] row, Map<String, Integer> headerMap, String columnName) {
        Integer index = headerMap.get(columnName.toLowerCase());
        if (index != null && index < row.length) {
            return row[index] != null ? row[index].trim() : null;
        }
        return null;
    }


    public void ingestCsv(MultipartFile file) throws Exception {
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] header = reader.readNext(); // skip header
            Map<String, Integer> headerIndexMap = new HashMap<>();
            for (int i = 0; i < header.length; i++) {
               headerIndexMap.put(header[i].trim().toLowerCase(), i);
            }

            String[] row;
            while ((row = reader.readNext()) != null) {
                // Battle fields
                String name = get(row, headerIndexMap, "name");
                String year = get(row, headerIndexMap, "year");
                String battleNumber = get(row, headerIndexMap, "battle_number");
                String attackerOutcome = get(row, headerIndexMap, "attacker_outcome");
                String battleType = get(row, headerIndexMap, "battle_type");
                String majorDeath = get(row, headerIndexMap, "major_death");
                String majorCapture = get(row, headerIndexMap, "major_capture");
                String attackerSize = get(row, headerIndexMap, "attacker_size");
                String defenderSize = get(row, headerIndexMap, "defender_size");
                String summer = get(row, headerIndexMap, "summer");
                String location = get(row, headerIndexMap, "location");
                String region = get(row, headerIndexMap, "region");
                String note = get(row, headerIndexMap, "note");
                
                // Pariticipants
                // Note: King is single for each side 
                String attackerKing = get(row, headerIndexMap, "attacker_king");
                String defenderKing = get(row, headerIndexMap, "defender_king");

                // Commander is multiple for each side. Need to split on comma
                String attackerCommander = get(row, headerIndexMap, "attacker_commander");
                String defenderCommander = get(row, headerIndexMap, "defender_commander");

                List<String> attackerCommanders = attackerCommander != null
                        ? Arrays.asList(attackerCommander.split(","))
                        : Collections.emptyList();

                List<String> defenderCommanders = defenderCommander != null
                        ? Arrays.asList(defenderCommander.split(","))
                        : Collections.emptyList();

                // Trim whitespace from each commander name
                attackerCommanders.replaceAll(String::trim);
                defenderCommanders.replaceAll(String::trim);
                
                // Attacker and Defender are single for each side. Need to join the attacker_1, attacker_2, etc. fields into List.
                List<String> attackers = new ArrayList<>();
                List<String> defenders = new ArrayList<>();

                String attacker_1 = get(row, headerIndexMap, "attacker_1");
                String attacker_2 = get(row, headerIndexMap, "attacker_2");
                String attacker_3 = get(row, headerIndexMap, "attacker_3");
                String attacker_4 = get(row, headerIndexMap, "attacker_4");
                String defender_1 = get(row, headerIndexMap, "defender_1");
                String defender_2 = get(row, headerIndexMap, "defender_2");
                String defender_3 = get(row, headerIndexMap, "defender_3");
                String defender_4 = get(row, headerIndexMap, "defender_4");

                if (attacker_1 != null && !attacker_1.trim().isEmpty()) attackers.add(attacker_1.trim());
                if (attacker_2 != null && !attacker_2.trim().isEmpty()) attackers.add(attacker_2.trim());
                if (attacker_3 != null && !attacker_3.trim().isEmpty()) attackers.add(attacker_3.trim());
                if (attacker_4 != null && !attacker_4.trim().isEmpty()) attackers.add(attacker_4.trim());

                if (defender_1 != null && !defender_1.trim().isEmpty()) defenders.add(defender_1.trim());
                if (defender_2 != null && !defender_2.trim().isEmpty()) defenders.add(defender_2.trim());
                if (defender_3 != null && !defender_3.trim().isEmpty()) defenders.add(defender_3.trim());
                if (defender_4 != null && !defender_4.trim().isEmpty()) defenders.add(defender_4.trim());

                

                // Battle Object Creation
                Battle battle = new Battle();
                battle.setName(name);
                battle.setYear(parseInt(year));
                battle.setBattleNumber(parseInt(battleNumber));
                battle.setAttackerOutcome(attackerOutcome);
                battle.setBattleType(battleType);
                battle.setMajorDeath(parseInt(majorDeath));
                battle.setMajorCapture(parseInt(majorCapture));
                battle.setAttackerSize(parseInt(attackerSize));
                battle.setDefenderSize(parseInt(defenderSize));
                battle.setSummer(parseBool(summer));
                battle.setNote(note);

                String locationName = location != null ? location.trim().toLowerCase() : null;
                String regionName = region != null ? region.trim().toLowerCase() : null;

                // Location and Region Object Creation
                final Region regionObj;
                Long regionId = null;
                if (regionName != null && !regionName.isEmpty()) {
                    regionObj = regionRepo.findByName(regionName.toLowerCase())
                        .orElseGet(() -> {
                            Region newRegion = new Region();
                            newRegion.setName(regionName.toLowerCase());
                            return regionRepo.save(newRegion);
                        });
                    regionId = regionObj.getRegionId();
                } else {
                    regionObj = null;
                }

                Location locationObj = null;
                if (locationName != null && !locationName.isEmpty() && region != null) {
                    locationObj = locationRepo.findByNameAndRegionRegionId(locationName.toLowerCase(), regionId)
                        .orElseGet(() -> {
                            Location newLocation = new Location();
                            newLocation.setName(locationName.toLowerCase());
                            newLocation.setRegion(regionObj);
                            return locationRepo.save(newLocation);
                        });
                }

                battle.setLocation(locationObj);
                battle.setRegion(regionObj);
                
                battle = battleRepo.save(battle);

                // Add king, attacker, defender, commanders to the participant list
                if (attackerKing != null && !attackerKing.isEmpty()) {
                    insertParticipant(attackerKing, battle, BattleParticipant.Role.ATTACKER_KING);
                }
                if (defenderKing != null && !defenderKing.isEmpty()) {
                    insertParticipant(defenderKing, battle, BattleParticipant.Role.DEFENDER_KING);
                }

                for (String attacker : attackers) {
                    insertParticipant(attacker, battle, BattleParticipant.Role.ATTACKER);
                }
                for (String defender : defenders) {
                    insertParticipant(defender, battle, BattleParticipant.Role.DEFENDER);
                }

                for (String commander : attackerCommanders) {
                    insertParticipant(commander, battle, BattleParticipant.Role.COMMANDER_ATTACKER);
                }
                for (String commander : defenderCommanders) {
                    insertParticipant(commander, battle, BattleParticipant.Role.COMMANDER_DEFENDER);
                }

            }
        }
    }

    private void insertParticipant(String name, Battle battle, BattleParticipant.Role role) {
        if (name == null || name.trim().isEmpty()) return;

        Participant participant = participantRepo.findByName(name.trim().toLowerCase())
                .orElseGet(() -> {
                    Participant newParticipant = new Participant(name.trim().toLowerCase());
                    return participantRepo.save(newParticipant);
                });

        BattleParticipantId id = new BattleParticipantId();
        id.setBattleId(battle.getBattleId());
        id.setParticipantId(participant.getParticipantId());

        BattleParticipant bp = new BattleParticipant();
        bp.setId(id);
        bp.setBattle(battle);
        bp.setParticipant(participant);
        bp.setRole(role);
        bpRepo.save(bp);
    }

    private Integer parseInt(String val) {
        try {
            return val == null || val.trim().isEmpty() ? null : Integer.parseInt(val.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private Boolean parseBool(String val) {
        return val != null && (val.equalsIgnoreCase("true") || val.equals("1"));
    }
}
