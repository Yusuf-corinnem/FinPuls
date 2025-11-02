package com.finplus.middleware;

import com.finplus.api.response.ApiResponse;
import com.finplus.common.RequestContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * AOP аспект для автоматической обертки ответов контроллеров в ApiResponse
 */
@Slf4j
@Aspect
@Component
@Order(2)
public class ResponseWrapperAspect {
    
    /**
     * Обертка ответов методов контроллеров, которые возвращают не ApiResponse
     * Перехватывает только методы контроллеров, возвращающие ResponseEntity или обычные объекты
     */
    @Around("execution(* com.finplus.api.controller.*.*(..)) && " +
            "(@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping))")
    public Object wrapResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        
        // Если результат уже ApiResponse, возвращаем как есть
        if (result instanceof ApiResponse) {
            return result;
        }
        
        // Если результат ResponseEntity, не оборачиваем
        if (result instanceof org.springframework.http.ResponseEntity) {
            return result;
        }
        
        // Если результат null, возвращаем успешный ответ с null данными
        if (result == null) {
            return ApiResponse.success(null, RequestContextHolder.getRequestId());
        }
        
        // Оборачиваем в ApiResponse
        return ApiResponse.success(result, RequestContextHolder.getRequestId());
    }
}

