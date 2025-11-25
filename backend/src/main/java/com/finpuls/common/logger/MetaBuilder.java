package com.finpuls.common.logger;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Builder для создания meta объектов
 * Аналог: { data: {...}, http: {...} } в JavaScript
 * 
 * Использование:
 * Logger.meta()
 * .data("userId", "123")
 * .http("method", "POST")
 * .build()
 */
public class MetaBuilder {
    private final Map<String, Object> meta = new HashMap<>();
    private final Map<String, Object> data = new HashMap<>();
    private final Map<String, Object> http = new HashMap<>();

    /**
     * Добавляет data поле
     * Аналог: meta.data.userId = "123"
     */
    public MetaBuilder data(String key, Object value) {
        if (key != null && value != null) {
            data.put(key, value);
        }
        return this;
    }

    /**
     * Добавляет несколько data полей сразу
     */
    public MetaBuilder data(Map<String, Object> dataMap) {
        if (dataMap != null) {
            data.putAll(dataMap);
        }
        return this;
    }

    /**
     * Добавляет HTTP поле
     * Аналог: meta.http.method = "POST"
     */
    public MetaBuilder http(String key, Object value) {
        if (key != null && value != null) {
            http.put(key, value);
        }
        return this;
    }

    /**
     * Добавляет несколько HTTP полей сразу
     */
    public MetaBuilder http(Map<String, Object> httpMap) {
        if (httpMap != null) {
            http.putAll(httpMap);
        }
        return this;
    }

    /**
     * Устанавливает requestId (также добавляется в http)
     */
    public MetaBuilder requestId(String requestId) {
        if (requestId != null) {
            http.put("requestId", requestId);
        }
        return this;
    }

    /**
     * Строит финальный meta объект
     */
    public Map<String, Object> build() {
        if (!data.isEmpty()) {
            meta.put("data", new HashMap<>(data));
        }
        if (!http.isEmpty()) {
            // Автоматически добавляем createdAt, если не указан
            if (!http.containsKey("createdAt")) {
                http.put("createdAt", Instant.now().toString());
            }
            meta.put("http", new HashMap<>(http));
        }
        return meta;
    }
}
