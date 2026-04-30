package com.trust.auth.infrastructure.persistence;

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
                CREATE TABLE IF NOT EXISTS sys_user (
                    id BIGINT PRIMARY KEY,
                    user_id VARCHAR(64) NOT NULL UNIQUE,
                    username VARCHAR(64) NOT NULL,
                    password VARCHAR(128) NOT NULL,
                    dept_id BIGINT,
                    status VARCHAR(16) DEFAULT 'ACTIVE',
                    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """);

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS sys_role (
                    id BIGINT PRIMARY KEY,
                    role_id VARCHAR(64) NOT NULL UNIQUE,
                    role_name VARCHAR(64) NOT NULL,
                    role_code VARCHAR(64) NOT NULL UNIQUE,
                    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """);

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS sys_user_role (
                    id BIGINT PRIMARY KEY,
                    user_id VARCHAR(64) NOT NULL,
                    role_id VARCHAR(64) NOT NULL,
                    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    UNIQUE (user_id, role_id)
                )
                """);

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS sys_dept (
                    id BIGINT PRIMARY KEY,
                    dept_id BIGINT NOT NULL UNIQUE,
                    parent_id BIGINT,
                    dept_name VARCHAR(64) NOT NULL,
                    ancestors VARCHAR(256),
                    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """);

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS sys_data_scope (
                    id BIGINT PRIMARY KEY,
                    scope_id VARCHAR(64) NOT NULL UNIQUE,
                    role_id VARCHAR(64) NOT NULL,
                    scope_type VARCHAR(32) NOT NULL,
                    dept_ids VARCHAR(512),
                    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """);

        // Seed data
        long now = System.currentTimeMillis();
        jdbcTemplate.update("INSERT INTO sys_user (id, user_id, username, password, dept_id, status) VALUES (?, ?, ?, ?, ?, ?) ON CONFLICT DO NOTHING",
                1, "admin", "admin", "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO", 1, "ACTIVE");
        jdbcTemplate.update("INSERT INTO sys_user (id, user_id, username, password, dept_id, status) VALUES (?, ?, ?, ?, ?, ?) ON CONFLICT DO NOTHING",
                2, "user01", "user01", "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO", 2, "ACTIVE");

        jdbcTemplate.update("INSERT INTO sys_role (id, role_id, role_name, role_code) VALUES (?, ?, ?, ?) ON CONFLICT DO NOTHING",
                1, "admin", "管理员", "ROLE_ADMIN");
        jdbcTemplate.update("INSERT INTO sys_role (id, role_id, role_name, role_code) VALUES (?, ?, ?, ?) ON CONFLICT DO NOTHING",
                2, "user", "普通用户", "ROLE_USER");

        jdbcTemplate.update("INSERT INTO sys_user_role (id, user_id, role_id) VALUES (?, ?, ?) ON CONFLICT DO NOTHING",
                1, "admin", "admin");
        jdbcTemplate.update("INSERT INTO sys_user_role (id, user_id, role_id) VALUES (?, ?, ?) ON CONFLICT DO NOTHING",
                2, "user01", "user");

        jdbcTemplate.update("INSERT INTO sys_dept (id, dept_id, parent_id, dept_name, ancestors) VALUES (?, ?, ?, ?, ?) ON CONFLICT DO NOTHING",
                1, 1, 0, "总部", "0");
        jdbcTemplate.update("INSERT INTO sys_dept (id, dept_id, parent_id, dept_name, ancestors) VALUES (?, ?, ?, ?, ?) ON CONFLICT DO NOTHING",
                2, 2, 1, "量化交易部", "0,1");
        jdbcTemplate.update("INSERT INTO sys_dept (id, dept_id, parent_id, dept_name, ancestors) VALUES (?, ?, ?, ?, ?) ON CONFLICT DO NOTHING",
                3, 3, 1, "资产管理部", "0,1");

        jdbcTemplate.update("INSERT INTO sys_data_scope (id, scope_id, role_id, scope_type, dept_ids) VALUES (?, ?, ?, ?, ?) ON CONFLICT DO NOTHING",
                1, "1", "admin", "ALL", null);
        jdbcTemplate.update("INSERT INTO sys_data_scope (id, scope_id, role_id, scope_type, dept_ids) VALUES (?, ?, ?, ?, ?) ON CONFLICT DO NOTHING",
                2, "2", "user", "CUSTOM", "2");
    }
}
