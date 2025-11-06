package com.finpuls.api.exception;

import java.util.Map;

/**
 * Исключение при ошибках валидации
 */
public class ValidationException extends FinPulsException {
    
    private final Map<String, String> validationErrors;
    
    public ValidationException(Map<String, String> validationErrors) {
        super("VALIDATION_ERROR", "Ошибка валидации");
        this.validationErrors = validationErrors;
    }
    
    public ValidationException(String field, String errorMessage) {
        super("VALIDATION_ERROR", String.format("Ошибка валидации поля '%s': %s", field, errorMessage));
        this.validationErrors = Map.of(field, errorMessage);
    }
    
    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }
}

