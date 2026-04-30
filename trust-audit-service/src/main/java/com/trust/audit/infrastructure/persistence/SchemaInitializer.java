package com.trust.audit.infrastructure.persistence;

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
                CREATE TABLE IF NOT EXISTS sys_audit_log (
                    id BIGINT PRIMARY KEY,
                    trace_id VARCHAR(64),
                    module VARCHAR(64) NOT NULL,
                    action VARCHAR(64) NOT NULL,
                    biz_id VARCHAR(64),
                    user_id VARCHAR(64),
                    req_params TEXT,
                    resp_result TEXT,
                    status VARCHAR(16),
                    cost_ms BIGINT,
                    ip VARCHAR(64),
                    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """);
    }
}
