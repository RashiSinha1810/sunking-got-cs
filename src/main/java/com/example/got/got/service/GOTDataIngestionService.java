package com.example.got.got.service;

import com.example.got.got.model.*;
import com.example.got.got.repository.*;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.*;

@Service
public class GOTDataIngestionService {

    @Autowired
    private BattleRepository battleRepo;
    @Autowired
    private ParticipantRepository participantRepo;
    @Autowired
    private BattleParticipantRepository bpRepo;
    @Autowired
    private RegionRepository regionRepo;
    @Autowired
    private LocationRepository locationRepo;
    @Autowired
    private BattleLocationRepository blRepo;

    private BattleParticipant.Role resolveRole(String columnName) {
        switch (columnName.toLowerCase()) {
            case "attacker_king":
                return BattleParticipant.Role.ATTACKER_KING;
            case "defender_king":
                return BattleParticipant.Role.DEFENDER_KING;
            case "attacker_commander":
                return BattleParticipant.Role.COMMANDER_ATTACKER;
            case "defender_commander":
                return BattleParticipant.Role.COMMANDER_DEFENDER;
            case "attacker_1":
            case "attacker_2":
            case "attacker_3":
            case "attacker_4":
                return BattleParticipant.Role.ATTACKER;
            case "defender_1":
            case "defender_2":
            case "defender_3":
            case "defender_4":
                return BattleParticipant.Role.DEFENDER;
            default:
                throw new IllegalArgumentException("Unknown participant role column: " + columnName);
        }
    }

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

                // Attacker and Defender are single for each side. Need to join the attacker_1,
                // attacker_2, etc. fields into List.
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

                if (attacker_1 != null && !attacker_1.trim().isEmpty())
                    attackers.add(attacker_1.trim());
                if (attacker_2 != null && !attacker_2.trim().isEmpty())
                    attackers.add(attacker_2.trim());
                if (attacker_3 != null && !attacker_3.trim().isEmpty())
                    attackers.add(attacker_3.trim());
                if (attacker_4 != null && !attacker_4.trim().isEmpty())
                    attackers.add(attacker_4.trim());

                if (defender_1 != null && !defender_1.trim().isEmpty())
                    defenders.add(defender_1.trim());
                if (defender_2 != null && !defender_2.trim().isEmpty())
                    defenders.add(defender_2.trim());
                if (defender_3 != null && !defender_3.trim().isEmpty())
                    defenders.add(defender_3.trim());
                if (defender_4 != null && !defender_4.trim().isEmpty())
                    defenders.add(defender_4.trim());

                // Battle Object Creation
                Battle battle = new Battle();
                battle.setName(name);
                battle.setYear(parseInt(year));
                battle.setBattleNumber(Long.getLong(battleNumber));
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

                List<Location> locationObjs = new ArrayList<>();
                if (locationName != null && !locationName.isEmpty() && region != null) {
                    String[] locationNames = locationName.split(",");
                    for (String loc : locationNames) {
                        String trimmedLoc = loc.trim().toLowerCase();
                        if (!trimmedLoc.isEmpty()) {
                            Location locationObj = locationRepo.findByNameAndRegionRegionId(trimmedLoc, regionId)
                                    .orElseGet(() -> {
                                        Location newLocation = new Location();
                                        newLocation.setName(trimmedLoc);
                                        newLocation.setRegion(regionObj);
                                        return locationRepo.save(newLocation);
                                    });
                            locationObjs.add(locationObj);
                        }
                    }
                }

                battle.setRegion(regionObj);

                battle = battleRepo.save(battle);

                // Add locations to the battle
                for (Location locationObj : locationObjs) {
                    BattleLocation bl = new BattleLocation();
                    BattleLocationId blId = new BattleLocationId();
                    blId.setBattleId(battle.getBattleNumber());
                    blId.setLocationId(locationObj.getLocationId());
                    bl.setId(blId);
                    bl.setBattle(battle);
                    bl.setLocation(locationObj);
                    blRepo.save(bl);
                }

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

