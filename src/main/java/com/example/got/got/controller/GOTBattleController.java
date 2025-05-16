package com.example.got.got.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.got.got.dto.RegionLocationDto;
import com.example.got.got.service.GOTBattleService;
import com.example.got.got.dto.ApiResponseDto;
import com.example.got.got.dto.BattleCountRecordsDto;
import com.example.got.got.dto.BattleDetailsDto;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@RequestMapping("/got")
public class GOTBattleController {

    private static final Logger logger = LogManager.getLogger(GOTBattleController.class);

    @Autowired
    private GOTBattleService battleService;

    @GetMapping("/count")
    public ResponseEntity<ApiResponseDto<BattleCountRecordsDto>> count() {
        logger.info("Received request to get battle count");
        return ResponseEntity.ok(
                ApiResponseDto.success(battleService.getBattleCount(),
                        null));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponseDto<List<RegionLocationDto>>> listPlaces() {
        logger.info("Received request to list places");
        return ResponseEntity.ok(ApiResponseDto.success(battleService.listPlaces(), null));
    }

    @GetMapping("/battle")
    public ResponseEntity<ApiResponseDto<BattleDetailsDto>> getBattle(@RequestParam String name) {
        logger.info("Received request to get battle details for name: {}", name);
        if (name == null || name.trim().isEmpty()) {
            logger.warn("Battle name is missing in the request");
            return ResponseEntity.badRequest().body(ApiResponseDto.error("Battle name is required"));
        }

        return battleService.getBattleByName(name)
                .map(dto -> {
                    logger.info("Battle details found for name: {}", name);
                    return ResponseEntity.ok(ApiResponseDto.success(dto, "Battle details found"));
                })
                .orElseGet(() -> {
                    logger.warn("Battle not found for name: {}", name);
                    return ResponseEntity.badRequest().body(ApiResponseDto.error("Battle not found"));
                });
    }
}
