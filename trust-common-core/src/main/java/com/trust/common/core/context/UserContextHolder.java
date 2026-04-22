package com.trust.common.core.context;

import com.alibaba.ttl.TransmittableThreadLocal;

public final class UserContextHolder {
    private static final ThreadLocal<UserContext> CONTEXT = new TransmittableThreadLocal<>();

    private UserContextHolder() {
    }

    public static void setContext(UserContext context) {
        CONTEXT.set(context);
    }

    public static UserContext getContext() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
