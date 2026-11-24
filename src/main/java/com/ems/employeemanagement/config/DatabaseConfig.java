package com.ems.employeemanagement.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.type.AnnotatedTypeMetadata;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    public static class RenderPostgresCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            String dbUrl = context.getEnvironment().getProperty("DATABASE_URL");
            if (dbUrl == null || dbUrl.isBlank()) {
                dbUrl = System.getenv("DATABASE_URL");
            }
            return dbUrl != null && !dbUrl.isBlank() && 
                   (dbUrl.startsWith("postgres://") || dbUrl.startsWith("postgresql://"));
        }
    }

    @Bean
    @Primary
    @Conditional(RenderPostgresCondition.class)
    public DataSource renderDataSource() {
        String dbUrl = System.getenv("DATABASE_URL");
        if (dbUrl == null || dbUrl.isBlank()) {
            dbUrl = System.getProperty("DATABASE_URL");
        }

        try {
            log.info("Detected Render DATABASE_URL. Configuring production PostgreSQL DataSource...");
            URI uri = new URI(dbUrl);

            String userInfo = uri.getUserInfo();
            String username = "";
            String password = "";
            if (userInfo != null && userInfo.contains(":")) {
                String[] parts = userInfo.split(":", 2);
                username = parts[0];
                password = parts[1];
            } else if (userInfo != null) {
                username = userInfo;
            }

            int port = uri.getPort() != -1 ? uri.getPort() : 5432;
            String path = uri.getPath();
            if (path.startsWith("/")) {
                path = path.substring(1);
            }

            String jdbcUrl = "jdbc:postgresql://" + uri.getHost() + ":" + port + "/" + path;
            log.info("Render PostgreSQL JDBC URL successfully configured: jdbc:postgresql://{}:{}/{}", 
                     uri.getHost(), port, path);

            return DataSourceBuilder.create()
                    .driverClassName("org.postgresql.Driver")
                    .url(jdbcUrl)
                    .username(username)
                    .password(password)
                    .build();
        } catch (Exception e) {
            log.error("Failed to parse Render DATABASE_URL: {}", dbUrl, e);
            throw new RuntimeException("Could not configure datasource from Render DATABASE_URL", e);
        }
    }
}
