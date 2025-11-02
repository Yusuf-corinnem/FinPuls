package com.finplus.api.exception;

/**
 * Исключение когда требуется Pro подписка для выполнения операции
 */
public class SubscriptionRequiredException extends FinPlusException {
    
    private final String feature;
    
    public SubscriptionRequiredException(String feature) {
        super("SUBSCRIPTION_REQUIRED", String.format("Для функции %s требуется Pro подписка", feature));
        this.feature = feature;
    }
    
    public SubscriptionRequiredException(String feature, String message) {
        super("SUBSCRIPTION_REQUIRED", message);
        this.feature = feature;
    }
    
    public String getFeature() {
        return feature;
    }
}

