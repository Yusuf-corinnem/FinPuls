package com.finpuls.api.exception;

/**
 * Исключение при работе с банковскими API
 */
public class BankApiException extends FinPulsException {
    
    private final String bankName;
    
    public BankApiException(String bankName, String message) {
        super("BANK_API_ERROR", String.format("Ошибка API банка [%s]: %s", bankName, message));
        this.bankName = bankName;
    }
    
    public BankApiException(String bankName, String message, Throwable cause) {
        super("BANK_API_ERROR", String.format("Ошибка API банка [%s]: %s", bankName, message), cause);
        this.bankName = bankName;
    }
    
    public BankApiException(String bankName, String message, Object context) {
        super("BANK_API_ERROR", String.format("Ошибка API банка [%s]: %s", bankName, message), context);
        this.bankName = bankName;
    }
    
    public String getBankName() {
        return bankName;
    }
}

