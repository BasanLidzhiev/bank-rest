package ru.lidzhiev.bankcards.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.info("Resource not found: {}", ex.getMessage());
        HttpStatus s = ex.getErrorCode().getHttpStatus();
        return ResponseEntity.status(s).body(ex.getMessage());
    }

    @ExceptionHandler(UserOperationException.class)
    public ResponseEntity<String> handleUserOperationException(UserOperationException ex) {
        log.warn("User operation error: {}", ex.getMessage());
        HttpStatus s = ex.getErrorCode().getHttpStatus();
        return ResponseEntity.status(s).body(ex.getMessage());
    }

    @ExceptionHandler(CardOperationException.class)
    public ResponseEntity<String> handleCardOperationException(CardOperationException ex) {
        log.warn("Card operation error: {}", ex.getMessage());
        HttpStatus s = ex.getErrorCode().getHttpStatus();
        return ResponseEntity.status(s).body(ex.getMessage());
    }
}
