package com.finpuls.api.response;

/**
 * Статус ответа API
 */
public enum ResponseStatus {
    SUCCESS("success"),
    ERROR("error");
    
    private final String value;
    
    ResponseStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}

