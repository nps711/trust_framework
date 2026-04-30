package com.trust.common.mq.interceptor;

import com.trust.common.core.context.UserContext;
import com.trust.common.core.context.UserContextHolder;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;

public class ContextMessagePostProcessor implements MessagePostProcessor {

    private static final String HEADER_USER_ID = "x-user-id";
    private static final String HEADER_TRACE_ID = "x-trace-id";

    @Override
    public Message postProcessMessage(Message message) throws AmqpException {
        UserContext context = UserContextHolder.getContext();
        if (context != null) {
            if (context.getUserId() != null) {
                message.getMessageProperties().setHeader(HEADER_USER_ID, context.getUserId());
            }
            if (context.getTraceId() != null) {
                message.getMessageProperties().setHeader(HEADER_TRACE_ID, context.getTraceId());
            }
        }
        return message;
    }
}
