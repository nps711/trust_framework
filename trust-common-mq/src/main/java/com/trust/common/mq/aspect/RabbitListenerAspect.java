package com.trust.common.mq.aspect;

import com.trust.common.core.context.UserContext;
import com.trust.common.core.context.UserContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.amqp.core.Message;

import java.util.Map;

@Aspect
public class RabbitListenerAspect {

    private static final String HEADER_USER_ID = "x-user-id";
    private static final String HEADER_TRACE_ID = "x-trace-id";

    @Around("@annotation(org.springframework.amqp.rabbit.annotation.RabbitListener)")
    public Object aroundRabbitListener(ProceedingJoinPoint joinPoint) throws Throwable {
        Message message = extractMessage(joinPoint);
        if (message != null) {
            Map<String, Object> headers = message.getMessageProperties().getHeaders();
            String userId = headers.get(HEADER_USER_ID) instanceof String ? (String) headers.get(HEADER_USER_ID) : null;
            String traceId = headers.get(HEADER_TRACE_ID) instanceof String ? (String) headers.get(HEADER_TRACE_ID) : null;
            UserContextHolder.setContext(new UserContext(userId, traceId));
        }
        try {
            return joinPoint.proceed();
        } finally {
            UserContextHolder.clear();
        }
    }

    private Message extractMessage(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof Message) {
                return (Message) arg;
            }
        }
        return null;
    }
}
