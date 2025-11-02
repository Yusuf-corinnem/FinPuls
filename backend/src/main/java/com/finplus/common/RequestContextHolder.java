package com.finplus.common;

import java.util.UUID;

/**
 * Хранит контекст запроса в ThreadLocal
 */
public class RequestContextHolder {
    
    private static final ThreadLocal<String> REQUEST_ID = new ThreadLocal<>();
    
    /**
     * Установить Request ID
     */
    public static void setRequestId(String requestId) {
        REQUEST_ID.set(requestId);
    }
    
    /**
     * Получить Request ID или сгенерировать новый
     */
    public static String getRequestId() {
        String requestId = REQUEST_ID.get();
        if (requestId == null) {
            requestId = generateRequestId();
            setRequestId(requestId);
        }
        return requestId;
    }
    
    /**
     * Очистить контекст запроса
     */
    public static void clear() {
        REQUEST_ID.remove();
    }
    
    /**
     * Генерация уникального Request ID
     */
    private static String generateRequestId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

