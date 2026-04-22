package com.trust.common.core.context;

public class UserContext {
    private String userId;
    private String traceId;

    public UserContext() {
    }

    public UserContext(String userId, String traceId) {
        this.userId = userId;
        this.traceId = traceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}
