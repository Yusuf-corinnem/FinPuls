package com.finplus.middleware;

import com.finplus.common.LoggingHelper;
import com.finplus.common.RequestContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Фильтр для генерации Request ID и логирования всех запросов (включая Actuator)
 */
@Slf4j
@Component
@Order(1)
public class RequestIdFilter extends OncePerRequestFilter {
    
    private static final String REQUEST_ID_HEADER = "X-Request-Id";
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Получаем Request ID из заголовка или генерируем новый
            String requestId = request.getHeader(REQUEST_ID_HEADER);
            if (requestId == null || requestId.isBlank()) {
                requestId = RequestContextHolder.getRequestId();
            } else {
                RequestContextHolder.setRequestId(requestId);
            }
            
            // Устанавливаем контекст для логирования
            LoggingHelper.setRequestId(requestId);
            LoggingHelper.setHttpContext(request);
            
            // Устанавливаем Request ID в заголовок ответа
            response.setHeader(REQUEST_ID_HEADER, requestId);
            
            // Получаем IP и User Agent
            String clientIp = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            
            // Логируем входящий запрос
            log.info("Входящий запрос: {} {} - IP: {} - User-Agent: {}", 
                    request.getMethod(), 
                    request.getRequestURI(),
                    clientIp,
                    userAgent != null ? userAgent : "не указан");
            
            // Продолжаем цепочку фильтров
            filterChain.doFilter(request, response);
            
            // Логируем завершение запроса
            long duration = System.currentTimeMillis() - startTime;
            log.info("Запрос завершен: {} {} - Статус: {} - Время выполнения: {}ms - IP: {}", 
                    request.getMethod(), 
                    request.getRequestURI(), 
                    response.getStatus(),
                    duration,
                    clientIp);
            
        } catch (Exception ex) {
            // Логируем ошибку
            long duration = System.currentTimeMillis() - startTime;
            String clientIp = getClientIpAddress(request);
            log.error("Ошибка обработки запроса: {} {} - Время выполнения: {}ms - IP: {}", 
                    request.getMethod(), 
                    request.getRequestURI(),
                    duration,
                    clientIp,
                    ex);
            throw ex;
        } finally {
            // Очищаем контекст после обработки запроса
            LoggingHelper.clear();
            RequestContextHolder.clear();
        }
    }
    
    /**
     * Получить IP адрес клиента из запроса
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // Если IP содержит несколько адресов (через прокси), берем первый
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip != null ? ip : "unknown";
    }
}

