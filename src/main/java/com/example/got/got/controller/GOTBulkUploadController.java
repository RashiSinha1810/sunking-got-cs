package com.example.got.got.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.got.got.service.GOTDataIngestionService;
import com.example.got.got.dto.ApiResponseDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/bulk")
public class GOTBulkUploadController {

    @Autowired
    private GOTDataIngestionService ingestionService;

    @PostMapping("upload-battle-details")
    public ResponseEntity<ApiResponseDto<Object>> postBattleDetails(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uploaded file is empty");
            }

            ingestionService.ingestCsv(file);
            return ResponseEntity.ok(ApiResponseDto.success(null, "Data ingested successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponseDto.error("Error: " + e.getMessage()));
        }
    }

}
