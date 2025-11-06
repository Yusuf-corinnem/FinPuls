package com.finpuls.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = {
                "com.finpuls.domain.repository.token"
        },
        entityManagerFactoryRef = "tokenEntityManagerFactory",
        transactionManagerRef = "tokenTransactionManager"
)
public class PersistenceConfigTokenH2 {

    @Value("${DB_H2_URL}")
    private String url;

    @Value("${DB_H2_USERNAME:sa}")
    private String username;

    @Value("${DB_H2_PASSWORD:}")
    private String password;

    @Value("${DB_H2_DRIVER:org.h2.Driver}")
    private String driverClassName;

    @Value("${DB_H2_PLATFORM:org.hibernate.dialect.H2Dialect}")
    private String hibernateDialect;

    @Bean(name = "tokenDataSource")
    public DataSource tokenDataSource() {
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .driverClassName(driverClassName)
                .build();
    }

    @Bean(name = "tokenEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean tokenEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        LocalContainerEntityManagerFactoryBean emf = builder
                .dataSource(tokenDataSource())
                .packages("com.finpuls.domain.model.token")
                .persistenceUnit("token")
                .build();
        emf.getJpaPropertyMap().put("hibernate.dialect", hibernateDialect);
        return emf;
    }

    @Bean(name = "tokenTransactionManager")
    public PlatformTransactionManager tokenTransactionManager(LocalContainerEntityManagerFactoryBean tokenEntityManagerFactory) {
        return new JpaTransactionManager(tokenEntityManagerFactory.getObject());
    }
}


