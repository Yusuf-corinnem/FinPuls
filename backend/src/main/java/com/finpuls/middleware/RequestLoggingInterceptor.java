package com.finpuls.middleware;

import com.finpuls.common.LoggingHelper;
import com.finpuls.common.RequestContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor для логирования входящих запросов
 */
@Slf4j
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            // Устанавливаем контекст для логирования
            String requestId = RequestContextHolder.getRequestId();
            LoggingHelper.setRequestId(requestId);
            LoggingHelper.setHttpContext(request);
            
            // Логируем входящий запрос
            log.info("Входящий запрос: {} {}", request.getMethod(), request.getRequestURI());
            
        } catch (Exception e) {
            log.warn("Ошибка в интерцепторе логирования запросов", e);
        }
        
        return true;
    }
    
    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex
    ) {
        try {
            // Логируем завершение запроса
            if (ex != null) {
                log.error("Запрос завершен с исключением: {} {}", request.getMethod(), request.getRequestURI(), ex);
            } else {
                log.info("Запрос завершен: {} {} - Статус: {}", 
                        request.getMethod(), 
                        request.getRequestURI(), 
                        response.getStatus());
            }
        } catch (Exception e) {
            log.warn("Ошибка в afterCompletion логирования запросов", e);
        } finally {
            // Очищаем MDC (контекст логирования)
            LoggingHelper.clear();
        }
    }
}

