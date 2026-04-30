package com.trust.audit.infrastructure.mq;

import com.trust.common.mq.event.AuditEventMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AuditLogConsumer {

    private final JdbcTemplate jdbcTemplate;

    public AuditLogConsumer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @RabbitListener(queues = "audit.log.queue")
    public void onAuditEvent(AuditEventMessage message) {
        jdbcTemplate.update(
                "INSERT INTO sys_audit_log (id, trace_id, module, action, biz_id, user_id, req_params, resp_result, status, cost_ms, ip, create_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                System.currentTimeMillis(),
                message.getTraceId(),
                message.getModule(),
                message.getAction(),
                message.getBizId(),
                message.getUserId(),
                message.getReqParams(),
                message.getRespResult(),
                message.getStatus(),
                message.getCostMs(),
                message.getIp(),
                LocalDateTime.now()
        );
    }
}
