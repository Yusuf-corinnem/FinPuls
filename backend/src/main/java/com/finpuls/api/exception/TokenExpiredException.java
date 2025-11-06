package com.finpuls.api.exception;

/**
 * Исключение при истечении срока действия токена
 */
public class TokenExpiredException extends FinPulsException {
    
    private final String bankName;
    private final String userId;
    
    public TokenExpiredException(String bankName, String userId) {
        super("TOKEN_EXPIRED", String.format("Токен истек для банка: %s, пользователь: %s", bankName, userId));
        this.bankName = bankName;
        this.userId = userId;
    }
    
    public TokenExpiredException(String bankName, String userId, Object context) {
        super("TOKEN_EXPIRED", String.format("Токен истек для банка: %s, пользователь: %s", bankName, userId), context);
        this.bankName = bankName;
        this.userId = userId;
    }
    
    public String getBankName() {
        return bankName;
    }
    
    public String getUserId() {
        return userId;
    }
}

