package com.finpuls.common.logger;

import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import java.time.Instant;
import java.util.Map;

/**
 * Простая обертка над SLF4J Logger
 * logback-spring.xml уже настроен, просто заполняем MDC перед логированием
 * 
 * Использование:
 * Logger logger = Logger.getLogger(MyClass.class);
 * 
 * // Простое логирование
 * logger.info("Message");
 * 
 * // С builder (удобно!)
 * logger.info("Message", LogStatus.SUCCESS,
 * Logger.meta()
 * .data("userId", "123")
 * .data("username", "ivan")
 * .http("method", "POST")
 * .http("url", "/api/login")
 * .build()
 * );
 */
public class Logger {

    private final org.slf4j.Logger slf4jLogger;

    private Logger(org.slf4j.Logger slf4jLogger) {
        this.slf4jLogger = slf4jLogger;
    }

    public static Logger getLogger(Class<?> clazz) {
        return new Logger(LoggerFactory.getLogger(clazz));
    }

    /**
     * Создает builder для meta данных
     * Использование: Logger.meta().data("key", "value").build()
     */
    public static MetaBuilder meta() {
        return new MetaBuilder();
    }

    public void info(String message, LogStatus status, Map<String, Object> meta) {
        setMDC(meta);
        if (status != null)
            MDC.put("status", status.getValue());
        slf4jLogger.info(message);
        MDC.clear();
    }

    public void info(String message, Map<String, Object> meta) {
        info(message, null, meta);
    }

    public void info(String message, LogStatus status) {
        info(message, status, null);
    }

    public void info(String message) {
        info(message, null, null);
    }

    public void error(String message, LogStatus status, Map<String, Object> meta, Throwable error) {
        setMDC(meta);
        if (status != null)
            MDC.put("status", status.getValue());
        if (error != null) {
            slf4jLogger.error(message, error);
        } else {
            slf4jLogger.error(message);
        }
        MDC.clear();
    }

    public void error(String message, LogStatus status, Map<String, Object> meta) {
        error(message, status, meta, null);
    }

    public void error(String message, Throwable error) {
        error(message, null, null, error);
    }

    public void warn(String message, LogStatus status, Map<String, Object> meta) {
        setMDC(meta);
        if (status != null)
            MDC.put("status", status.getValue());
        slf4jLogger.warn(message);
        MDC.clear();
    }

    public void warn(String message, Map<String, Object> meta) {
        warn(message, null, meta);
    }

    public void warn(String message, LogStatus status) {
        warn(message, status, null);
    }

    public void debug(String message, LogStatus status, Map<String, Object> meta) {
        setMDC(meta);
        if (status != null)
            MDC.put("status", status.getValue());
        slf4jLogger.debug(message);
        MDC.clear();
    }

    public void debug(String message, Map<String, Object> meta) {
        debug(message, null, meta);
    }

    public void debug(String message, LogStatus status) {
        debug(message, status, null);
    }

    @SuppressWarnings("unchecked")
    private void setMDC(Map<String, Object> meta) {
        if (meta == null)
            return;

        // HTTP контекст
        if (meta.containsKey("http") && meta.get("http") instanceof Map) {
            Map<String, Object> http = (Map<String, Object>) meta.get("http");
            put("http.method", http.get("method"));
            put("http.url", http.get("url"));
            put("http.status", http.get("status"));
            put("http.durationMs", http.get("durationMs"));
            put("http.ip", http.get("ip"));
            put("http.userAgent", http.get("userAgent"));
            put("requestId", http.get("requestId"));
            if (!http.containsKey("createdAt")) {
                MDC.put("http.createdAt", Instant.now().toString());
            } else {
                put("http.createdAt", http.get("createdAt"));
            }
        }

        // Data контекст
        if (meta.containsKey("data") && meta.get("data") instanceof Map) {
            Map<String, Object> data = (Map<String, Object>) meta.get("data");
            data.forEach((k, v) -> {
                if (k != null && v != null)
                    MDC.put("data." + k, String.valueOf(v));
            });
        }
    }

    private void put(String key, Object value) {
        if (value != null) {
            MDC.put(key, String.valueOf(value));
        }
    }
}
