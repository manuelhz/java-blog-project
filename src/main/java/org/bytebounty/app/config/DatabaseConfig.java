package org.bytebounty.app.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.host}")
    private String host;

    @Value("${spring.datasource.port}")
    private String port;

    @Value("${spring.datasource.database}")
    private String database;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String mysqlPasswordFilePath;

    @Bean
    public DataSource dataSource() {
        String url = "jdbc:mysql://"+ host + ":" + port + "/" + ReadFile.readFile(database) + 
        "?createDatabaseIfNotExist=true&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false";
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(ReadFile.readFile(username));
        dataSource.setPassword(ReadFile.readFile(mysqlPasswordFilePath));
        return dataSource;
    }
}
