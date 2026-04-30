package com.trust.file.infrastructure.persistence;

import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SchemaInitializer {

    private final JdbcTemplate jdbcTemplate;

    public SchemaInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void init() {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS sys_file_record (
                    id BIGINT PRIMARY KEY,
                    file_id VARCHAR(64) NOT NULL UNIQUE,
                    file_name VARCHAR(256) NOT NULL,
                    original_name VARCHAR(256),
                    file_size BIGINT,
                    file_type VARCHAR(64),
                    storage_type VARCHAR(32) NOT NULL,
                    storage_path VARCHAR(512),
                    url VARCHAR(512),
                    create_by VARCHAR(64),
                    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """);
    }
}
