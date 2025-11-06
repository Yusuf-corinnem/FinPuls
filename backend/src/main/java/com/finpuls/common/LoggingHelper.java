package com.finpuls.common;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;

import java.util.Map;

/**
 * Вспомогательный класс для работы с MDC (Mapped Diagnostic Context) для логирования
 */
public class LoggingHelper {
    
    private static final String REQUEST_ID_KEY = "requestId";
    private static final String USER_ID_KEY = "userId";
    private static final String HTTP_METHOD_KEY = "http.method";
    private static final String HTTP_URI_KEY = "http.uri";
    private static final String HTTP_IP_KEY = "http.ip";
    private static final String DATA_KEY = "data";
    
    /**
     * Установить Request ID в MDC
     */
    public static void setRequestId(String requestId) {
        if (requestId != null) {
            MDC.put(REQUEST_ID_KEY, requestId);
        }
    }
    
    /**
     * Установить HTTP контекст из запроса
     */
    public static void setHttpContext(HttpServletRequest request) {
        if (request != null) {
            MDC.put(HTTP_METHOD_KEY, request.getMethod());
            MDC.put(HTTP_URI_KEY, request.getRequestURI());
            MDC.put(HTTP_IP_KEY, getClientIpAddress(request));
        }
    }
    
    /**
     * Установить User ID в MDC
     */
    public static void setUserId(String userId) {
        if (userId != null) {
            MDC.put(USER_ID_KEY, userId);
        }
    }
    
    /**
     * Установить дополнительные данные в MDC
     */
    public static void setData(Map<String, Object> data) {
        if (data != null && !data.isEmpty()) {
            // Можно сериализовать в JSON если нужно
            MDC.put(DATA_KEY, data.toString());
        }
    }
    
    /**
     * Очистить MDC
     */
    public static void clear() {
        MDC.clear();
    }
    
    /**
     * Получить IP адрес клиента из запроса
     */
    private static String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip != null ? ip.split(",")[0].trim() : "unknown";
    }
}

