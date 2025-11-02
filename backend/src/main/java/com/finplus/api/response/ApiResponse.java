package com.finplus.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Единый формат ответа API
 * @param <T> Тип данных в ответе
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    
    private ResponseStatus status;
    private T data;
    private String message;
    private ErrorDetails error;
    private String requestId;
    
    /**
     * Успешный ответ
     */
    public static <T> ApiResponse<T> success(T data, String message, String requestId) {
        return ApiResponse.<T>builder()
                .status(ResponseStatus.SUCCESS)
                .data(data)
                .message(message)
                .requestId(requestId)
                .build();
    }
    
    /**
     * Успешный ответ без сообщения
     */
    public static <T> ApiResponse<T> success(T data, String requestId) {
        return success(data, "Операция успешно выполнена", requestId);
    }
    
    /**
     * Ответ с ошибкой
     */
    public static <T> ApiResponse<T> error(String errorCode, String errorMessage, String requestId) {
        return error(errorCode, errorMessage, null, requestId);
    }
    
    /**
     * Ответ с ошибкой и контекстом
     */
    public static <T> ApiResponse<T> error(String errorCode, String errorMessage, Object context, String requestId) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .code(errorCode)
                .message(errorMessage)
                .context(context)
                .build();
        
        return ApiResponse.<T>builder()
                .status(ResponseStatus.ERROR)
                .data(null)
                .message("Операция завершилась с ошибкой")
                .error(errorDetails)
                .requestId(requestId)
                .build();
    }
}

