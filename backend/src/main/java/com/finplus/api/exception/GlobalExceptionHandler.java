package com.finplus.api.exception;

import com.finplus.api.response.ApiResponse;
import com.finplus.common.RequestContextHolder;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Глобальный обработчик исключений
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Обработка FinPlusException и его наследников
     */
    @ExceptionHandler(FinPlusException.class)
    public ResponseEntity<ApiResponse<?>> handleFinPlusException(FinPlusException ex) {
        log.error("Исключение FinPlus: {} - {}", ex.getErrorCode(), ex.getMessage(), ex);
        
        String requestId = RequestContextHolder.getRequestId();
        ApiResponse<?> response = ApiResponse.error(
                ex.getErrorCode(),
                ex.getMessage(),
                ex.getContext(),
                requestId
        );
        
        HttpStatus status = determineHttpStatus(ex);
        return ResponseEntity.status(status).body(response);
    }
    
    /**
     * Обработка ValidationException
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(ValidationException ex) {
        log.warn("Ошибка валидации: {}", ex.getMessage());
        
        String requestId = RequestContextHolder.getRequestId();
        Map<String, Object> context = Map.of("validationErrors", ex.getValidationErrors());
        
        ApiResponse<?> response = ApiResponse.error(
                ex.getErrorCode(),
                ex.getMessage(),
                context,
                requestId
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * Обработка MethodArgumentNotValidException (валидация через @Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.warn("Ошибка валидации аргументов метода");
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage != null ? errorMessage : "Ошибка валидации");
        });
        
        String requestId = RequestContextHolder.getRequestId();
        Map<String, Object> context = Map.of("validationErrors", errors);
        
        ApiResponse<?> response = ApiResponse.error(
                "VALIDATION_ERROR",
                "Ошибка валидации",
                context,
                requestId
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * Обработка ConstraintViolationException
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("Нарушение ограничений валидации");
        
        Map<String, String> errors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage
                ));
        
        String requestId = RequestContextHolder.getRequestId();
        Map<String, Object> context = Map.of("validationErrors", errors);
        
        ApiResponse<?> response = ApiResponse.error(
                "VALIDATION_ERROR",
                "Ошибка валидации",
                context,
                requestId
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * Обработка всех остальных исключений
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGenericException(Exception ex) {
        log.error("Произошла неожиданная ошибка", ex);
        
        String requestId = RequestContextHolder.getRequestId();
        ApiResponse<?> response = ApiResponse.error(
                "INTERNAL_ERROR",
                "Произошла неожиданная ошибка",
                null,
                requestId
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * Определение HTTP статуса на основе типа исключения
     */
    private HttpStatus determineHttpStatus(FinPlusException ex) {
        if (ex instanceof TokenExpiredException) {
            return HttpStatus.UNAUTHORIZED;
        }
        if (ex instanceof BankNotConnectedException) {
            return HttpStatus.NOT_FOUND;
        }
        if (ex instanceof SubscriptionRequiredException) {
            return HttpStatus.FORBIDDEN;
        }
        if (ex instanceof BankApiException) {
            return HttpStatus.BAD_GATEWAY;
        }
        if (ex instanceof ValidationException) {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}

