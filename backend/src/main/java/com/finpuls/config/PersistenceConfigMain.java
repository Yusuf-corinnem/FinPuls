package com.finpuls.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
                "com.finpuls.domain.repository"
        },
        excludeFilters = {
                @org.springframework.context.annotation.ComponentScan.Filter(
                        type = org.springframework.context.annotation.FilterType.REGEX,
                        pattern = ".*\\.domain\\.repository\\.token\\..*"
                )
        },
        entityManagerFactoryRef = "mainEntityManagerFactory",
        transactionManagerRef = "mainTransactionManager"
)
public class PersistenceConfigMain {

    @Value("${DB_PG_URL}")
    private String url;

    @Value("${DB_PG_USERNAME}")
    private String username;

    @Value("${DB_PG_PASSWORD}")
    private String password;

    @Value("${DB_PG_DRIVER}")
    private String driverClassName;

    @Value("${DB_PG_PLATFORM:org.hibernate.dialect.PostgreSQLDialect}")
    private String hibernateDialect;

    @Primary
    @Bean(name = "mainDataSource")
    public DataSource mainDataSource() {
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .driverClassName(driverClassName)
                .build();
    }

    @Primary
    @Bean(name = "mainEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean mainEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        LocalContainerEntityManagerFactoryBean emf = builder
                .dataSource(mainDataSource())
                .packages("com.finpuls.domain.model")
                .persistenceUnit("main")
                .build();
        emf.getJpaPropertyMap().put("hibernate.dialect", hibernateDialect);
        return emf;
    }

    @Primary
    @Bean(name = "mainTransactionManager")
    public PlatformTransactionManager mainTransactionManager(LocalContainerEntityManagerFactoryBean mainEntityManagerFactory) {
        return new JpaTransactionManager(mainEntityManagerFactory.getObject());
    }
}


