package com.assignmentjava.config;

import com.assignmentjava.services.DotEnvService;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

//Config đối tượng đa thuộc tính

@Configuration
public class DataSourceConfig {
    @Autowired
    DotEnvService dotEnvService;

    @Bean("dataSource")
    public DataSource getDataSource(){
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(dotEnvService.getDotEnvValue("DB_CLASSNAME"));
        config.setJdbcUrl(dotEnvService.getDotEnvValue("DB_URL"));
        config.setUsername(dotEnvService.getDotEnvValue("DB_USERNAME"));
        config.setPassword(dotEnvService.getDotEnvValue("DB_PASSWORD"));
        return  new HikariDataSource(config);
    }
}
