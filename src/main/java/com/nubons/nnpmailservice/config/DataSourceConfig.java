package com.nubons.nnpmailservice.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;



//@Configuration
@Slf4j
public class DataSourceConfig {
	
	@Value("${spring.datasource.driverClassName}")
	private String driverClassName ;
	
	@Value("${spring.datasource.url}")
	private String dataSourceUrl ;
	
	@Value("${spring.datasource.username}")
	private String dataSourceUsername ;
	
	@Value("${spring.datasource.password}")
	private String dataSourcePassword ;
	
	@Value("${spring.datasource.hikari.maximum-pool-size}")
	private int dataSourcePoolSize ;
	
	
	
	@Bean
    public DataSource getDataSource() {
		
		// log.info("DataSource Details");
		// log.info(String.format("Driver : %s URL : %s Username : %s ", driverClassName, dataSourceUrl, dataSourceUsername));
		
        return DataSourceBuilder.create()
          .driverClassName(driverClassName)
          .url(dataSourceUrl)
          .username(dataSourceUsername)
          .password(dataSourcePassword)
          .build();	
    }


}
