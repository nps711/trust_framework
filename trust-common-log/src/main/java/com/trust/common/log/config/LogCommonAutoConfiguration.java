package com.trust.common.log.config;

import com.trust.common.log.aspect.AuditLogAspect;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class LogCommonAutoConfiguration {
    @Bean
    public AuditLogAspect auditLogAspect() {
        return new AuditLogAspect();
    }
}