    public void validateHeaders(MultipartFile file) throws Exception {
        List<String> requiredHeaders = Arrays.asList(
                "name",
                "year",
                "battle_number",
                "attacker_king",
                "defender_king",
                "attacker_1",
                "attacker_2",
                "attacker_3",
                "attacker_4",
                "defender_1",
                "defender_2",
                "defender_3",
                "defender_4",
                "attacker_outcome",
                "battle_type",
                "major_death",
                "major_capture",
                "attacker_size",
                "defender_size",
                "attacker_commander",
                "defender_commander",
                "summer",
                "location",
                "region",
                "note");

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] header = reader.readNext();

            if (header == null) {
                throw new IllegalArgumentException("The file is empty or does not contain a header row.");
            }

            Set<String> headerSet = new HashSet<>();
            for (String h : header) {
                headerSet.add(h.trim().toLowerCase());
            }

            for (String requiredHeader : requiredHeaders) {
                if (!headerSet.contains(requiredHeader.toLowerCase())) {
                    throw new IllegalArgumentException("Missing required header: " + requiredHeader);
                }
            }

            String[] row = reader.readNext();

            if (row == null) {
                throw new IllegalArgumentException("The file is empty or does not contain data rows.");
            }
        }
    }

    public void truncateAllTables() {
        bpRepo.deleteAllInBatch();
        blRepo.deleteAllInBatch();
        battleRepo.deleteAllInBatch();
        participantRepo.deleteAllInBatch();
        locationRepo.deleteAllInBatch();
        regionRepo.deleteAllInBatch();
    }

    public void ingestCsvWithBulk(MultipartFile file) throws Exception {
        List<Region> regionList = new ArrayList<>();
        List<Location> locationList = new ArrayList<>();
        List<Participant> participantList = new ArrayList<>();
        List<Battle> battleList = new ArrayList<>();
        List<BattleParticipant> bpList = new ArrayList<>();
        List<BattleLocation> blList = new ArrayList<>();

        Map<String, Region> regionMap = new HashMap<>();
        Map<String, Location> locationMap = new HashMap<>();
        Map<String, Participant> participantMap = new HashMap<>();
        Map<String, Integer> headerIndexMap = new HashMap<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] header = reader.readNext(); // skip header
            for (int i = 0; i < header.length; i++) {
                headerIndexMap.put(header[i].trim().toLowerCase(), i);
            }

            String[] row;
            while ((row = reader.readNext()) != null) {
                String regionName = get(row, headerIndexMap, "region");
                String locationName = get(row, headerIndexMap, "location");

                if (regionName != null && !regionMap.containsKey(regionName)) {
                    Region region = new Region();
                    region.setRegionId(regionList.size() + 1L);
                    region.setName(regionName);
                    regionList.add(region);
                    regionMap.put(regionName, region);
                }

                if (locationName != null && regionName != null) {
                    String[] locationNames = locationName.split(",");
                    for (String loc : locationNames) {
                        String trimmedLoc = loc.trim();
                        if (!trimmedLoc.isEmpty()) {
                            String locKey = (trimmedLoc + "|" + regionName).toLowerCase();
                            if (!locationMap.containsKey(locKey)) {
                                Location location = new Location();
                                location.setLocationId(locationList.size() + 1L);
                                location.setName(trimmedLoc);
                                location.setRegion(regionMap.get(regionName));
                                locationList.add(location);
                                locationMap.put(locKey, location);
                            }
                        }
                    }
                }

                String[] roles = { "attacker_king", "defender_king", "attacker_commander", "defender_commander",
                        "attacker_1", "attacker_2", "attacker_3", "attacker_4",
                        "defender_1", "defender_2", "defender_3", "defender_4" };

                for (String role : roles) {
                    String val = get(row, headerIndexMap, role);
                    if (val != null) {
                        for (String part : val.split(",")) {
                            String p = part.trim().toLowerCase();
                            if (!p.isEmpty() && !participantMap.containsKey(p)) {
                                Participant participant = new Participant();
                                participant.setParticipantId(participantList.size() + 1L);
                                participant.setName(p);
                                participantList.add(participant);
                                participantMap.put(p, participant);
                            }
                        }
                    }
                }
            }

            regionRepo.saveAll(regionList);
            locationRepo.saveAll(locationList);
            participantRepo.saveAll(participantList);

            // reload to get managed state
            regionMap.replaceAll((k, v) -> regionRepo.findById(v.getRegionId()).orElse(null));
            locationMap.replaceAll((k, v) -> locationRepo.findById(v.getLocationId()).orElse(null));
            participantMap.replaceAll((k, v) -> participantRepo.findById(v.getParticipantId()).orElse(null));

            // reset and re-read CSV
            reader.close();

        }

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {

            reader.readNext(); // skip header again
            String[] row;

            while ((row = reader.readNext()) != null) {
                String name = get(row, headerIndexMap, "name");
                String regionName = get(row, headerIndexMap, "region");

                Battle battle = new Battle();
                battle.setName(name);
                battle.setYear(parseInt(get(row, headerIndexMap, "year")));
                String battleNumber = get(row, headerIndexMap, "battle_number");
                Long battleNum = Long.parseLong(battleNumber);
                if (battleNumber == null || battleNumber.trim().isEmpty()) {
                    battleNumber = String.valueOf(battleList.size() + 1);
                    Random random = new Random();
                    battleNum = random.nextLong();
                }
                battle.setBattleNumber(battleNum);
                battle.setAttackerOutcome(get(row, headerIndexMap, "attacker_outcome"));
                battle.setBattleType(get(row, headerIndexMap, "battle_type"));
                battle.setMajorDeath(parseInt(get(row, headerIndexMap, "major_death")));
                battle.setMajorCapture(parseInt(get(row, headerIndexMap, "major_capture")));
                battle.setAttackerSize(parseInt(get(row, headerIndexMap, "attacker_size")));
                battle.setDefenderSize(parseInt(get(row, headerIndexMap, "defender_size")));
                battle.setSummer(parseBool(get(row, headerIndexMap, "summer")));
                battle.setNote(get(row, headerIndexMap, "note"));
                battle.setRegion(regionMap.get(regionName));
                // battle.setLocation(locationMap.get(locKey));
                battleList.add(battle);

                String[] roles = { "attacker_king", "defender_king", "attacker_commander", "defender_commander",
                        "attacker_1", "attacker_2", "attacker_3", "attacker_4",
                        "defender_1", "defender_2", "defender_3", "defender_4" };

                for (String role : roles) {
                    String val = get(row, headerIndexMap, role);
                    if (val != null) {
                        for (String part : val.split(",")) {
                            String p = part.trim().toLowerCase();
                            if (!p.isEmpty()) {
                                Participant participant = participantMap.get(p);
                                BattleParticipant bp = new BattleParticipant();
                                BattleParticipantId bpId = new BattleParticipantId();
                                bpId.setBattleId(battle.getBattleNumber());
                                bpId.setParticipantId(participant.getParticipantId());
                                bp.setId(bpId);
                                bp.setBattle(battle);
                                bp.setParticipant(participant);
                                bp.setRole(resolveRole(role));
                                bpList.add(bp);
                            }
                        }
                    }
                }

                String locationName = get(row, headerIndexMap, "location");

                if (locationName != null && regionName != null) {
                    String[] locationNames = locationName.split(",");
                    for (String loc : locationNames) {
                        String trimmedLoc = loc.trim();
                        if (!trimmedLoc.isEmpty()) {
                            String locKey = (trimmedLoc + "|" + regionName).toLowerCase();
                            if (locationMap.containsKey(locKey)) {
                                Location location = locationMap.get(locKey);
                                if (location != null) {
                                    BattleLocation bl = new BattleLocation();
                                    BattleLocationId blId = new BattleLocationId();
                                    blId.setBattleId(battle.getBattleNumber());
                                    blId.setLocationId(location.getLocationId());
                                    bl.setId(blId);
                                    bl.setLocation(location);
                                    bl.setBattle(battle);
                                    blList.add(bl);
                                }
                            }
                        }
                    }
                }
            }

            battleRepo.saveAll(battleList);
            bpRepo.saveAll(bpList);
            blRepo.saveAll(blList);
            reader.close();
        }
    }

    private void insertParticipant(String name, Battle battle, BattleParticipant.Role role) {
        if (name == null || name.trim().isEmpty())
            return;

        Participant participant = participantRepo.findByName(name.trim().toLowerCase())
                .orElseGet(() -> {
                    Participant newParticipant = new Participant(name.trim().toLowerCase());
                    return participantRepo.save(newParticipant);
                });

        // BattleParticipantId id = new BattleParticipantId();
        // id.setBattleId(battle.getBattleId());
        // id.setParticipantId(participant.getParticipantId());

        BattleParticipant bp = new BattleParticipant();
        // bp.setId(id);
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
