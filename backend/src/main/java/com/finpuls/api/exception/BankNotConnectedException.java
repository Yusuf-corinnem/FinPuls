package com.finpuls.api.exception;

/**
 * Исключение когда банк не подключен у пользователя
 */
public class BankNotConnectedException extends FinPulsException {
    
    private final String bankName;
    private final String userId;
    
    public BankNotConnectedException(String bankName, String userId) {
        super("BANK_NOT_CONNECTED", String.format("Банк %s не подключен для пользователя: %s", bankName, userId));
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

