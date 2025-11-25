package com.finpuls.common.logger;

/**
 * Статусы для логирования
 */
public enum LogStatus {
    PROCESS("PROCESS"),
    SUCCESS("SUCCESS"),
    FAIL("FAIL"),
    WARNING("WARNING");

    private final String value;

    LogStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
