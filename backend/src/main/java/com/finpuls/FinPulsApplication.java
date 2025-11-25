package com.finpuls;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.finpuls.common.logger.*;

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
    @Value("${SERVER_PORT:8080}")
    private String serverPort;

    private static final Logger logger = Logger.getLogger(FinPulsApplication.class);

    public static void main(String[] args) {
        logger.info("Старт сервера ...");

        SpringApplication.run(FinPulsApplication.class, args);

        logger.info("Сервер запущен", LogStatus.SUCCESS);
    }
}
