package com.finpuls;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * FinPuls - Financial Pulse Monitor
 * 
 * Мультибанковское приложение для управления личными финансами
 * с проактивным мониторингом финансового здоровья.
 * 
 * @author FinPuls Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableFeignClients
public class FinPulsApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinPulsApplication.class, args);
    }
}

