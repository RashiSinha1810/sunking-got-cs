package com.example.got.got.exception;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.got.got.dto.ApiResponseDto;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR_PREFIX = "Error: ";

    @ExceptionHandler(CustomBadRequestException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleCustomBadRequestException(CustomBadRequestException ex) {
        return ResponseEntity.badRequest().body(ApiResponseDto.error(ERROR_PREFIX + ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<Object>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDto.error(ERROR_PREFIX + ex.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponseDto.error(ERROR_PREFIX + ex.getMessage()));
    }
}
