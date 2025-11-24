package com.finpuls.config.h2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@Getter
@Setter
public class H2DataSourceProperties {
    @Value("${DB_H2_URL:jdbc:h2:mem:tokens;DB_CLOSE_DELAY=-1}")
    private String url;

    @Value("${DB_H2_USERNAME:finpuls}")
    private String username;

    @Value("${DB_H2_PASSWORD:finpuls}")
    private String password;

    @Value("${DB_H2_DBNAME:tokens}")
    private String databaseName;

    @Value("${DB_H2_DRIVER:org.h2.Driver}")
    private String driverClassName;

    @Value("${DB_H2_PLATFORM:org.hibernate.dialect.H2Dialect}")
    private String platform;

    @Value("${DB_H2_SHOW_SQL:false}")
    private Boolean showSql;

    @Value("${DB_H2_FORMAT_SQL:true}")
    private Boolean formatSql;
}
