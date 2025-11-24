package com.finpuls.config.h2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.finpuls.domain.repository.token", entityManagerFactoryRef = "tokenEntityManagerFactory", transactionManagerRef = "tokenTransactionManager")
public class PersistenceConfigTokenH2 {
    @Autowired
    private H2DataSourceProperties h2DataSourceProperties;

    @Bean(name = "tokenDataSource")
    public DataSource tokenDataSource() {
        HikariDataSource hikariDataSource = new HikariDataSource();

        hikariDataSource.setJdbcUrl(h2DataSourceProperties.getUrl());
        hikariDataSource.setUsername(h2DataSourceProperties.getUsername());
        hikariDataSource.setPassword(h2DataSourceProperties.getPassword());
        hikariDataSource.setDriverClassName(h2DataSourceProperties.getDriverClassName());

        hikariDataSource.setMaximumPoolSize(5);
        hikariDataSource.setMinimumIdle(2);
        hikariDataSource.setConnectionTimeout(20000);
        hikariDataSource.setIdleTimeout(30000);
        hikariDataSource.setMaxLifetime(60000);

        return hikariDataSource;
    }

    @SuppressWarnings("null")
    @Bean(name = "tokenEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean tokenEntityManagerFactory(@Autowired DataSource tokenDataSource) {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();

        entityManagerFactoryBean.setDataSource(tokenDataSource);
        entityManagerFactoryBean.setPackagesToScan("com.finpuls.domain.entity.token");
        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        // Настройки Hibernate
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", h2DataSourceProperties.getPlatform());
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.show_sql", h2DataSourceProperties.getShowSql());
        properties.put("hibernate.format_sql", h2DataSourceProperties.getFormatSql());

        entityManagerFactoryBean.setJpaPropertyMap(properties);

        return entityManagerFactoryBean;
    }

    @Bean(name = "tokenTransactionManager")
    public PlatformTransactionManager tokenTransactionManager(@Autowired DataSource tokenDataSource) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();

        transactionManager.setEntityManagerFactory(
                tokenEntityManagerFactory(tokenDataSource()).getObject());

        return transactionManager;
    }
}
