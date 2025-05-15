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

// TODO: Add authentication and authorization
// TODO: Add rate limiting
// TODO: Add logging
// TODO: Scale to AWS S3 and Lambda based upload process
// TODO: Add support for other file formats (e.g. JSON, EXCEL)
@RestController
@RequestMapping("/bulk")
public class GOTBulkUploadController {

    @Autowired
    private GOTDataIngestionService ingestionService;

    @PostMapping("bulk-upload")
    public ResponseEntity<ApiResponseDto<Object>> postS3(@RequestParam("file") MultipartFile file) {
        try {

            if (file.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uploaded file is empty");
            }

            if (!file.getOriginalFilename().endsWith(".csv")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uploaded file is not a CSV");
            }

            if (file.getSize() > 52428800) { // 50 MB
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uploaded file is too large");
            }

            // Validate headers
            ingestionService.validateHeaders(file);

            // Clear existing data
            ingestionService.truncateAllTables();

            // Ingest data
            ingestionService.ingestCsvWithBulk(file);

            return ResponseEntity.ok(ApiResponseDto.success(null, "Data ingested successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponseDto.error("Error: " + e.getMessage()));
        }

    }

    @PostMapping("normal-upload")
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
