package com.trust.common.rpc.interceptor;

import com.trust.common.core.context.UserContext;
import com.trust.common.core.context.UserContextHolder;
import feign.RequestInterceptor;
import feign.RequestTemplate;

public class ContextFeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        UserContext context = UserContextHolder.getContext();
        if (context != null) {
            if (context.getUserId() != null) {
                template.header("x-user-id", context.getUserId());
            }
            if (context.getTraceId() != null) {
                template.header("x-trace-id", context.getTraceId());
            }
        }
    }
}
