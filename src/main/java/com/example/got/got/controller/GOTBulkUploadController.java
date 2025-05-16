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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private static final Logger logger = LogManager.getLogger(GOTBulkUploadController.class);

    @PostMapping("bulk-upload")
    public ResponseEntity<ApiResponseDto<Object>> postS3(@RequestParam("file") MultipartFile file) {
        logger.info("Received bulk-upload request");
        try {
            if (file.isEmpty()) {
                logger.warn("Uploaded file is empty");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uploaded file is empty");
            }

            if (!file.getOriginalFilename().endsWith(".csv")) {
                logger.warn("Uploaded file is not a CSV");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uploaded file is not a CSV");
            }

            if (file.getSize() > 52428800) { // 50 MB
                logger.warn("Uploaded file is too large");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uploaded file is too large");
            }

            logger.info("Validating headers");
            ingestionService.validateHeaders(file);

            logger.info("Clearing existing data");
            ingestionService.truncateAllTables();

            logger.info("Ingesting data");
            ingestionService.ingestCsvWithBulk(file);

            logger.info("Data ingested successfully");
            return ResponseEntity.ok(ApiResponseDto.success(null, "Data ingested successfully."));
        } catch (Exception e) {
            logger.error("Error during bulk-upload: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponseDto.error("Error: " + e.getMessage()));
        }

    }

    @PostMapping("normal-upload")
    public ResponseEntity<ApiResponseDto<Object>> postBattleDetails(@RequestParam("file") MultipartFile file) {
        logger.info("Received normal-upload request");
        try {
            if (file.isEmpty()) {
                logger.warn("Uploaded file is empty");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uploaded file is empty");
            }

            logger.info("Ingesting data");
            ingestionService.ingestCsv(file);

            logger.info("Data ingested successfully");
            return ResponseEntity.ok(ApiResponseDto.success(null, "Data ingested successfully."));
        } catch (Exception e) {
            logger.error("Error during normal-upload: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponseDto.error("Error: " + e.getMessage()));
        }
    }

}
