package com.fasttime.domain.report.contoller;

import com.fasttime.domain.report.exception.AlreadyDeletedPostException;
import com.fasttime.domain.report.exception.DuplicateReportException;
import com.fasttime.global.util.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
class ReportRestControllerAdvice {

    @ExceptionHandler
    ResponseEntity<ResponseDTO> duplicateReportException(DuplicateReportException e) {
        log.error("DuplicateReportException: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ResponseDTO.res(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler
    ResponseEntity<ResponseDTO> alreadyDeletedPostException(AlreadyDeletedPostException e) {
        log.error("PostAlreadyDeletedException: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ResponseDTO.res(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler
    ResponseEntity<ResponseDTO> bindException(BindException e) {
        log.error("BindException: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ResponseDTO.res(HttpStatus.BAD_REQUEST,
                e.getBindingResult().getAllErrors().get(0).getDefaultMessage()));
    }

    @ExceptionHandler
    ResponseEntity<ResponseDTO> illegalArgException(IllegalArgumentException e) {
        log.error("IllegalArgumentException: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ResponseDTO.res(HttpStatus.BAD_REQUEST, e.getMessage()));
    }
}
