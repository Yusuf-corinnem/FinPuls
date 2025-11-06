package com.finpuls.api.exception;

/**
 * Базовое исключение приложения FinPuls
 */
public class FinPulsException extends RuntimeException {
    
    private final String errorCode;
    private final Object context;
    
    public FinPulsException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.context = null;
    }
    
    public FinPulsException(String errorCode, String message, Object context) {
        super(message);
        this.errorCode = errorCode;
        this.context = context;
    }
    
    public FinPulsException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.context = null;
    }
    
    public FinPulsException(String errorCode, String message, Object context, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.context = context;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public Object getContext() {
        return context;
    }
}

