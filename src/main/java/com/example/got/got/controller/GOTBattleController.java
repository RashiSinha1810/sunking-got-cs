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

@RestController
@RequestMapping("/got")
public class GOTBattleController {

    @Autowired
    private GOTBattleService battleService;

    @GetMapping("/count")
    public ResponseEntity<ApiResponseDto<BattleCountRecordsDto>> count() {
        return ResponseEntity.ok(
                ApiResponseDto.success(battleService.getBattleCount(),
                        null));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponseDto<List<RegionLocationDto>>> listPlaces() {
        List<RegionLocationDto> places = battleService.listPlaces();
        return ResponseEntity.ok(ApiResponseDto.success(places, null));
    }

    @GetMapping("/battle")
    public ResponseEntity<ApiResponseDto<BattleDetailsDto>> getBattle(@RequestParam String name) {
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponseDto.error("Battle name is required"));
        }

        return battleService.getBattleByName(name)
                .map(dto -> ResponseEntity.ok(ApiResponseDto.success(dto, "Battle details found")))
                .orElse(ResponseEntity.badRequest().body(ApiResponseDto.error("Battle not found")));
    }
}
