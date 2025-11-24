package com.finpuls;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * FinPuls - Financial Pulse Monitor
 * 
 * Финансовое приложение с изолированным банковским модулем.
 * Backend выступает в роли API Gateway между клиентом и банком.
 * 
 * @author FinPuls Team
 * @version 2.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
public class FinPulsApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinPulsApplication.class, args);
    }
}
