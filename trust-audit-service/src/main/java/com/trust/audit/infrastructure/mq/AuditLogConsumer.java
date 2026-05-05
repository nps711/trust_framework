package com.trust.audit.infrastructure.mq;

import com.trust.audit.infrastructure.persistence.mapper.AuditLogMapper;
import com.trust.audit.infrastructure.persistence.model.AuditLog;
import com.trust.common.mq.event.AuditEventMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AuditLogConsumer {

    private final AuditLogMapper auditLogMapper;

    public AuditLogConsumer(AuditLogMapper auditLogMapper) {
        this.auditLogMapper = auditLogMapper;
    }

    @RabbitListener(queues = "audit.log.queue")
    public void onAuditEvent(AuditEventMessage message) {
        AuditLog log = new AuditLog();
        log.setId(System.currentTimeMillis());
        log.setTraceId(message.getTraceId());
        log.setModule(message.getModule());
        log.setAction(message.getAction());
        log.setBizId(message.getBizId());
        log.setUserId(message.getUserId());
        log.setReqParams(message.getReqParams());
        log.setRespResult(message.getRespResult());
        log.setStatus(message.getStatus());
        log.setCostMs(message.getCostMs());
        log.setIp(message.getIp());
        auditLogMapper.insert(log);
    }
}
