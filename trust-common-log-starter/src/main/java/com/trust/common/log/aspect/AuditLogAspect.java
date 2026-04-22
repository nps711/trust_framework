package com.trust.common.log.aspect;

import com.trust.common.core.context.UserContextHolder;
import com.trust.common.log.annotation.AuditLog;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class AuditLogAspect {
    private static final Logger log = LoggerFactory.getLogger(AuditLogAspect.class);

    @Around("@annotation(auditLog)")
    public Object around(ProceedingJoinPoint joinPoint, AuditLog auditLog) throws Throwable {
        long start = System.currentTimeMillis();
        String traceId = UserContextHolder.getContext() == null ? "" : UserContextHolder.getContext().getTraceId();
        try {
            Object result = joinPoint.proceed();
            log.info("audit success module={} action={} bizId={} traceId={} costMs={}",
                    auditLog.module(), auditLog.action(), auditLog.bizId(), traceId, System.currentTimeMillis() - start);
            return result;
        } catch (Throwable ex) {
            log.error("audit fail module={} action={} bizId={} traceId={} costMs={}",
                    auditLog.module(), auditLog.action(), auditLog.bizId(), traceId, System.currentTimeMillis() - start, ex);
            throw ex;
        }
    }
}
