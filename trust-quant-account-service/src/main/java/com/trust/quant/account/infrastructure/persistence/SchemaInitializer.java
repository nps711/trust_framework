package com.trust.quant.account.infrastructure.persistence;

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
            CREATE TABLE IF NOT EXISTS quant_account (
                id BIGINT PRIMARY KEY,
                account_id VARCHAR(64) NOT NULL UNIQUE,
                account_name VARCHAR(128),
                account_type VARCHAR(32),
                status VARCHAR(16),
                available_balance DECIMAL(20,6) DEFAULT 0,
                frozen_balance DECIMAL(20,6) DEFAULT 0,
                create_time TIMESTAMP,
                update_time TIMESTAMP
            )
            """);

        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM quant_account", Long.class);
        if (count == null || count == 0) {
            jdbcTemplate.update("""
                INSERT INTO quant_account (
                    id, account_id, account_name, account_type, status,
                    available_balance, frozen_balance, create_time, update_time
                ) VALUES (
                    1, 'ACC001', 'Test Account 1', 'INDIVIDUAL', 'ACTIVE',
                    1000000.000000, 0.000000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
                )
                """);
        }
    }
}
